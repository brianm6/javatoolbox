package toolbox.plugin.jdbc;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import org.jedit.syntax.TSQLTokenMarker;

import toolbox.jedit.JEditActions;
import toolbox.jedit.JEditPopupMenu;
import toolbox.jedit.JEditTextArea;
import toolbox.jedit.SQLDefaults;
import toolbox.plugin.jdbc.action.BenchmarkAction;
import toolbox.plugin.jdbc.action.ExecuteAllAction;
import toolbox.plugin.jdbc.action.ExecuteCurrentAction;
import toolbox.plugin.jdbc.action.ExecutePriorAction;
import toolbox.plugin.jdbc.action.FormatSQLAction;
import toolbox.plugin.jdbc.action.ListColumnsAction;
import toolbox.plugin.jdbc.action.ListTablesAction;
import toolbox.plugin.jdbc.action.SQLReferenceAction;
import toolbox.plugin.jdbc.action.ShowResultsFilterAction;
import toolbox.util.ClassUtil;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;
import toolbox.util.JDBCSession;
import toolbox.util.JDBCUtil;
import toolbox.util.PreferencedUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.db.SQLFormatter;
import toolbox.util.db.SQLFormatterView;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JConveyorMenu;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartPopupMenu;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.table.JSmartTable;
import toolbox.util.ui.table.TableSorter;
import toolbox.util.ui.textarea.action.ClearAction;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;

/**
 * Simple SQL Plugin that allows you to query and update a database via JDBC.
 * <p>
 * Features:
 * <ul>
 *  <li>Remembers past queries so you don't have to type them in again
 *      (activated by a right mouse click in the SQL text area).
 *  <li>Output is in plain text aligned by columns. Great for copy and paste to
 *      other applications.
 *  <li>SQL statements can span multiple lines but must be terminated by a 
 *      semicolon.
 *  <li>Results can be filtered "as you type" by regular expressions.
 *  <li>Query results can be viewed in either a sortable table or a freeform
 *      textarea.
 *  <li>JDBC benchmark is built in.
 *  <li>SQL formatter with many formatting options.
 *  <li>Ability to execute a single SQL statement or a group of SQL statements
 *      in batch mode.
 *  <li>Multiple JDBC sessions using different JDBC drivers can be active
 *      simultaneously.
 * </ul>
 *
 * Shortcuts:
 * <p>
 * <table border=1>
 *   <tr>
 *      <th>Key</th>
 *      <th>Function</th>
 *   </tr>
 *   <tr>
 *      <td>Ctrl-Enter</td>
 *      <td>Executes all SQL statements</td>
 *   </tr>
 *   <tr>
 *      <td>Ctrl-Shift-Enter (nothing selected)</td>
 *      <td>Execute the SQL statement starting on the current line</td>
 *   </tr>
 *   <tr>
 *      <td>Ctrl-Shift-Enter (text selected)</td>
 *      <td>Executes the selected SQL statement</td>
 *   </tr>
 *   <tr>
 *      <td>Ctrl-Shift-F</td>
 *      <td>Format the active SQL statement(s)</td>
 *   </tr>
 *   <tr>
 *      <td>Ctrl-Up</td>
 *      <td>Scroll up in SQL history</td>
 *   </tr>
 *   <tr>
 *      <td>Ctrl-Down</td>
 *      <td>Scroll down in SQL history</td>
 *   </tr>
 * </table>
 * 
 * @see toolbox.util.JDBCUtil
 */
public class QueryPlugin extends JPanel implements IPlugin
{
    public static final Logger logger_ = Logger.getLogger(QueryPlugin.class);

    //--------------------------------------------------------------------------
    // XML Constants
    //--------------------------------------------------------------------------

    /**
     * Root preferences element for the query plugin.
     */
    public static final String NODE_QUERY_PLUGIN = "QueryPlugin";

    /**
     * Attribute of QueryPlugin that stores the max number of entries in the sql
     * history popup menu before getting truncated.
     */
    public static final String ATTR_HISTORY_MAX = "maxHistory";

    /**
     * Child of QueryPlugin that contains a single "remembered" SQL stmt.
     */
    public static final String NODE_HISTORY_ITEM = "HistoryItem";

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Terminator character for SQL statements is a semicolon.
     */
    public static final char SQL_TERMINATOR = ';';
    
    /**
     * The number of lines that must be contained in a given resultset before
     * the output textarea will autoscroll to the end.
     */
    public static final int AUTO_SCROLL_THRESHOLD = 50;
    
    /**
     * List of javabean properties that are persisted.
     */
    public static final String[] SAVED_PROPS = 
        new String[] {"sendErrorToConsole"};

    /**
     * Name of the card in the results panel associated with the results text 
     * area.
     */
    private static final String CARD_TEXTAREA = "resultsTextArea";

    /**
     * Name of the card in the results panel associated with the results table.
     */
    private static final String CARD_TABLE = "resultsTable";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to the workspace statusbar.
     */
    private IStatusBar statusBar_;

    /**
     * Splitpane separating the sqlArea_ and the resultsArea_.
     */
    private JSmartSplitPane areaSplitPane_;

    /**
     * Text area for entering sql statements.
     */
    private JEditTextArea sqlEditor_;

    /**
     * Text are for sql execution results.
     */
    private JSmartTextArea resultsArea_;

    /**
     * Flippane which houses the jdbc configuration panel.
     */
    private JFlipPane leftFlipPane_;

    /**
     * Menu that contains a history of recently executed sql.
     */
    private JConveyorMenu sqlMenu_;

    /**
     * Database configuration panel.
     */
    private DBConfig dbConfigPane_;
    
    /**
     * Pretty prints SQL statements.
     */
    private SQLFormatter formatter_;

    /**
     * View to edit the sql formatter.
     */
    private SQLFormatterView formatterView_;
    
    /**
     * View to edit the query plugin preferences.
     */
    private DBPrefsView prefsView_;
    
    /**
     * Flag to send error messages to the console.
     */
    private boolean sendErrorToConsole_;

    /**
     * Database benchmark.
     */
    private DBBenchmark benchmark_;
    
    /**
     * View for configuring the database benchmark.
     */
    private DBBenchmarkView benchmarkView_;

    /**
     * Table that shows the results of a SQL query.
     */
    private JSmartTable resultsTable_;

    /**
     * Layout that flips between a table in the results panel and a textarea.
     */
    private CardLayout resultsLayout_;
    
    /**
     * Panel that has both the table and textarea used to show query results.
     */
    private JPanel resultsCardPanel_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a QueryPlugin. Instantiated via reflection.
     */
    public QueryPlugin()
    {
        sendErrorToConsole_ = true;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the status bar.
     *
     * @return IStatusBar
     */
    public IStatusBar getStatusBar()
    {
        return statusBar_;
    }

    
    /**
     * Returns the results area where the output of SQL statements is displayed.
     * 
     * @return JSmartTextArea
     */
    public JSmartTextArea getResultsArea()
    {
        return resultsArea_;
    }    

    
    /**
     * Returns the sql editor.
     * 
     * @return JEditTextArea
     */
    public JEditTextArea getSQLEditor()
    {
        return sqlEditor_;
    }

    
    /**
     * Returns the formatter.
     * 
     * @return SQLFormatter
     */
    public SQLFormatter getFormatter()
    {
        return formatter_;
    }
    
    
    /**
     * Sets the formatter.
     * 
     * @param formatter The formatter to set.
     */
    public void setFormatter(SQLFormatter formatter)
    {
        formatter_ = formatter;
    }
    
    
    /**
     * Returns the sendErrorToConsole.
     * 
     * @return boolean
     */
    public boolean isSendErrorToConsole()
    {
        return sendErrorToConsole_;
    }


    /**
     * Sets the sendErrorToConsole.
     * 
     * @param sendErrorToConsole The sendErrorToConsole to set.
     */
    public void setSendErrorToConsole(boolean sendErrorToConsole)
    {
        sendErrorToConsole_ = sendErrorToConsole;
    }
    
    
    /**
     * Returns the currently selected database profile.
     * 
     * @return DBProfile
     */
    public DBProfile getCurrentProfile() 
    {
        return dbConfigPane_.getCurrentProfile();
    }
    
    
    /**
     * Gets the active sql statement(s) based on the cursor position, selection
     * and position in an existing sql statement.
     *
     * @return Selected text if a selection exists. SQL statement if the cursor
     *         is position in the bounds of a valid sql statement, or the
     *         entire contents of the text area.
     */
    public String getActiveText()
    {
        IntRange range = getActiveRange();

        String active =
            sqlEditor_.getText(
                range.getMinimumInteger(),
                range.getMaximumInteger() - range.getMinimumInteger() + 1);

        logger_.info(StringUtil.banner("Active Text:\n" + "'" + active +"'"));
        return active;
    }


    /**
     * Replaces the current active text.
     *
     * @param active Text to replace current active text with.
     */
    public void setActiveText(String active)
    {
        String all = sqlEditor_.getText();
        int len = all.length();
        IntRange range = getActiveRange();
        int min = range.getMinimumInteger();
        int max = range.getMaximumInteger();

        logger_.debug("Range: " + range);
        logger_.debug("JETA: " + len);

        StringBuffer sb = new StringBuffer();
        sb.append(all.substring(0, min));
        //sb.append("\n");
        sb.append(active);
        //sb.append("\n");
        sb.append(all.substring(max + 1, len));

        sqlEditor_.setText(sb.toString());
    }

    
    /**
     * Runs a query against the database and returns the results as a nicely
     * formatted string.
     *
     * @param sql SQL statement.
     * @return Formatted results.
     * @throws SQLException on SQL error.
     * @see JDBCUtil#format(ResultSet)
     */
    public String executeSQL(String sql) throws SQLException
    {
        String metaResults = null;
        String lower = sql.trim().toLowerCase();
        String session = dbConfigPane_.getSession();
        
        if (lower.startsWith("select"))
        {
            // Execute select statement
            String formattedResults = null;
            Object[][] tableResults = null;
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet resultSet = null;
            
            try
            {
                conn = JDBCSession.getConnection(session);
                
                stmt = 
                    conn.prepareStatement(
                        JDBCSession.prepSQL(sql), 
                        ResultSet.TYPE_SCROLL_INSENSITIVE, 
                        ResultSet.CONCUR_READ_ONLY);
                
                resultSet = stmt.executeQuery();
                formattedResults = JDBCUtil.format(resultSet);
                resultSet.beforeFirst();
                tableResults = JDBCUtil.toArray(resultSet, false);
                
                // Output to textarea
                resultsArea_.append(formattedResults);
                
                // Output to table
                DefaultTableModel dtm = 
                    new DefaultTableModel(
                        tableResults, 
                        JDBCUtil.getColumnNames(resultSet)); 

                TableSorter sorter = (TableSorter) resultsTable_.getModel();
                sorter.setTableModel(dtm);
                
                metaResults = formattedResults;
            }
            finally
            {
                JDBCUtil.close(stmt);
                JDBCUtil.close(resultSet);
                JDBCUtil.releaseConnection(conn); 
            }
        }
        else if (lower.startsWith("insert") ||
                 lower.startsWith("delete") ||
                 lower.startsWith("update") ||
                 lower.startsWith("create") ||
                 lower.startsWith("drop")   ||
                 lower.startsWith("alter"))
        {
            metaResults = 
                JDBCSession.executeUpdate(session, sql) + " rows affected.";
        }
        else
        {
            // Everything else is processed as an update
            metaResults = 
                JDBCSession.executeUpdate(session, sql) + " rows affected.";
        }

        addToHistory(sql);
        return metaResults;
    }

    //--------------------------------------------------------------------------
    // Build UI
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        setLayout(new BorderLayout());

        // PrintWriter depends on the results text area so this goes first
        JHeaderPanel resultsPanel = buildResultsArea();
        
        PrintWriter pw = 
            new PrintWriter(
                new OutputStreamWriter(
                    new JTextAreaOutputStream(getResultsArea())), true);
        
        // buildSQLEditor() depends on a non-null benchmark_ so this goes first
        benchmark_ = new DBBenchmark(this, true, pw);

        // Split SQL editor and results panel
        areaSplitPane_ =
            new JSmartSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                buildSQLEditor(),
                resultsPanel);
        
        leftFlipPane_ = new JFlipPane(JFlipPane.LEFT);
     
        //
        // Hookup the flippers!
        //
        
        // Databases
        leftFlipPane_.addFlipper(
            DBConfig.ICON_DBCONFIG, 
            "Databases", 
            dbConfigPane_ = new DBConfig(this));
        
        // Query Plugin Prefs
        leftFlipPane_.addFlipper(
            DBPrefsView.ICON_DBPREFS,
            "Preferences", 
            prefsView_ = new DBPrefsView(this));

        // Formatter
        formatter_ = new SQLFormatter();
        
        leftFlipPane_.addFlipper(
            SQLFormatterView.ICON, 
            "Formatter", 
            formatterView_ = new SQLFormatterView(formatter_));

        // Benchmark
        leftFlipPane_.addFlipper(
            DBBenchmarkView.ICON_DBBENCHMARK,
            "Benchmark", 
            benchmarkView_ = new DBBenchmarkView(benchmark_));

        // Reference
        leftFlipPane_.addFlipper(
            ImageCache.getIcon(ImageCache.IMAGE_QUESTION_MARK),
            "Reference", 
            buildSQLReferencePane());
        
        add(leftFlipPane_, BorderLayout.WEST);
        add(areaSplitPane_, BorderLayout.CENTER);
    }

    
    /**
     * Constructs the flipper panel that contains SQL reference information.
     * 
     * @return JHeaderPanel
     */
    protected JHeaderPanel buildSQLReferencePane()
    {
        JHeaderPanel refPane = 
            new JHeaderPanel(
                ImageCache.getIcon(ImageCache.IMAGE_QUESTION_MARK),
                "SQL Reference");

        String sqlRef =
            File.separator + FileUtil.trailWithSeparator(
                ClassUtil.packageToPath(
                    ClassUtils.getPackageName(
                        QueryPlugin.class))) + "sqlref.txt";

        sqlRef = sqlRef.replace(File.separatorChar, '/');

        JEditTextArea area = 
            new JEditTextArea(new TSQLTokenMarker(), new SQLDefaults());

        area.getPainter().setFont(FontUtil.getPreferredMonoFont());
        refPane.setContent(area);
        area.setText(ResourceUtil.getResourceAsString(sqlRef)+ "\n");
        area.scrollTo(0, 0);
        return refPane;
    }


    /**
     * Constructs the SQL editor text area.
     *
     * @return JHeaderPanel
     */
    protected JHeaderPanel buildSQLEditor()
    {
        sqlMenu_ = new JConveyorMenu("SQL History", 10);
        JEditPopupMenu editMenu = new JEditPopupMenu();
        editMenu.setLabel("Edit");

        JPopupMenu rootPopup = new JSmartPopupMenu("Root menu");
        rootPopup.add(sqlMenu_);

        sqlEditor_ = 
            new JEditTextArea(new TSQLTokenMarker(), new SQLDefaults());
        
        sqlEditor_.setPopupMenu(rootPopup);  

        editMenu.setTextArea(sqlEditor_);
        editMenu.buildView();

        rootPopup.add(SwingUtil.popupToMenu(editMenu));

        sqlEditor_.setFont(FontUtil.getPreferredMonoFont());
        sqlEditor_.setSaveContents(true);
        
        sqlEditor_.getInputHandler().addKeyBinding(
            "C+ENTER", new ExecuteAllAction(this));

        sqlEditor_.getInputHandler().addKeyBinding(
            "CS+ENTER", new ExecuteCurrentAction(this));

        sqlEditor_.getInputHandler().addKeyBinding(
            "C+UP", new CtrlUpAction());

        sqlEditor_.getInputHandler().addKeyBinding(
            "CS+F", new FormatSQLAction(this));

        //
        // Build toolbar for SQL editor
        //
        JButton executeAll = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_PLAY),
            "Execute All SQL",
            new ExecuteAllAction(this));

        JButton executeCurrent = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_FORWARD),
            "Execute Current/Selected",
            new ExecuteCurrentAction(this));
        
        JButton format = JHeaderPanel.createButton(
            SQLFormatterView.ICON,
            "Format",
            new FormatSQLAction(this));

        JButton clear = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_CLEAR),
            "Clear",
            new JEditActions.ClearAction(sqlEditor_));

        JButton help = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_QUESTION_MARK),
            "SQL Reference",
            new SQLReferenceAction(this));
        
        JButton bench = 
            JHeaderPanel.createButton(new BenchmarkAction(this, benchmark_));
        
        JToolBar toolbar = JHeaderPanel.createToolBar();
        toolbar.add(executeAll);
        toolbar.add(executeCurrent);
        toolbar.add(format);
        toolbar.addSeparator();
        toolbar.add(bench);
        toolbar.addSeparator();
        toolbar.add(clear);
        toolbar.add(help);
        
        return new JHeaderPanel("SQL Editor", toolbar, sqlEditor_);
    }

    
    /**
     * Constructs the results area.
     *
     * @return JHeaderPanel
     */
    protected JHeaderPanel buildResultsArea()
    {
        resultsArea_ = new JSmartTextArea();
        resultsArea_.setFont(FontUtil.getPreferredMonoFont());

        // Build toolbar for Results panel
        JButton clear = JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_CLEAR),
                "Clear results",
                new ClearAction(resultsArea_));

        JButton listTables = JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_TABLES),
                "List tables",
                new ListTablesAction(this));

        JButton listColumns = JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_COLUMNS),
                "List columns",
                new ListColumnsAction(this));

        JToggleButton switchResults = JHeaderPanel.createToggleButton(
                ImageCache.getIcon(ImageCache.IMAGE_DUKE),
                "Switch results display",
                new ToggleTableAction());
        
        JPanel resultsAreaPanel = new JPanel(new BorderLayout());
        
        JToggleButton filterResults = JHeaderPanel.createToggleButton(
                ImageCache.getIcon(ImageCache.IMAGE_FUNNEL),
                "Show results filter",
                new ShowResultsFilterAction(this, resultsAreaPanel));
                
        JToolBar toolbar = JHeaderPanel.createToolBar();
        toolbar.add(switchResults);
        toolbar.add(listTables);
        toolbar.add(listColumns);
        toolbar.add(filterResults);
        toolbar.add(clear);

        resultsLayout_ = new CardLayout();
        resultsCardPanel_ = new JPanel(resultsLayout_);
        
        DefaultTableModel resultsTableModel = new DefaultTableModel(10,10);
        TableSorter sorter = new TableSorter(resultsTableModel);
        resultsTable_ = new JSmartTable(sorter);
        resultsTable_.setFont(FontUtil.getPreferredMonoFont());
        FontUtil.setBold(resultsTable_.getTableHeader());
        sorter.setTableHeader(resultsTable_.getTableHeader());
        
        resultsCardPanel_.add(new JScrollPane(resultsTable_), CARD_TABLE);
        resultsCardPanel_.add(new JScrollPane(resultsArea_), CARD_TEXTAREA);
        resultsAreaPanel.add(BorderLayout.CENTER, resultsCardPanel_);
        resultsLayout_.show(resultsCardPanel_, CARD_TEXTAREA);
        return new JHeaderPanel("Results", toolbar, resultsCardPanel_);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Adds a sql statement to the popup menu history.
     *
     * @param sql SQL statement to add to the history.
     */
    protected void addToHistory(String sql)
    {
        sqlMenu_.add(new JSmartMenuItem(new ExecutePriorAction(this, sql)));
    }


    /**
     * Determines the range of what we consider "active" text in the sql input
     * text area. Active text is defined as follows:<p>
     * <ol>
     *  <li>The selected text if the selection is not empty.
     *  <li>The current sql statement.
     *  <li>The entire contents of the text area.
     * </ol>
     *
     * @return Range enclosing active text.
     */
    protected IntRange getActiveRange()
    {
        IntRange range = new IntRange(0);
        String selected = sqlEditor_.getSelectedText();

        if (!StringUtils.isEmpty(selected))
        {
            range = new IntRange(
                sqlEditor_.getSelectionStart(), 
                sqlEditor_.getSelectionEnd());
        }
        else
        {
            int caret = sqlEditor_.getCaretPosition();
            String all = sqlEditor_.getText();
            
            // Find first semicolon after the caret and let that be our sql
            // statement terminator
            int semi = all.indexOf(SQL_TERMINATOR, caret);

            if (semi >= 0)
            {
                // Terminating semicolon found. so we just have to find the
                // beginning of the statement which either be the beginning of
                // the buffer or the first semicolor going in the reverse
                // direction from the carets location.
                int begin;

                for (begin = caret - 1; begin >= 0; begin--)
                {
                    if (all.charAt(begin) == SQL_TERMINATOR)
                    {
                        break;
                    }
                }

                range = new IntRange(begin + 1, semi);
            }
            else
            {
                // No terminating semicolon found so just return the contents
                // of the entire textarea.
                range = new IntRange(0, all.length() - 1);
            }
        }

        return range;
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPlugin#startup(java.util.Map)
     */
    public void startup(Map params)
    {
        if (params != null)
            statusBar_ = (IStatusBar)
                params.get(PluginWorkspace.KEY_STATUSBAR);

        buildView();
    }


    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "JDBC Query";
    }


    /**
     * @see toolbox.workspace.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }


    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Simple SQL driven interface to a JDBC accessible database.";
    }


    /**
     * @see toolbox.workspace.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root =
            XOMUtil.getFirstChildElement(
                prefs,
                NODE_QUERY_PLUGIN,
                new Element(NODE_QUERY_PLUGIN));

        sqlMenu_.setCapacity(XOMUtil.getInteger(
            root.getFirstChildElement(ATTR_HISTORY_MAX), 10));

        Elements historyItems = root.getChildElements(NODE_HISTORY_ITEM);

        logger_.debug("Restoring " + historyItems.size() + " saved sql stmts");

        for (int i = 0; i < historyItems.size(); i++)
            addToHistory(historyItems.get(i).getValue());

        leftFlipPane_.applyPrefs(root);
        dbConfigPane_.applyPrefs(root);
        resultsArea_.applyPrefs(root);
        sqlEditor_.applyPrefs(root);
        areaSplitPane_.applyPrefs(root);
        
        // Update the sql formatter configuration view
        formatter_.applyPrefs(root);
        formatterView_.setFormatter(formatter_);
               
        // Update the db benchmark configuration view
        benchmark_.applyPrefs(root);
        benchmarkView_.setBenchmark(benchmark_);
        
        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_QUERY_PLUGIN);

        for (Iterator i = new ArrayIterator(sqlMenu_.getMenuComponents()); 
            i.hasNext();)
        {
            Object obj = i.next();
            
            if (obj instanceof JMenuItem)
            {
                JMenuItem item = (JMenuItem) obj;
                Element historyItem = new Element(NODE_HISTORY_ITEM);
                historyItem.appendChild(item.getText());
                root.appendChild(historyItem);
            }
        }

        leftFlipPane_.savePrefs(root);
        dbConfigPane_.savePrefs(root);
        resultsArea_.savePrefs(root);
        
        // Hardcode to always be true!
        sqlEditor_.setSaveContents(true);
        sqlEditor_.savePrefs(root);
        
        areaSplitPane_.savePrefs(root);
        formatter_.savePrefs(root);
        benchmark_.savePrefs(root);
        
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);

        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // CtrlUpAction
    //--------------------------------------------------------------------------

    /**
     * Ctrl-Up Key action.
     */
    class CtrlUpAction extends AbstractAction
    {
        /**
         * Creates a CtrlUpAction.
         */
        CtrlUpAction()
        {
            super("Scroll History Up");
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            statusBar_.setWarning("TODO: Implement ctrl-up");
            String s = getActiveText();
            logger_.debug(StringUtil.addBars(s));
        }
    }

    //--------------------------------------------------------------------------
    // ToggleTableAction
    //--------------------------------------------------------------------------
    
    /**
     * Flips between the textarea and the table in the results panel.
     */
    public class ToggleTableAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            resultsLayout_.next(resultsCardPanel_);
        }
    }
}