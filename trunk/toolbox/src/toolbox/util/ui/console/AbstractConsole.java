package toolbox.util.ui.console;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.service.Nameable;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.service.Startable;
import toolbox.util.statemachine.StateMachine;

/**
 * Abstract base class for console implementations.
 */
public abstract class AbstractConsole implements Startable, Console, Nameable
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractConsole.class);

    //--------------------------------------------------------------------------
    // Static
    //--------------------------------------------------------------------------

    private static String PROMPT = "CMD>";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Name of this console.
     */
    private String name_;

    /**
     * State machine that keeps track of this console's lifecycle events.
     */
    private StateMachine machine_;

    /**
     * Installed command handler.
     */
    private CommandHandler commandHandler_;


    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a AbstractConsole with the given name.
     * 
     * @param name Name of the console.
     */
    public AbstractConsole(String name)
    {
        machine_ = ServiceUtil.createStateMachine(this);
        name_ = name;

        // Install a command handler by default
        setCommandHandler(new DefaultCommandHandler());
    }

    //--------------------------------------------------------------------------
    // Abstract
    //--------------------------------------------------------------------------

    /**
     * Replaces the current command line with the command indicated
     * 
     * @param cmd the command replacing the typed command
     */
    public abstract void setCommandLine(String cmd);


    /**
     * Returns the command this console send if the cursor down key was pressed.
     * 
     * @return command this console send if the cursor down key was pressed
     */
    public abstract String getCursorDownName();


    /**
     * Returns the command this console send if the cursor up key was pressed.
     * 
     * @return command this console send if the cursor up key was pressed
     */
    public abstract String getCursorUpName();
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Returns the installed command handler or null if one has not been set.
     * 
     * @return CommandHandler
     */
    public CommandHandler getCommandHandler()
    {
        return commandHandler_;
    }


    /**
     * Sets the installed command handler.
     * 
     * @param commandHandler Hander to install.
     */
    public void setCommandHandler(CommandHandler commandHandler)
    {
        commandHandler_ = commandHandler;
    }

    //--------------------------------------------------------------------------
    // Nameable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Returns the name of this console.
     * 
     * @return String
     */
    public String getName()
    {
        return name_;
    }

    
    /**
     * Sets the name of this console.
     * 
     * @param name The name to set.
     */
    public void setName(String name)
    {
        name_ = name;
    }
    
    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------

    /**
     * Start command loop processor in its own thread.
     * 
     * @see toolbox.util.service.Startable#start()
     */
    public void start()
    {
        machine_.transition(ServiceTransition.START);

        new Thread(new Runnable()
        {
            public void run()
            {
                while (isRunning())
                {
                    try
                    {
                        // Use chomp to get rid of the trailing newline if any
                        String s = StringUtils.chomp(read());

                        // Delegate execution of the command to the installed
                        // command handler
                        getCommandHandler().handleCommand(AbstractConsole.this,
                            s);

                        setPrompt(PROMPT);
                    }
                    catch (Exception e)
                    {
                        logger_.error("run", e);
                    }
                }
            }
        }).start();
    }


    /**
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws IllegalStateException, ServiceException
    {
        if (isRunning())
            machine_.transition(ServiceTransition.STOP);
    }


    /**
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return getState() == ServiceState.RUNNING;
    }


    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }
}