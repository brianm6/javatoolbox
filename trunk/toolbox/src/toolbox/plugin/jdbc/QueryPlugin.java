package toolbox.jdbc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import org.jedit.syntax.KeywordMap;
import org.jedit.syntax.SQLTokenMarker;
import org.jedit.syntax.TextAreaDefaults;

import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.util.ExceptionUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.PropertiesUtil;
import toolbox.util.StringUtil;
import toolbox.util.Stringz;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JConveyorPopupMenu;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JTextComponentPopupMenu;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.PluginWorkspace;
import toolbox.util.ui.plugin.WorkspaceAction;

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
    /*
     * TODO: create SQLDefaults for syntax hiliting
     * TODO: Ctrl-Up/Down should scroll through query history
     * TODO: Move num rows to top of display and stop scrolling down to bottom
     * TODO: Remberber database profiles and make them user selectable
     */
     
    public static final Logger logger_ =
        Logger.getLogger(QueryPlugin.class);   

    /** Prefix for embedded configurable components */
    public static final String PROP_PREFIX = "query";
    
    /** Property key for SQL history */
    public static final String PROP_HISTORY = "query.plugin.history";

    /** Max number of entries in sql history popup menu */
    public static final String PROP_HISTORY_MAX = "query.plugin.history.max";
    
//    /** Property key for JDBC driver name */
//    public static final String PROP_DRIVER = "query.plugin.driver";
//    
//    /** Property key for JDBC driver URL */
//    public static final String PROP_URL = "query.plugin.url";
//    
//    /** Property key for JDBC user */
//    public static final String PROP_USER = "query.plugin.user";
//    
//    /** Property key for JDBC password */
//    public static final String PROP_PASSWORD = "query.plugin.password";
    
    /** Property key for the contents of the SQL text area */
    public static final String PROP_CONTENTS = "query.plugin.contents";
    
    public static final String PROP_PROFILES = "query.plugin.profiles";    
    
    /** Status bar of plugin host */
    private IStatusBar statusBar_;
    
    /** Text area for entering sql statements */    
    private JEditTextArea sqlArea_;
    
    /** Text are for sql execution results */
    private JSmartTextArea resultsArea_;
    
    /** Invokes execution of sql command */
    private JButton queryButton_;
    
    /** Clears the contents of the sql results area */
    private JButton clearButton_;
    
    /** Flippane which houses the jdbc configuration panel */
    private JFlipPane leftFlipPane_;
    
    /** Popup menu that contains a history of recently executed sql */
    private JConveyorPopupMenu sqlPopup_;    
    
    /** Maps sqlpopup_ menu items to the actual sql text */
    private Map sqlHistory_;

    /** Database configuration panel */
    private DBConfig dbConfigPane_;

    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default Constructor
     */
    public QueryPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // Package Protected 
    //--------------------------------------------------------------------------
        
    /**
     * Returns the status bar
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
     * Builds the GUI
     */
    protected void buildView()
    {
        sqlHistory_ = new HashMap();
        sqlPopup_ = new JConveyorPopupMenu();

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
        new JTextComponentPopupMenu(resultsArea_);
        
        JSplitPane splitPane = 
            new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(sqlArea_), 
                new JScrollPane(resultsArea_));

        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout());
            
        queryButton_ = new JButton(new ExecuteAction());
        buttonPanel.add(queryButton_);

        clearButton_ = new JButton(new ClearAction());
        buttonPanel.add(clearButton_);

        // Root 
        setLayout(new BorderLayout());
        
        dbConfigPane_ = new DBConfig(this);
        leftFlipPane_ = new JFlipPane(JFlipPane.LEFT);
        leftFlipPane_.addFlipper("JDBC Drivers", dbConfigPane_);
        
        add(leftFlipPane_, BorderLayout.WEST);
        add(splitPane, BorderLayout.CENTER);                
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    
    /**
     * Runs a query against the database and returns the results as a nicely 
     * formatted string.
     * 
     * @param  sql  SQL query
     * @return Formatted results
     * @see    JDBCUtil#format(ResultSet)
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
     * Adds a sql statement to the popup menu history
     * 
     * @param  sql  SQL statement to add to the history
     */
    protected void addToHistory(String sql)
    {
        if (!sqlHistory_.containsValue(sql))
        {   
            sqlHistory_.put(sql, sql);
            JMenuItem menuItem = new JMenuItem(new ExecutePriorAction(sql));
            sqlPopup_.add(menuItem);
        }
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    public void startup(Map params)
    {
        if (params != null)
            statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
            
        buildView();
    }

    public String getName()
    {
        return "JDBC Query";
    }

    public JComponent getComponent()
    {
        return this;
    }

    public String getDescription()
    {
        return "Simple SQL driven interface to a JDBC accessible database.";
    }
    
	public void shutdown()
	{
	}

	//--------------------------------------------------------------------------
	// IPreferenced Interface
	//--------------------------------------------------------------------------

    public void applyPrefs(Properties prefs)
    {
        // Restore sql history
        sqlPopup_.setCapacity(
            PropertiesUtil.getInteger(prefs, PROP_HISTORY_MAX, 10));
        
        String[] historyItems = 
            StringUtil.tokenize(prefs.getProperty(PROP_HISTORY,""), "|");
            
        logger_.debug("Restoring " + historyItems.length + " saved sql stmts");
        
        for (int i=0; i<historyItems.length; i++)
            addToHistory(historyItems[i]);
        
        sqlArea_.setText(prefs.getProperty(PROP_CONTENTS, ""));
        leftFlipPane_.applyPrefs(prefs, PROP_PREFIX);
        
        try
        {
            dbConfigPane_.applyPrefs(prefs);
        }
        catch (IOException e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
    }

    public void savePrefs(Properties prefs)
    {
        // Munge all SQL statements into one string and save
        StringBuffer sb = new StringBuffer("");
        
        for (Iterator i =  sqlHistory_.values().iterator(); i.hasNext(); )
            sb.append(i.next().toString() + "|");
        
        prefs.setProperty(PROP_HISTORY,  sb.toString());
        prefs.setProperty(PROP_CONTENTS, sqlArea_.getText().trim());
        leftFlipPane_.savePrefs(prefs, PROP_PREFIX);
        dbConfigPane_.savePrefs(prefs);
    }
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------
    
    /**
     * Runs the query and appends the results to the output text area
     */
    private class ExecuteAction extends WorkspaceAction
    {
        public ExecuteAction()
        {
            super("Execute SQL", true, QueryPlugin.this, statusBar_);
            putValue(MNEMONIC_KEY, new Integer('E'));
            putValue(SHORT_DESCRIPTION, "Executes the SQL statement");
        }

        public void runAction(ActionEvent e) throws Exception
        {
            String sql = sqlArea_.getText();
            
            if (StringUtil.isNullOrBlank(sql))
                statusBar_.setStatus("Enter SQL to execute");
            else
            {
                statusBar_.setStatus("Executing...");
                String results = executeSQL(sqlArea_.getText());
                resultsArea_.append(results);
                
                if (StringUtil.tokenize(results, Stringz.NL).length < 50)
                    resultsArea_.scrollToEnd();
                    
                statusBar_.setStatus("Done");
            }
        }
    }

    /**
     * Runs the query and appends the results to the output text area
     */
    private class ExecuteCurrentAction extends WorkspaceAction
    {
        public ExecuteCurrentAction()
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
                
                if (StringUtil.tokenize(results, Stringz.NL).length < 50)
                    resultsArea_.scrollToEnd();

                statusBar_.setInfo("Done");
            }
        }
    }

    /**
     * Runs the query selected from the SQL history popup menu
     */
    private class ExecutePriorAction extends AbstractAction
    {
        private String sql_;
        
        public ExecutePriorAction(String sql)
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
    
    /**
     * Clears the output text area
     */
    private class ClearAction extends AbstractAction
    {
        public ClearAction()
        {
            super("Clear");
            putValue(MNEMONIC_KEY, new Integer('C'));
            putValue(SHORT_DESCRIPTION, "Clears the output");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            resultsArea_.setText("");            
        }
    }

    /**
     * Ctrl-Up Key action
     */
    private class CtrlUpAction extends AbstractAction
    {
        public CtrlUpAction()
        {
            super("Scroll History Up");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            statusBar_.setStatus("Ctrl-up registered!");
        }
    }
}


// Wire CTRL-Enter to execute the query
//sqlArea_.getPainter().addKeyListener( new KeyAdapter()
//{
//    public void keyTyped(KeyEvent e)
//    {
//        if ((e.getKeyChar() ==  '\n') && ((KeyEvent.getKeyModifiersText(
//            e.getModifiers()).equals("Ctrl"))))
//                (new ExecuteAction()).actionPerformed(
//                    new ActionEvent(sqlArea_, 0, "" ));
//    }
//});
        
