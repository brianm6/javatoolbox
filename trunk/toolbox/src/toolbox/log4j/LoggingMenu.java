package toolbox.workspace;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import toolbox.log4j.JTextAreaAppender;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartCheckBoxMenuItem;
import toolbox.util.ui.JSmartFrame;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartTextArea;

/**
 * Log4J specific logging menu that allows easy changing of the log level and
 * also the logging output to console, file, or window.
 */
public class LoggingMenu extends JSmartMenu implements IPreferenced
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    // Preferences
    private static final String NODE_LOGGING_MENU     = "LoggingMenu";
    private static final String   ATTR_LEVEL          = "level";
    private static final String   ATTR_LOG_TO_CONSOLE = "logToConsole";
    private static final String   ATTR_LOG_TO_WINDOW  = "logToWindow";
    
    // Default preferences
    private static final String DEFAULT_LEVEL = Level.ERROR.toString();
    private static final boolean DEFAULT_LOG_TO_CONSOLE = false;
    private static final boolean DEFAULT_LOG_TO_WINDOW = false;
    
    /**
     * Name of the toolbox logger.
     */
    private static final String LOGGER_TOOLBOX = "toolbox";
    
    /**
     * Name of the console appender.
     */
    private static final String APPENDER_CONSOLE = "console";

    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Maps Log Level -> JCheckBoxMenuItem.
     */
    private Map levelMap_;
    
    /**
     * Workspace logger.
     */
    private Logger logger_;

    /**
     * Check box to toggle logging to console.
     */
    private JCheckBoxMenuItem consoleItem_;

    /**
     * Check box to toggle logging to window.
     */
    private JCheckBoxMenuItem windowItem_;

    /**
     * Window containing textarea to which log output is redirected.
     */
    private LogToWindowAction.LoggingWindow loggingWindow_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a {@link LoggingMenu}.
     */
    public LoggingMenu()
    {
        super("Logging");
        buildView();
        init();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets the logging level on the menu.
     * 
     * @param level {@link Level} to set.
     */
    public void setLogLevel(Level level)
    {
        ((JCheckBoxMenuItem) levelMap_.get(level)).setState(true);
    }

    
    /**
     * Returns the currently selected log level.
     * 
     * @return Level.
     */
    public Level getLogLevel()
    {
        for (Iterator i = levelMap_.keySet().iterator(); i.hasNext();)
        {
            Level level = (Level) i.next();
            JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) levelMap_.get(level);
            
            if (cbmi.isSelected())
                return level;
        }

        logger_.warn(
            "None of the log levels were selected in the logging menu.");
        
        return Level.DEBUG;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Initialzes the selection state of the menu.
     */
    protected void init()
    {
        logger_ = Logger.getLogger(LOGGER_TOOLBOX);
        consoleItem_.setSelected(logger_.getAppender(APPENDER_CONSOLE) != null);
    }

    
    /**
     * Builds the menus contents.
     */
    protected void buildView()
    {
        setMnemonic('L');
        
        ButtonGroup group = new ButtonGroup();
        
        Level[] levels = new Level[]
        {
            Level.ALL, 
            Level.DEBUG, 
            Level.INFO, 
            Level.ERROR, 
            Level.FATAL, 
            Level.OFF
        };

        levelMap_ = new HashMap(levels.length);
        
        for (int i = 0; i < levels.length; i++)
        {
            JCheckBoxMenuItem cbmi =
                new JSmartCheckBoxMenuItem(new SetLogLevelAction(levels[i]));
            
            levelMap_.put(levels[i], cbmi);
            group.add(cbmi);
            add(cbmi);
        }
        
        addSeparator();
        
        consoleItem_ = new JSmartCheckBoxMenuItem(new LogToConsoleAction());
        add(consoleItem_);
        
        windowItem_ = new JSmartCheckBoxMenuItem(new LogToWindowAction());
        add(windowItem_);
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_LOGGING_MENU, new Element(NODE_LOGGING_MENU));
        
        String level = XOMUtil.getStringAttribute(
            root, ATTR_LEVEL, Level.ERROR.toString());
        
        setLogLevel(Level.toLevel(level));
        
        consoleItem_.setSelected(
            XOMUtil.getBooleanAttribute(
                root, ATTR_LOG_TO_CONSOLE, DEFAULT_LOG_TO_CONSOLE));
        
        windowItem_.setSelected(
            XOMUtil.getBooleanAttribute(
                root, ATTR_LOG_TO_WINDOW, DEFAULT_LOG_TO_WINDOW));
    }
    
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_LOGGING_MENU);
        root.addAttribute(new Attribute(ATTR_LEVEL, getLogLevel().toString()));
        
        root.addAttribute(new Attribute(ATTR_LOG_TO_CONSOLE, 
            consoleItem_.isSelected() + ""));
        
        root.addAttribute(new Attribute(ATTR_LOG_TO_WINDOW, 
            windowItem_.isSelected() + ""));
        
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // SetLogLevelAction
    //--------------------------------------------------------------------------

    /**
     * Action to set the logging level.
     */
    class SetLogLevelAction extends AbstractAction
    {
        private Level level_;
        
        /**
         * Creates a SetLogLevelAction.
         * 
         * @param level Logging level to activate
         */
        SetLogLevelAction(Level level)
        {
            super(level.toString());
            level_ = level;
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent arg0)
        {
            logger_.setLevel(level_);
        }
    }

    //--------------------------------------------------------------------------
    // LogToConsoleAction
    //--------------------------------------------------------------------------

    /**
     * Action to send log output to the system console.
     */
    class LogToConsoleAction extends AbstractAction
    {
        private ConsoleAppender appender_;
        
        /**
         * Creates a LogToConsoleAction.
         */
        LogToConsoleAction()
        {
            super("Log to Console");
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent ae)
        {
            if (logger_.getAppender(APPENDER_CONSOLE) == null)
            {
                // Appender never existed in the first place from log4j.xml
                if (appender_ ==  null)
                {    
                    appender_ = new ConsoleAppender();
                    appender_.setTarget("System.out");
                    appender_.setName(APPENDER_CONSOLE);
                    appender_.setLayout(new PatternLayout());
                    appender_.activateOptions();
                }
                
                logger_.addAppender(appender_);
            }
            else
            {
                appender_ = (ConsoleAppender) 
                    logger_.getAppender(APPENDER_CONSOLE);
                
                logger_.removeAppender(APPENDER_CONSOLE);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // LogToWindowAction
    //--------------------------------------------------------------------------

    /**
     * Action to send log output to a window.
     */
    class LogToWindowAction extends AbstractAction
    {
        /**
         * Creates a LogToWindowAction.
         */
        LogToWindowAction()
        {
            super("Log to Window");
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent ae)
        {
            if (loggingWindow_ == null)
            {    
                loggingWindow_ = new LoggingWindow();
                loggingWindow_.setVisible(true);
            }
            else
            {
                loggingWindow_.dispose();
                loggingWindow_ = null;
            }
        }
        
        //----------------------------------------------------------------------
        // LoggingWindow
        //----------------------------------------------------------------------
        
        /**
         * Log output is sent to the text area embedded in this window.
         */
        class LoggingWindow extends JSmartFrame
        {
            private JSmartTextArea area_;
            private JTextAreaAppender appender_;
            
            /**
             * Creates a LoggingWindow.
             */
            public LoggingWindow()
            {
                super("Logging Console");
                buildView();
                setSize(300,400);
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                addWindowListener(new WindowListener());
            }
    
            
            /**
             * Constructs the user interface. 
             */
            protected void buildView()
            {
                area_ = 
                    new JSmartTextArea(true, SwingUtil.getDefaultAntiAlias());
                
                appender_ = new JTextAreaAppender(area_);
                logger_.addAppender(appender_);
                
                Container cp = getContentPane();
                cp.setLayout(new BorderLayout());
                cp.add(new JScrollPane(area_), BorderLayout.CENTER);
            }
            
            
            class WindowListener extends WindowAdapter
            {
                /**
                 * @see java.awt.event.WindowAdapter#windowClosing(
                 *      java.awt.event.WindowEvent)
                 */
                public void windowClosing(WindowEvent e)
                {
                    logger_.removeAppender(appender_);
                    appender_.close();
                    windowItem_.setSelected(false);
                    loggingWindow_ = null;
                }
            }
        }        
    }
}