package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
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
import toolbox.util.ui.JTextComponentPopupMenu;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * Simple SQL query Plugin
 * 
 * <pre>
 * TODO: create SQLDefaults for syntax hiliting
 * <pre>
 */ 
public class QueryPlugin extends JPanel implements IPlugin
{ 
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
    //  Private
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
        
        // Wire CTRL-Enter to execute the query
        sqlArea_.addKeyListener( new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if ((e.getKeyChar() ==  '\n') && ((KeyEvent.getKeyModifiersText(
                    e.getModifiers()).equals("Ctrl"))))
                        (new RunQueryAction()).actionPerformed(
                            new ActionEvent(sqlArea_, 0, "" ));
            }
        });
        
        resultsArea_ = new JTextArea();
        resultsArea_.setFont(SwingUtil.getPreferredMonoFont());
        new JTextComponentPopupMenu(resultsArea_);
        
        JSplitPane splitPane = 
            new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(sqlArea_), 
                new JScrollPane(resultsArea_));

        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout());
            
        queryButton_ = new JButton(new RunQueryAction());
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
     * Builds the Configuration view in a Flip pane
     * 
     * @return JPanel containing the Configuration UI
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
     * Runs a query against the database and returns the results in a 
     * formatted string
     * 
     * @param  sql  SQL query
     * @return Formatted results
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
                throw new IllegalArgumentException(
                    "SQL statement not supported: " + sql);
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
            JMenuItem menuItem = new JMenuItem(new RunHistoryQueryAction(sql));
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

    public JMenuBar getMenuBar()
    {
        return null;
    }

    public void applyPrefs(Properties prefs)
    {
        String history = prefs.getProperty(PROP_HISTORY);
        
        if (!StringUtil.isNullOrEmpty(history))
        {            
            String[] historyItems = StringUtil.tokenize(history, "|");
    
            logger_.debug(
                "Restoring " + historyItems.length + "sql stmts to history.");
            
            for (int i=0; i<historyItems.length; i++)
                addToHistory(historyItems[i]);
        }
            
        driverField_.setText(prefs.getProperty(PROP_DRIVER, ""));
        urlField_.setText(prefs.getProperty(PROP_URL, ""));
        userField_.setText(prefs.getProperty(PROP_USER, ""));
        passwordField_.setText(prefs.getProperty(PROP_PASSWORD, ""));
        
        leftFlipPane_.applyPrefs(prefs, PROP_PREFIX);
    }

    public void savePrefs(Properties prefs)
    {
        // Munge all SQL statements into one string and save
        StringBuffer sb = new StringBuffer("");
        
        for (Iterator i =  sqlHistory_.values().iterator(); i.hasNext(); )
        {
            String sql = (String) i.next();
            sb.append(sql + "|");
        }
        
        prefs.setProperty(PROP_HISTORY, sb.toString());
        prefs.setProperty(PROP_DRIVER, driverField_.getText().trim());
        prefs.setProperty(PROP_URL, urlField_.getText().trim());
        prefs.setProperty(PROP_USER, userField_.getText().trim());
        prefs.setProperty(PROP_PASSWORD, passwordField_.getText().trim());
        
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
     * Runs the query
     */
    private class RunQueryAction extends AbstractAction
    {
        public RunQueryAction()
        {
            super("Execute SQL");
            putValue(MNEMONIC_KEY, new Integer('E'));
            putValue(SHORT_DESCRIPTION, "Executes the SQL statement");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            String sql = sqlArea_.getText();        
            
            if (StringUtil.isNullOrEmpty(sql))
            {
                statusBar_.setStatus("Nothing to execute.");
            }
            else
            {
                String results = executeSQL(sql);        
                resultsArea_.append(results);
            }
        }
    }

    /**
     * Runs the query off the history popup menu
     */
    private class RunHistoryQueryAction extends AbstractAction
    {
        private String sql_;
        
        public RunHistoryQueryAction(String sql)
        {
            super(sql);
            sql_ = sql;
            putValue(SHORT_DESCRIPTION, "Executes the SQL statement");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            sqlArea_.setText(sql_);
            new RunQueryAction().actionPerformed(e);
        }
    }
    
    /**
     * Clears the output
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