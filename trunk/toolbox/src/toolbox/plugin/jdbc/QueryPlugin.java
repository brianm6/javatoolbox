package toolbox.jdbc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

import org.jedit.syntax.KeywordMap;
import org.jedit.syntax.SQLTokenMarker;
import org.jedit.syntax.TextAreaDefaults;

import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.util.ExceptionUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.StringUtil;
import toolbox.util.Stringz;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JConveyorPopupMenu;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.WorkspaceAction;

/**
 * Simple SQL Plugin that allows you to query and update a database via JDBC.
 * <p>
 * Features:
 * <ul>
 * <li>Remembers past queries so you don't have to type them in again
 *     (activated by a right mouse click in the SQL text area)
 * <li>Output is in plain text aligned by columns. Great for copy and paste to 
 *     other applications.
 * </ul>
 * 
 * Shortcuts:
 * <p>
 * <table border=1>
 *   <tr><th>Key</th><th>Function</th></tr>
 *   <tr><td>Ctrl-Enter</td><td>Execute query</td></tr>
 *   <tr><td>Ctrl-Up</td><td>Scroll up in SQL history</td></tr>
 *   <tr><td>Ctrl-Down</td><td>Scroll down in SQL history</td></tr>
 * </table>
 */ 
public class QueryPlugin extends JPanel implements IPlugin
{
    // TODO: create SQLDefaults for syntax hiliting
    // TODO: Ctrl-Up/Down should scroll through query history
     
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------     
     
    public static final Logger logger_ =
        Logger.getLogger(QueryPlugin.class);   

    /**
     * XML: Root preferences element for the query plugin.
     */
    public static final String NODE_QUERY_PLUGIN = "QueryPlugin";

    /**
     * XML: Attribute of QueryPlugin that stores the max number of entries 
     *      in the sql history popup menu before getting truncated. 
     */
    public static final String ATTR_HISTORY_MAX = "maxHistory";

    /**
     * XML: Child of QueryPlugin that contains a single "remembered" SQL stmt.
     */
    public static final String NODE_HISTORY_ITEM = "HistoryItem";
        
    /** 
     * XML: Child of QueryPlugin that contains the contents of the SQL text area 
     */
    public static final String NODE_CONTENTS = "SQLContents";

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
    private JEditTextArea sqlArea_;
    
    /** 
     * Text are for sql execution results. 
     */
    private JSmartTextArea resultsArea_;
    
    /** 
     * Invokes execution of sql command. 
     */
    private JButton queryButton_;
    
    /** 
     * Clears the contents of the sql results area. 
     */
    private JButton clearButton_;
    
    /** 
     * Flippane which houses the jdbc configuration panel. 
     */
    private JFlipPane leftFlipPane_;
    
    /** 
     * Popup menu that contains a history of recently executed sql. 
     */
    private JConveyorPopupMenu sqlPopup_;    
    
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
     * Builds the GUI.
     */
    protected void buildView()
    {
        sqlHistory_ = new HashMap();
        sqlPopup_ = new JConveyorPopupMenu("crapola",10);

        TextAreaDefaults defaults = new JavaDefaults();
        defaults.popup = sqlPopup_;
                        
        sqlArea_ = new JEditTextArea(
            new SQLTokenMarker(new KeywordMap(true)), defaults);
            
        sqlArea_.setFont(SwingUtil.getPreferredMonoFont());
        
        sqlArea_.getInputHandler().addKeyBinding(
            "C+ENTER", new ExecuteAction());
       
        sqlArea_.getInputHandler().addKeyBinding(
            "CS+ENTER", new ExecuteCurrentAction());
             
        sqlArea_.getInputHandler().addKeyBinding(
            "C+UP", new CtrlUpAction());
                         
        resultsArea_ = new JSmartTextArea();
        resultsArea_.setFont(SwingUtil.getPreferredMonoFont());
        
        areaSplitPane_ = 
            new JSmartSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                sqlArea_, // JEditTextArea is already wrapped in a scroller
                new JScrollPane(resultsArea_));

        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout());
            
        queryButton_ = new JSmartButton(new ExecuteAction());
        buttonPanel.add(queryButton_);

        clearButton_ = new JSmartButton(resultsArea_.new ClearAction());
        buttonPanel.add(clearButton_);
        
        buttonPanel.add(new JSmartButton(new ListTablesAction()));
        buttonPanel.add(new JSmartButton(new ListColumnsAction()));

        // Root 
        setLayout(new BorderLayout());
        
        dbConfigPane_ = new DBConfig(this);
        leftFlipPane_ = new JFlipPane(JFlipPane.LEFT);
        leftFlipPane_.addFlipper("JDBC Drivers", dbConfigPane_);

        add(leftFlipPane_, BorderLayout.WEST);
        add(areaSplitPane_, BorderLayout.CENTER);                
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    
    /**
     * Runs a query against the database and returns the results as a nicely 
     * formatted string.
     * 
     * @param sql SQL query
     * @return Formatted results
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
                     lower.startsWith("drop") )
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
     * @param sql SQL statement to add to the history
     */
    protected void addToHistory(String sql)
    {
        if (!sqlHistory_.containsValue(sql))
        {   
            sqlHistory_.put(sql, sql);
            
            JMenuItem menuItem = 
                new JSmartMenuItem(new ExecutePriorAction(sql));
            
            sqlPopup_.add(menuItem);
        }
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
            statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
            
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
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_QUERY_PLUGIN, new Element(NODE_QUERY_PLUGIN));
        
        sqlPopup_.setCapacity(XOMUtil.getInteger(
            root.getFirstChildElement(ATTR_HISTORY_MAX), 10));
        
        Elements historyItems = root.getChildElements(NODE_HISTORY_ITEM);
                
        logger_.debug("Restoring " + historyItems.size() + " saved sql stmts");
        
        for (int i=0; i<historyItems.size(); i++)
            addToHistory(historyItems.get(i).getValue());
        
        leftFlipPane_.applyPrefs(root);
        dbConfigPane_.applyPrefs(root);
        resultsArea_.applyPrefs(root);
        sqlArea_.applyPrefs(root);
        areaSplitPane_.applyPrefs(root);
        
        sqlArea_.setText(
            XOMUtil.getString(
                root.getFirstChildElement(NODE_CONTENTS),""));
    }

    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_QUERY_PLUGIN);
         
        for (Iterator i =  sqlHistory_.values().iterator(); i.hasNext(); )
        {
            Element historyItem = new Element(NODE_HISTORY_ITEM);
            historyItem.appendChild(i.next().toString());
            root.appendChild(historyItem);
        }
        
        Element contents = new Element(NODE_CONTENTS);
        contents.appendChild(sqlArea_.getText().trim());
        root.appendChild(contents);
        
        leftFlipPane_.savePrefs(root);
        dbConfigPane_.savePrefs(root);
        resultsArea_.savePrefs(root);
        sqlArea_.savePrefs(root);
        areaSplitPane_.savePrefs(root);
        
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // ExecuteAction
    //--------------------------------------------------------------------------
    
    /**
     * Runs the query and appends the results to the output text area.
     */
    class ExecuteAction extends WorkspaceAction
    {
        ExecuteAction()
        {
            super("Execute SQL", true, QueryPlugin.this, statusBar_);
            putValue(MNEMONIC_KEY, new Integer('E'));
            putValue(SHORT_DESCRIPTION, "Executes the SQL statement");
        }

        public void runAction(ActionEvent e) throws Exception
        {
            String sql = sqlArea_.getText();
            
            if (StringUtil.isNullOrBlank(sql))
            {
                statusBar_.setStatus("Enter SQL to execute");
            }
            else
            {
                statusBar_.setStatus("Executing...");
                String results = executeSQL(sqlArea_.getText());
                resultsArea_.append(results);
                
                if ((!StringUtil.isNullOrBlank(results)) &&
                    (StringUtil.tokenize(results, Stringz.NL).length < 50))
                    resultsArea_.scrollToEnd();
                    
                statusBar_.setStatus("Done");
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
        ExecuteCurrentAction()
        {
            super("Execute Current Statement", true, QueryPlugin.this, 
                statusBar_);
        }
    
        public void runAction(ActionEvent e) throws Exception
        {
            String sql = sqlArea_.getLineText(sqlArea_.getCaretLine());
            
            if (StringUtil.isNullOrBlank(sql))
            {
                statusBar_.setStatus("Enter SQL to execute");
            }
            else
            {   
                statusBar_.setInfo("Executing...");
                String results = executeSQL(sql);        
                resultsArea_.append(results);
                
                if ((!StringUtil.isNullOrBlank(results)) &&
                    (StringUtil.tokenize(results, Stringz.NL).length < 50))
                    resultsArea_.scrollToEnd();

                statusBar_.setInfo("Done");
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
        private String sql_;
        
        ExecutePriorAction(String sql)
        {
            super(sql);
            sql_ = sql;
            putValue(SHORT_DESCRIPTION, "Executes the SQL statement");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            sqlArea_.setText(sql_);
            new ExecuteAction().actionPerformed(e);
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
        CtrlUpAction()
        {
            super("Scroll History Up");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            statusBar_.setStatus("Ctrl-up registered!");
        }
    }
    
    //--------------------------------------------------------------------------
    // ListTablesAction
    //--------------------------------------------------------------------------
    
    /**
     * Queries the DB metadata and dumps a list of the tables.
     */
    class ListTablesAction extends SmartAction
    {
        public ListTablesAction()
        {
            super("List Tables", true, false, null);
        }
        
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
    class ListColumnsAction extends SmartAction
    {
        public ListColumnsAction()
        {
            super("List Columns", true, false, null);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            Connection conn = JDBCUtil.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            String table = resultsArea_.getSelectedText();
            ResultSet rs = meta.getColumns(null, null, table, null);
            String tables = JDBCUtil.format(rs);
            resultsArea_.append(tables);
            JDBCUtil.releaseConnection(conn);
        }
    }
}