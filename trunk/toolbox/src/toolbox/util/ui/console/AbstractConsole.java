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
public abstract class AbstractConsole implements Console, Startable, Nameable
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractConsole.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Default command propmt.
     */
    private String prompt_ = "Yes, master>";

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
        setName(name);
        machine_ = ServiceUtil.createStateMachine(this);

        // Install the default command handler...
        setCommandHandler(new DefaultCommandHandler());
    }

    //--------------------------------------------------------------------------
    // Abstract
    //--------------------------------------------------------------------------

    /**
     * Replaces the current command line with the command indicated.
     * 
     * @param cmd Command replacing the typed command.
     */
    public abstract void setCommandLine(String cmd);


    /**
     * Returns the command this console is sent if the cursor down key is
     * pressed.
     * 
     * @return String
     */
    public abstract String getCursorDownName();


    /**
     * Returns the command this console is sent if the cursor up key is pressed.
     * 
     * @return String
     */
    public abstract String getCursorUpName();

    //--------------------------------------------------------------------------
    // Console Interface
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
     * @see toolbox.util.ui.console.Console#getPrompt()
     */
    public String getPrompt()
    {
        return prompt_;
    }
    
    
    /**
     * @see toolbox.util.ui.console.Console#setPrompt(java.lang.String)
     */
    public void setPrompt(String prompt)
    {
        prompt_ = prompt;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

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
                        renderPrompt();
                        
                        // Use chomp to get rid of the trailing newline if any
                        String s = StringUtils.chomp(read());

                        // Delegate execution of the command to the installed
                        // command handler
                        getCommandHandler().handleCommand(
                            AbstractConsole.this, s);
                    }
                    catch (Exception e)
                    {
                        logger_.error("start", e);
                    }
                }
            }
        }).start();
    }


    /**
     * Stops command loop processor from accepting any more commands.
     * 
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws IllegalStateException, ServiceException
    {
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