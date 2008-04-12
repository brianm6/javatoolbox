package toolbox.log4j;

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

import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartCheckBoxMenuItem;
import toolbox.util.ui.JSmartFrame;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartTextArea;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * Log4J specific logging menu that provides useful functionality. Perfect for
 * adding to an existing application. Just instantiate and add to an existing
 * JMenu or JMenuBar. Features include:
 * 
 * <ul>
 *  <li>Change the logger level
 *  <li>Append log output to stdout/stderr (console)
 *  <li>Pop up a new window and send logger output to it.
 *  <li>Append log output to a file.
 *  <li>Saves/restores menu selection state.
 * </ul>
 */
public class LoggingMenu extends JSmartMenu implements IPreferenced {

    private static final Logger logger = Logger.getLogger(LoggingMenu.class);
    
    //--------------------------------------------------------------------------
    // XML Constants
    //--------------------------------------------------------------------------

    private static final String NODE_LOGGING_MENU     = "LoggingMenu";
    private static final String   ATTR_LEVEL          =   "level";
    private static final String   ATTR_LOG_TO_CONSOLE =   "logToConsole";
    private static final String   ATTR_LOG_TO_WINDOW  =   "logToWindow";
    
    //--------------------------------------------------------------------------
    // Default Constants
    //--------------------------------------------------------------------------
    
    /**
     * The default log level is ERROR.
     */
    private static final String  DEFAULT_LEVEL = Level.ERROR.toString();
    
    /**
     * Logging to the console is turned off by default.
     */
    private static final boolean DEFAULT_LOG_TO_CONSOLE = false;
    
    /**
     * Logging to window is turned off by default.
     */
    private static final boolean DEFAULT_LOG_TO_WINDOW = false;
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Name of the toolbox logger in /resources/log4j.xml.
     */
    public static final String LOGGER_TOOLBOX = "toolbox";
    
    /**
     * Name of the console appender in /resources/log4j.xml.
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
    
    public LoggingMenu() {
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
    public void setLogLevel(Level level) {
        try {
            ((JCheckBoxMenuItem) levelMap_.get(level)).setState(true);
        }
        catch (NullPointerException npe) {
            logger.warn("Tried to set logging level before UI realized.");
        }
    }

    
    /**
     * Returns the currently selected log level.
     * 
     * @return Level.
     */
    public Level getLogLevel() {
        
        for (Iterator i = levelMap_.keySet().iterator(); i.hasNext();) {
            Level level = (Level) i.next();
            JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) levelMap_.get(level);
            
            if (cbmi.isSelected())
                return level;
        }

        logger_.warn("None of the log levels were selected in the logging menu.");
        return Level.DEBUG;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Initialzes the selection state of the menu.
     */
    protected void init() {
        logger_ = Logger.getLogger(LOGGER_TOOLBOX);
        consoleItem_.setSelected(logger_.getAppender(APPENDER_CONSOLE) != null);
    }

    
    /**
     * Builds the menus contents.
     */
    protected void buildView() {
        
        setMnemonic('L');
        
        ButtonGroup group = new ButtonGroup();
        
        Level[] levels = new Level[] {
            Level.ALL, 
            Level.TRACE,
            Level.DEBUG, 
            Level.INFO, 
            Level.ERROR, 
            Level.FATAL, 
            Level.OFF
        };

        levelMap_ = new HashMap(levels.length);
        
        for (int i = 0; i < levels.length; i++) {
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
    
    /*
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException {
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
    
    
    /*
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException {
        Element root = new Element(NODE_LOGGING_MENU);
        root.addAttribute(new Attribute(ATTR_LEVEL, getLogLevel().toString()));
        root.addAttribute(new Attribute(ATTR_LOG_TO_CONSOLE, consoleItem_.isSelected() + ""));
        root.addAttribute(new Attribute(ATTR_LOG_TO_WINDOW, windowItem_.isSelected() + ""));
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
        /**
         * Current logging level.
         */
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
    class LogToConsoleAction extends AbstractAction {
        /**
         * Reference to the console appender.
         */
        private ConsoleAppender appender_;
        
        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        LogToConsoleAction() {
            super("Log to Console");
        }
        
        //----------------------------------------------------------------------
        // ActionListener Interface
        //----------------------------------------------------------------------
        
        public void actionPerformed(ActionEvent ae) {
            
            if (logger_.getAppender(APPENDER_CONSOLE) == null) {
                
                // Appender never existed in the first place from log4j.xml
                if (appender_ == null) {
                    appender_ = new ConsoleAppender();
                    appender_.setTarget("System.out");
                    appender_.setName(APPENDER_CONSOLE);
                    appender_.setLayout(new PatternLayout());
                    appender_.activateOptions();
                }

                logger_.addAppender(appender_);
            }
            else {
                appender_ = (ConsoleAppender) logger_.getAppender(APPENDER_CONSOLE);
                logger_.removeAppender(APPENDER_CONSOLE);
            }
        }
    }
    
    // --------------------------------------------------------------------------
    // LogToWindowAction
    //--------------------------------------------------------------------------

    /**
     * Action to send log output to a window.
     */
    class LogToWindowAction extends AbstractAction {
        
        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        LogToWindowAction() {
            super("Log to Window");
        }
        
        //----------------------------------------------------------------------
        // ActionListener Interface
        //----------------------------------------------------------------------
        
        public void actionPerformed(ActionEvent ae) {
            
            if (loggingWindow_ == null) {
                loggingWindow_ = new LoggingWindow();
                loggingWindow_.setVisible(true);
            }
            else {
                loggingWindow_.dispose();
                loggingWindow_ = null;
            }
        }
        
        // ----------------------------------------------------------------------
        // LoggingWindow
        //----------------------------------------------------------------------
        
        /**
         * Log output is sent to the text area embedded in this window.
         */
        class LoggingWindow extends JSmartFrame {
            
            //------------------------------------------------------------------
            // Fields
            //------------------------------------------------------------------
            
            /**
             * Logging output sent to this text area.
             */
            private JSmartTextArea area_;
            
            /**
             * Bridges a LOG4J appender and the text area.
             */
            private JTextAreaAppender appender_;
            
            //------------------------------------------------------------------
            // Constructors
            //------------------------------------------------------------------
            
            public LoggingWindow() {
                super("Logging Console");
                buildView();
                setSize(300, 400);
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                addWindowListener(new WindowListener());
            }
    
            //------------------------------------------------------------------
            // Builds UI 
            //------------------------------------------------------------------
            
            protected void buildView() {
                area_ = new JSmartTextArea(true, SwingUtil.getDefaultAntiAlias());
                area_.setFont(FontUtil.getPreferredMonoFont());
                appender_ = new JTextAreaAppender(area_);
                logger_.addAppender(appender_);
                Container cp = getContentPane();
                cp.setLayout(new BorderLayout());
                cp.add(new JScrollPane(area_), BorderLayout.CENTER);
            }
            
            //------------------------------------------------------------------
            // WindowListener
            //------------------------------------------------------------------
            
            /**
             * On window close, the appender is removed from the logger and then
             * closed.
             */
            class WindowListener extends WindowAdapter {
                
                public void windowClosing(WindowEvent e) {
                    logger_.removeAppender(appender_);
                    appender_.close();
                    windowItem_.setSelected(false);
                    loggingWindow_ = null;
                }
            }
        }        
    }
}