package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.jedit.syntax.KeywordMap;
import org.jedit.syntax.SQLTokenMarker;
import org.jedit.syntax.TextAreaDefaults;

import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.util.ExceptionUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.ui.TryCatchAction;
import toolbox.util.ui.JTextComponentPopupMenu;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.layout.ParagraphLayout;

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
     * TODO: find busy cursor that goes down ui hierarchy
     */
     
    public static final Logger logger_ =
        Logger.getLogger(QueryPlugin.class);   

    /** Prefix for embedded configurable components */
    public static final String PROP_PREFIX = "query";
    
    /** Property key for SQL history */
    public static final String PROP_HISTORY = "query.plugin.history";
    
    /** Property key for JDBC driver name */
    public static final String PROP_DRIVER = "query.plugin.driver";
    
    /** Property key for JDBC driver URL */
    public static final String PROP_URL = "query.plugin.url";
    
    /** Property key for JDBC user */
    public static final String PROP_USER = "query.plugin.user";
    
    /** Property key for JDBC password */
    public static final String PROP_PASSWORD = "query.plugin.password";
    
    /** Property key for the contents of the SQL text area */
    public static final String PROP_CONTENTS = "query.plugin.contents";
    
    // SQL query & results stuff
    private IStatusBar      statusBar_;    
    private JEditTextArea   sqlArea_;
    private JTextArea       resultsArea_;
    private JButton         queryButton_;
    private JButton         clearButton_;
    private JPopupMenu      sqlPopup_;
    private Map             sqlHistory_;
    private JFlipPane       leftFlipPane_;
    
    // JDBC config stuff 
    private JTextField driverField_;
    private JTextField urlField_;
    private JTextField userField_;
    private JTextField passwordField_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default Constructor
     */
    public QueryPlugin()
    {
    }
    
    //--------------------------------------------------------------------------
    //  Protected
    //--------------------------------------------------------------------------
    
    /** 
     * Builds the GUI
     */
    protected void buildView()
    {
        sqlHistory_ = new HashMap();
        sqlPopup_ = new JPopupMenu("History");

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
                         
        resultsArea_ = new JTextArea();
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
        add(buildConfigView(), BorderLayout.WEST);
        add(splitPane, BorderLayout.CENTER);                
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Builds the panel which displays all the JDBC configuration information 
     * 
     * @return JDBC configuration panel
     */
    protected JPanel buildConfigView()
    {
        JPanel cp = new JPanel(new ParagraphLayout());
        
        cp.add(new JLabel("Driver"), ParagraphLayout.NEW_PARAGRAPH);
        cp.add(driverField_ = new JTextField(15));
       
        cp.add(new JLabel("URL"), ParagraphLayout.NEW_PARAGRAPH);
        cp.add(urlField_ = new JTextField(15));
        
        cp.add(new JLabel("User"), ParagraphLayout.NEW_PARAGRAPH);
        cp.add(userField_ = new JTextField(15));
         
        cp.add(new JLabel("Password"), ParagraphLayout.NEW_PARAGRAPH);
        cp.add(passwordField_ = new JTextField(15));

        cp.add(new JButton(new ConnectAction()), ParagraphLayout.NEW_PARAGRAPH);

        leftFlipPane_ = new JFlipPane(JFlipPane.LEFT);
        leftFlipPane_.addFlipper("JDBC Config", cp);
        return leftFlipPane_;
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
    //  IPlugin Interface
    //--------------------------------------------------------------------------

    public void init()
    {
        buildView();
    }

    public String getName()
    {
        return "JDBC Query";
    }

    public Component getComponent()
    {
        return this;
    }

    public String getDescription()
    {
        return "Simple SQL driven interface to a JDBC accessible database.";
    }

    public void applyPrefs(Properties prefs)
    {
        String history = prefs.getProperty(PROP_HISTORY);
        
        if (!StringUtil.isNullOrEmpty(history))
        {            
            String[] historyItems = StringUtil.tokenize(history, "|");
    
            logger_.debug(
                "Restoring " + historyItems.length + " saved sql stmts");
            
            for (int i=0; i<historyItems.length; i++)
                addToHistory(historyItems[i]);
        }
            
        driverField_.setText(prefs.getProperty(PROP_DRIVER, ""));
        urlField_.setText(prefs.getProperty(PROP_URL, ""));
        userField_.setText(prefs.getProperty(PROP_USER, ""));
        passwordField_.setText(prefs.getProperty(PROP_PASSWORD, ""));
        sqlArea_.setText(prefs.getProperty(PROP_CONTENTS, ""));
        leftFlipPane_.applyPrefs(prefs, PROP_PREFIX);
    }

    public void savePrefs(Properties prefs)
    {
        // Munge all SQL statements into one string and save
        StringBuffer sb = new StringBuffer("");
        
        for (Iterator i =  sqlHistory_.values().iterator(); i.hasNext(); )
            sb.append(i.next().toString() + "|");
        
        prefs.setProperty(PROP_HISTORY,  sb.toString());
        prefs.setProperty(PROP_DRIVER,   driverField_.getText().trim());
        prefs.setProperty(PROP_URL,      urlField_.getText().trim());
        prefs.setProperty(PROP_USER,     userField_.getText().trim());
        prefs.setProperty(PROP_PASSWORD, passwordField_.getText().trim());
        prefs.setProperty(PROP_CONTENTS, sqlArea_.getText().trim());
           
        leftFlipPane_.savePrefs(prefs, PROP_PREFIX);
    }

    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }
    
    public void shutdown()
    {
    }
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------
    
    /**
     * Runs the query and appends the results to the output text area
     */
    private class ExecuteAction extends TryCatchAction implements Runnable
    {
        public ExecuteAction()
        {
            super("Execute SQL");
            putValue(MNEMONIC_KEY, new Integer('E'));
            putValue(SHORT_DESCRIPTION, "Executes the SQL statement");
        }
    
        public void tryActionPerformed(ActionEvent e)
        {
            if (StringUtil.isNullOrBlank(sqlArea_.getText()))
                statusBar_.setStatus("Enter SQL to execute");
            else
                ThreadUtil.run(this, "run", null);
        }
        
        public void run()
        {
            try
            {   
                statusBar_.setStatus("Executing...");
                SwingUtil.setWaitCursor(getComponent());
                String results = executeSQL(sqlArea_.getText());        
                resultsArea_.append(results);
            }
            finally
            {
                SwingUtil.setDefaultCursor(getComponent());
                statusBar_.setStatus("Done");
            }
        }
    }

    /**
     * Runs the query and appends the results to the output text area
     */
    private class ExecuteCurrentAction extends TryCatchAction implements Runnable
    {
        public ExecuteCurrentAction()
        {
            super("Execute Current Statement");
        }
    
        public void tryActionPerformed(ActionEvent e)
        {
            if (StringUtil.isNullOrBlank(sqlArea_.getText()))
                statusBar_.setStatus("Enter SQL to execute");
            else
                ThreadUtil.run(this, "run", null);
        }
        
        public void run()
        {
            try
            {   
                String sql = sqlArea_.getLineText(sqlArea_.getCaretLine());
                statusBar_.setStatus("Executing...");
                SwingUtil.setWaitCursor(getComponent());
                String results = executeSQL(sql);        
                resultsArea_.append(results);
            }
            finally
            {
                SwingUtil.setDefaultCursor(getComponent());
                statusBar_.setStatus("Done");
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
    
    /**
     * Connects to the database
     */
    class ConnectAction extends AbstractAction
    {
        public ConnectAction()
        {
            super("Connect");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JDBCUtil.init(
                    driverField_.getText(),
                    urlField_.getText(),
                    userField_.getText(),
                    passwordField_.getText());
                    
                statusBar_.setStatus("Connected to DB!");
            }
            catch (Throwable se)
            {
                statusBar_.setStatus("Connect failed: " + se.getMessage());
                ExceptionUtil.handleUI(se, logger_);
                
            }
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
        
