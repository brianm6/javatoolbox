package toolbox.workspace;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import toolbox.util.ui.JSmartCheckBoxMenuItem;
import toolbox.util.ui.JSmartMenu;

/**
 * Log4J specific logging menu that allows easy changing of the log level and
 * also the logging output to console, file, or window.
 */
public class LoggingMenu extends JSmartMenu
{
    // TODO: Add logging to window
    // TODO: Add save/restore of prefs
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
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
     * @param level {@link Level} to set
     */
    public void setLogLevel(Level level)
    {
        ((JCheckBoxMenuItem) levelMap_.get(level)).setState(true);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
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
        
        for (int i=0; i<levels.length; i++)
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
    }

    
    /**
     * Initialzes the selection state of the menu.
     */
    protected void init()
    {
        logger_ = Logger.getLogger(LOGGER_TOOLBOX);
        consoleItem_.setSelected(logger_.getAppender(APPENDER_CONSOLE) != null);
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
        ConsoleAppender appender_;
        
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
}