package toolbox.plugin.jdbc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import org.jedit.syntax.TSQLTokenMarker;
import org.jedit.syntax.TextAreaDefaults;

import toolbox.jedit.JEditActions;
import toolbox.jedit.JEditPopupMenu;
import toolbox.jedit.JEditTextArea;
import toolbox.jedit.SQLDefaults;
import toolbox.util.ClassUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.db.SQLFormatter;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JConveyorMenu;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartPopupMenu;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.textarea.ClearAction;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.WorkspaceAction;

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

    /**
     * Child of QueryPlugin that contains contents of the SQL text area.
     */
    public static final String NODE_CONTENTS = "SQLContents";

    /**
     * Child of SQLContents that contains a single line of content (since XOM
     * conveniently left out support for CDATA)
     */
    public static final String NODE_LINE = "Line";

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
     * Maps sqlpopup_ menu items to the actual sql text.
     */
    private Map sqlHistory_;

    /**
     * Database configuration panel.
     */
    private DBConfig dbConfigPane_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a QueryPlugin.
     */
    public QueryPlugin()
    {
        // Instantiated via reflection
    }

    //--------------------------------------------------------------------------
    // Package Protected
    //--------------------------------------------------------------------------

    /**
     * Returns the status bar.
     *
     * @return IStatusBar
     */
    IStatusBar getStatusBar()
    {
        return statusBar_;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        // Split SQL editor and results panel
        areaSplitPane_ =
            new JSmartSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                buildSQLEditor(),
                buildResultsArea());

        setLayout(new BorderLayout());
        dbConfigPane_ = new DBConfig(this);
        leftFlipPane_ = new JFlipPane(JFlipPane.LEFT);
        leftFlipPane_.addFlipper("Databases", dbConfigPane_);
        leftFlipPane_.addFlipper("Reference", buildReferencePane());
        add(leftFlipPane_, BorderLayout.WEST);
        add(areaSplitPane_, BorderLayout.CENTER);
    }


    protected JHeaderPanel buildReferencePane()
    {
        JHeaderPanel refPane = new JHeaderPanel("SQL Reference");

        String sqlRef =
            File.separator +
            FileUtil.trailWithSeparator(
                ClassUtil.packageToPath(
                    ClassUtil.stripClass(
                        QueryPlugin.class.getName()))) + "sqlref.txt";

        sqlRef = sqlRef.replace(File.separatorChar, '/');

        JEditTextArea area =
            new JEditTextArea(new TSQLTokenMarker(), new SQLDefaults());

        area.getPainter().setFont(FontUtil.getPreferredMonoFont());
        refPane.setContent(area);
        area.setText( ResourceUtil.getResourceAsString(sqlRef)+ "\n");
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
        sqlHistory_ = new HashMap();

        sqlMenu_ = new JConveyorMenu("SQL History", 10);
        JEditPopupMenu editMenu = new JEditPopupMenu();
        editMenu.setLabel("Edit");

        JPopupMenu rootPopup = new JSmartPopupMenu("Root menu");
        rootPopup.add(sqlMenu_);

        TextAreaDefaults defaults = new SQLDefaults();
        defaults.popup = rootPopup;

        sqlEditor_ = new JEditTextArea(new TSQLTokenMarker(), defaults);

        editMenu.setTextArea(sqlEditor_);
        editMenu.buildView();

        rootPopup.add(SwingUtil.popupToMenu(editMenu));

        sqlEditor_.setFont(FontUtil.getPreferredMonoFont());

        sqlEditor_.getInputHandler().addKeyBinding(
            "C+ENTER", new ExecuteAllAction());

        sqlEditor_.getInputHandler().addKeyBinding(
            "CS+ENTER", new ExecuteCurrentAction());

        sqlEditor_.getInputHandler().addKeyBinding(
            "C+UP", new CtrlUpAction());

        sqlEditor_.getInputHandler().addKeyBinding(
            "CS+F", new FormatSQLAction());

        // Build toolbar for SQL editor
        JButton executeAll = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_PLAY),
            "Execute All SQL",
            new ExecuteAllAction());

        JButton executeCurrent = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_FORWARD),
            "Execute Current/Selected",
            new ExecuteCurrentAction());
        
        JButton format = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_BRACES),
            "Format",
            new FormatSQLAction());

        JButton clear = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_CLEAR),
            "Clear",
            new JEditActions.ClearAction(sqlEditor_));

        JButton help = JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_QUESTION_MARK),
            "SQL Reference",
            new SQLReferenceAction());

        JToolBar toolbar = JHeaderPanel.createToolBar();
        toolbar.add(executeAll);
        toolbar.add(executeCurrent);
        toolbar.add(format);
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
                new ListTablesAction());

        JButton listColumns = JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_COLUMNS),
                "List columns",
                new ListColumnsAction());

        JToolBar toolbar = JHeaderPanel.createToolBar();
        toolbar.add(listTables);
        toolbar.add(listColumns);
        toolbar.add(clear);

        return new JHeaderPanel(
                "Results", toolbar, new JScrollPane(resultsArea_));
    }


    /**
     * Runs a query against the database and returns the results as a nicely
     * formatted string.
     *
     * @param sql SQL query.
     * @return Formatted results.
     * @see JDBCUtil#format(ResultSet)
     */
    protected String executeSQL(String sql)
    {
        String metaResults = null;
        String lower = sql.trim().toLowerCase();

        try
        {
            if (lower.startsWith("select"))
            {
                // Execute select statement
                metaResults = JDBCUtil.executeQuery(sql);
            }
            else if (lower.startsWith("insert") ||
                     lower.startsWith("delete") ||
                     lower.startsWith("update") ||
                     lower.startsWith("create") ||
                     lower.startsWith("drop"))
            {
                metaResults = JDBCUtil.executeUpdate(sql) + " rows affected.";
            }
            else
            {
                // Everything else is processed as an update
                metaResults = JDBCUtil.executeUpdate(sql) + " rows affected.";
            }

            addToHistory(sql);
        }
        catch (Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }

        return metaResults;
    }


    /**
     * Adds a sql statement to the popup menu history.
     *
     * @param sql SQL statement to add to the history.
     */
    protected void addToHistory(String sql)
    {
        if (!sqlHistory_.containsValue(sql))
        {
            sqlHistory_.put(sql, sql);

            JMenuItem menuItem =
                new JSmartMenuItem(new ExecutePriorAction(sql));

            sqlMenu_.add(menuItem);
        }
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
                sqlEditor_.getSelectionStart(), sqlEditor_.getSelectionEnd());
        }
        else
        {
            int caret = sqlEditor_.getCaretPosition();
            String all = sqlEditor_.getText();
            int semi = all.indexOf(';', caret);

            if (semi >= 0)
            {
                // Terminating semicolon found. so we just have to find the
                // beginning of the statement which either be the beginning of
                // the buffer or the first semicolor going in the reverse
                // direction from the carets location.
                int begin;

                for (begin = caret - 1; begin >= 0; begin--)
                {
                    if (all.charAt(begin) == ';')
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


    /**
     * Gets the active sql statement(s) based on the cursor position, selection
     * and position in an existing sql statement.
     *
     * @return Selected text if a selection exists. SQL statement if the cursor
     *         is position in the bounds of a valid sql statement, or the
     *         entire contents of the text area.
     */
    protected String getActiveText()
    {
        IntRange range = getActiveRange();

        String active =
            sqlEditor_.getText(
                range.getMinimumInteger(),
                range.getMaximumInteger() - range.getMinimumInteger());

        return active;
    }


    /**
     * Replaces the current active text.
     *
     * @param active Text to replace current active text with.
     */
    protected void setActiveText(String active)
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
        sb.append("\n");
        sb.append(active);
        sb.append("\n");
        sb.append(all.substring(max, len));

        sqlEditor_.setText(sb.toString());
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
    public void applyPrefs(Element prefs)
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


        Element contents =
            XOMUtil.getFirstChildElement(
                root,
                NODE_CONTENTS,
                new Element(NODE_CONTENTS));

        sqlEditor_.setText(XOMUtil.decodeBlankLines(contents.getValue()));
        sqlEditor_.scrollTo(0, 0);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_QUERY_PLUGIN);

        for (Iterator i = sqlHistory_.values().iterator(); i.hasNext();)
        {
            Element historyItem = new Element(NODE_HISTORY_ITEM);
            historyItem.appendChild(i.next().toString());
            root.appendChild(historyItem);
        }

        Element contents = new Element(NODE_CONTENTS);
        contents.appendChild(XOMUtil.encodeBlankLines(sqlEditor_.getText()));
        root.appendChild(contents);

        leftFlipPane_.savePrefs(root);
        dbConfigPane_.savePrefs(root);
        resultsArea_.savePrefs(root);
        sqlEditor_.savePrefs(root);
        areaSplitPane_.savePrefs(root);

        XOMUtil.insertOrReplace(prefs, root);
    }

    //--------------------------------------------------------------------------
    // SQLReferenceAction
    //--------------------------------------------------------------------------

    /**
     * Dumps a short SQL reference text to the SQL editor.
     */
    class SQLReferenceAction extends AbstractAction
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String sqlRef = File.separator +
                FileUtil.trailWithSeparator(
                    ClassUtil.packageToPath(
                        ClassUtil.stripClass(
                            QueryPlugin.class.getName()))) +
                                "sqlref.txt";

            sqlRef = sqlRef.replace(File.separatorChar, '/');

            sqlEditor_.setText(sqlEditor_.getText() + "\n" +
                ResourceUtil.getResourceAsString(sqlRef));
        }
    }

    //--------------------------------------------------------------------------
    // ExecuteAllAction
    //--------------------------------------------------------------------------

    /**
     * Runs all SQL statements in the editor. Each SQL statement must be 
     * terminated by a semicolon. The results are appendended to the output 
     * textarea.
     */
    class ExecuteAllAction extends WorkspaceAction
    {
        /**
         * Creates an ExecuteAllAction.
         */
        ExecuteAllAction()
        {
            super("Execute All SQL", true, QueryPlugin.this, statusBar_);
            putValue(MNEMONIC_KEY, new Integer('E'));
            putValue(SHORT_DESCRIPTION, "Executes all the SQL statements");
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            String sqlText = sqlEditor_.getText().trim();
            
            if (StringUtils.isBlank(sqlText))
            {
                statusBar_.setWarning(
                    "Enter SQL statements to execute into the editor first.");
            }
            else
            {
                statusBar_.setInfo("Executing...");
                
	            String[] stmts = StringUtils.split(sqlText, SQL_TERMINATOR);
	            
	            //logger_.debug(
	            //    StringUtil.addBars(ArrayUtil.toString(stmts, true)));
	            
                List errors = new ArrayList();
                
	            for (int i = 0; i < stmts.length; i++)
	            {
	                try
	                {
	                    stmts[i] = stmts[i].trim();
	                    String results = executeSQL(stmts[i]);
	                    
	                    if (!StringUtil.isMultiline(results))
	                    {
	                        String command = StringUtils.split(stmts[i])[0];
	                        resultsArea_.append(command + " ");
	                    }
	                    else
	                    {
	                        resultsArea_.append("\n");
	                        
	                        //resultsArea_.append(
	                        //    "Multline found..skipping command : " + 
	                        //    stmts[i].substring(0,10));
	                    }
	                    
	                    //resultsArea_.append(StringUtil.addBars(results) + "\n");
	                    resultsArea_.append(results + "\n");
	                    
	                    //StringOutputStream sos = new StringOutputStream();
	                    //HexDump.dump(results.getBytes(), 0, sos, 0);
	                    //resultsArea_.append(sos.toString());
	                    
	                    
	                    //
	                    // Scroll to the end of the output textarea if more
	                    // than a couple of page fulls of results is appended
	                    //
	                    
	                    if ((!StringUtils.isBlank(results)) &&
	                        (StringUtil.tokenize(results, 
	                            StringUtil.NL).length < AUTO_SCROLL_THRESHOLD))
	                    {
	                        resultsArea_.scrollToEnd();
	                    }
	                }
	                catch (Exception ee)
	                {
	                    errors.add(e);
	                }
	                
	                if (errors.size() == 1)
	                {
	                    throw (Exception) errors.get(0);
	                }
	                else if (errors.size() > 1)
	                {
	                    StringBuffer sb = new StringBuffer();
	                    sb.append("Not all statements executed successfully.");
	                    sb.append("\n");
	                    
	                    for (int j = 0; j < errors.size(); j++)
	                    {
	                        Exception ex = (Exception) errors.get(j);
	                        sb.append(ex.getMessage()).append("\n");
	                        sb.append(ExceptionUtils.getFullStackTrace(ex));
	                        sb.append("\n");
	                    }
	                    
	                    throw new SQLException(sb.toString());
	                }
	            }

                statusBar_.setInfo("Done");
            }
        }
    }

    
    //--------------------------------------------------------------------------
    // ExecuteCurrentAction
    //--------------------------------------------------------------------------

    /**
     * Runs the query and appends the results to the output text area.
     */
    class ExecuteCurrentAction extends WorkspaceAction
    {
        /**
         * Creates a ExecuteCurrentAction.
         */
        ExecuteCurrentAction()
        {
            super("Execute Current Statement", true, QueryPlugin.this,
                statusBar_);
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            //
            // By default, execute the selected text
            //

            String sql = sqlEditor_.getSelectedText();

            //
            // If no text is selected, then execute the current statement. This
            // assumes we are on the first line of the statement and that there
            // is a semicolon somewhere to tell us where the statement ends.
            //

            if (StringUtils.isBlank(sql))
            {
                int max = sqlEditor_.getLineCount();
                int curr = sqlEditor_.getCaretLine();
                boolean terminatorFound = false;
                StringBuffer stmt = new StringBuffer();

                while (curr <= max && !terminatorFound)
                {
                    String line = sqlEditor_.getLineText(curr++);
                    int pos = -1;
                    if ((pos = line.indexOf(";")) >= 0)
                    {
                        stmt.append(line.substring(0, pos + 1));
                        terminatorFound = true;
                    }
                    else
                    {
                        stmt.append("\n" + line);
                    }
                }

                //
                // If no terminating semicolon for the statement is found, then
                // assume only the current line contains the entire sql
                // statement to execute.
                //

                sql = stmt.toString();
            }

            if (StringUtils.isBlank(sql))
            {
                statusBar_.setInfo("Enter SQL to execute");
            }
            else
            {
                logger_.debug("Executing SQL: \n" + sql);

                statusBar_.setInfo("Executing...");
                String results = executeSQL(sql);
                resultsArea_.append(results + "\n");

                if ((!StringUtils.isBlank(results)) &&
                    (StringUtil.tokenize(results, StringUtil.NL).length < 50))
                    resultsArea_.scrollToEnd();

                statusBar_.setInfo("Done");
            }
        }
    }

    //--------------------------------------------------------------------------
    // FormatSQLAction
    //--------------------------------------------------------------------------

    /**
     * Formats the current or selected sql statements.
     */
    class FormatSQLAction extends WorkspaceAction
    {
        /**
         * Creates a FormatSQLAction.
         */
        FormatSQLAction()
        {
            super("Format", false, null, statusBar_);
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            String sql = getActiveText();

            if (StringUtils.isBlank(sql))
            {
                statusBar_.setWarning("Nothing to format.");
            }
            else
            {
                SQLFormatter formatter = new SQLFormatter();
                String[] statements = StringUtil.tokenize(sql, ";");
                StringBuffer sb = new StringBuffer();

                for (int i = 0; i < statements.length; i++)
                {
                    sb.append(formatter.format(statements[i] + ";"));
                    sb.append("\n");
                }

                setActiveText(sb.toString());
            }
        }
    }

    //--------------------------------------------------------------------------
    // ExecutePriorAction
    //--------------------------------------------------------------------------

    /**
     * Runs the query selected from the SQL history popup menu.
     */
    class ExecutePriorAction extends AbstractAction
    {
        /**
         * SQL statement to execute.
         */
        private String sql_;

        /**
         * Creates a ExecutePriorAction.
         *
         * @param sql SQL to execute.
         */
        ExecutePriorAction(String sql)
        {
            super(sql);
            sql_ = sql;
            putValue(SHORT_DESCRIPTION, "Executes the SQL statement");
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            sqlEditor_.setText(sql_);
            new ExecuteAllAction().actionPerformed(e);
        }
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

            System.out.println(StringUtil.addBars(s));
        }
    }

    //--------------------------------------------------------------------------
    // ListTablesAction
    //--------------------------------------------------------------------------

    /**
     * Queries the DB metadata and dumps a list of the tables.
     */
    class ListTablesAction extends WorkspaceAction
    {
        /**
         * Creates a ListTablesAction.
         */
        public ListTablesAction()
        {
            super("List Tables", true, true, null, statusBar_);
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            Connection conn = JDBCUtil.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, null, null);
            String tables = JDBCUtil.format(rs);
            resultsArea_.append(tables);
            JDBCUtil.releaseConnection(conn);
        }
    }

    //--------------------------------------------------------------------------
    // ListColumnsAction
    //--------------------------------------------------------------------------

    /**
     * Queries the DB metadata and dumps a list of all columns. If a table
     * name is selected in the results area, then only the columns for the
     * selected table are returned.
     */
    class ListColumnsAction extends WorkspaceAction
    {
        /**
         * Creates a ListColumnsAction.
         */
        public ListColumnsAction()
        {
            super("List Columns", true, true, null, statusBar_);
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            String table = resultsArea_.getSelectedText();

            if (StringUtils.isBlank(table))
            {
                JSmartOptionPane.showMessageDialog(QueryPlugin.this,
                    "Select text matching the column name from the output " +
                    "area.", "Error", JSmartOptionPane.ERROR_MESSAGE);
            }
            else
            {
                Connection conn = null;

                try
                {
                    conn = JDBCUtil.getConnection();
                    DatabaseMetaData meta = conn.getMetaData();
                    ResultSet rs = meta.getColumns(null, null, table, null);
                    String tables = JDBCUtil.format(rs);
                    resultsArea_.append(tables);
                }
                finally
                {
                    JDBCUtil.releaseConnection(conn);
                }
            }
        }
    }
}