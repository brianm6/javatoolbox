package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JFlipPane;
import toolbox.util.ui.JTextComponentPopupMenu;

/**
 * Simple SQL query panel
 */ 
public class QueryPlugin extends JPanel implements IPlugin
{ 
    /** Logger **/ 
    public static final Logger logger_ =
        Logger.getLogger(QueryPlugin.class);   

    /** 
     * Property key for history 
     */
    public static final String KEY_HISTORY = "query.plugin.history";
    
    /** 
     * Property key for driver name 
     */
    public static final String KEY_DRIVER = "query.plugin.driver";
    
    /** 
     * Property key for driver URL 
     */
    public static final String KEY_URL = "query.plugin.url";
    
    /** 
     * Property key for user 
     */
    public static final String KEY_USER = "query.plugin.user";
    
    /** 
     * Property key for password 
     */
    public static final String KEY_PASSWORD = "query.plugin.password";
    
    /**
     * Newline character
     */    
    public static final String NEWLINE = "\n";

    // SQL query & results stuff
    private IStatusBar  statusBar_;    
    private JTextArea   inputArea_;
    private JTextArea   outputArea_;
    private JButton     queryButton_;
    private JButton     clearButton_;
    private JPopupMenu  sqlPopup_;
    private Map         sqlHistory_;
   
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
        buildView();
    }
    
    /**
     * Constructor for QueryPlugin.
     * 
     * @param  statusBar  IStatusBar interface
     */
    public QueryPlugin(IStatusBar statusBar)
    {
        setStatusBar(statusBar);
        buildView(); 
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
                
        inputArea_ = new JTextArea();
        inputArea_.setFont(SwingUtil.getPreferredMonoFont());
        inputArea_.addMouseListener(new PopupListener());
        
        // Wire CTRL-Enter to execute the query
        inputArea_.addKeyListener( new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if ((e.getKeyChar() ==  '\n') && ((KeyEvent.getKeyModifiersText(
                    e.getModifiers()).equals("Ctrl"))))
                        (new RunQueryAction()).actionPerformed(
                            new ActionEvent(inputArea_, 0, "" ));
            }
        });
        
        outputArea_ = new JTextArea();
        outputArea_.setFont(SwingUtil.getPreferredMonoFont());
        JPopupMenu popup = new JTextComponentPopupMenu(outputArea_);
        
        JSplitPane splitPane = 
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(inputArea_), 
                new JScrollPane(outputArea_));

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
     * @return JPanel
     */
    protected JPanel buildConfigView()
    {
        JPanel cp = new JPanel(new GridLayout(4,2));
        
        cp.add(SwingUtil.wrap(new JLabel("Driver")));
        cp.add(SwingUtil.wrap(driverField_ = new JTextField(15)));
       
        cp.add(SwingUtil.wrap(new JLabel("URL")));
        cp.add(SwingUtil.wrap(urlField_ = new JTextField(15)));
        
        cp.add(SwingUtil.wrap(new JLabel("User")));
        cp.add(SwingUtil.wrap(userField_ = new JTextField(15)));
         
        cp.add(SwingUtil.wrap(new JLabel("Password")));
        cp.add(SwingUtil.wrap(passwordField_ = new JTextField(15)));

        JFlipPane jfp = new JFlipPane(JFlipPane.LEFT);
        jfp.addFlipper("JDBC Config", cp);
        jfp.setExpanded(false);
        return jfp;
    }
    
    /**
     * Runs a query against the database and 
     * returns the results in a formatted string
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
                metaResults = JDBCUtil.executeAndFormatQuery(sql);
            }
            else if (lower.startsWith("insert") ||
                     lower.startsWith("delete") ||
                     lower.startsWith("update"))
            {
                metaResults = JDBCUtil.executeUpdate(sql) + " rows affected."; 
            }
            else
            {
                throw new IllegalArgumentException(
                    "Not a valid SQL statement: " + sql);
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

    /**
     * Set status
     * 
     * @param  status  Status text
     */    
    protected void setStatus(String status)
    {
        statusBar_.setStatus(status);
    }

    //--------------------------------------------------------------------------
    //  IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see com.swa.turbo.util.ui.IPlugin#init()
     */
    public void init()
    {
    }

    /**
     * @return The plugin name
     */
    public String getName()
    {
        return "Query";
    }

    /**
     * @return  Visual representation of the plugin. In our case, the query
     *          panel.
     */
    public Component getComponent()
    {
        return this;
    }

    /**
     * @see com.swa.turbo.pos.comm.proxy.ui.IPlugin#getMenu()
     */
    public JMenu getMenu()
    {
        return null;
    }

    /**
     * Saves contents of sqlPopupMenu to a Properties object 
     * 
     * @param  prefs  Preferences object
     */
    public void applyPrefs(Properties prefs)
    {
        String method = "[appPrf] ";
        
        String history = prefs.getProperty(KEY_HISTORY);
        
        if (!StringUtil.isNullOrEmpty(history))
        {            
            String[] historyItems = StringUtil.tokenize(history, "|");
    
            logger_.debug(method + 
                "Restoring " + historyItems.length + "sql stmts to history.");
            
            for (int i=0; i<historyItems.length; i++)
                addToHistory(historyItems[i]);
        }
            
        driverField_.setText(prefs.getProperty(KEY_DRIVER, "??"));
        urlField_.setText(prefs.getProperty(KEY_URL, "??"));
        userField_.setText(prefs.getProperty(KEY_USER, "??"));
        passwordField_.setText(prefs.getProperty(KEY_PASSWORD, "??"));
    }

    /**
     * Restores the of the sqlPopupMenu from a Properties object
     * 
     * @param  prefs  Preferences object
     */
    public void savePrefs(Properties prefs)
    {
        String method = "[savPrf] ";

        // Munge all SQL statements into one string and save
        StringBuffer sb = new StringBuffer("");
        
        for (Iterator i =  sqlHistory_.values().iterator(); i.hasNext(); )
        {
            String sql = (String) i.next();
            sb.append(sql + "|");
        }
        
        prefs.setProperty(KEY_HISTORY, sb.toString());
        prefs.setProperty(KEY_DRIVER, driverField_.getText().trim());
        prefs.setProperty(KEY_URL, urlField_.getText().trim());
        prefs.setProperty(KEY_USER, userField_.getText().trim());
        prefs.setProperty(KEY_PASSWORD, passwordField_.getText().trim());
    }

    /**
     * @see com.swa.turbo.util.ui.IPlugin#setStatusBar(IStatusBar)
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }
    
    /**
     * @see com.swa.turbo.util.ui.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }
    
    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    class PopupListener extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e)
        {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e)
        {
            if (e.isPopupTrigger())
            {
                sqlPopup_.show(e.getComponent(), e.getX(), e.getY());
            }
        }
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
            String sql = inputArea_.getText();        
            
            if (StringUtil.isNullOrEmpty(sql))
            {
                setStatus("Nothing to execute.");
            }
            else
            {
                String results = executeSQL(sql);        
                outputArea_.append(results);
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
            inputArea_.setText(sql_);
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
            outputArea_.setText("");            
        }
    }
}
