package toolbox.util.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;

/**
 * Convenience class that removes the need to add a try/catch block to the 
 * actionPerformed() method of your AbstractAction implementations.
 * TryCatchAction catches all exceptions and displays an error with stack trace
 * into to the display. The exception is also logged via Log4J as an ERROR.
 */
public abstract class TryCatchAction extends AbstractAction
{
    private static final Logger logger_ = 
        Logger.getLogger(TryCatchAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TryCatchAction
     * 
     * @param  name  Name of action
     */
    public TryCatchAction(String name)
    {
        super(name);
    }
    
    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ActionListener#
     *      actionPerformed(java.awt.event.ActionEvent)
     */
    public final void actionPerformed(ActionEvent e)
    {
        try
        {
            tryActionPerformed(e);
        }
        catch (Throwable t)
        {
            ExceptionUtil.handleUI(t, logger_);
        }
    }

    //--------------------------------------------------------------------------
    // Abstract 
    //--------------------------------------------------------------------------
    
    /**
     * actionPerformed delegates to this method after surrounding it with a 
     * try/catch block to handle errors.
     */
    public abstract void tryActionPerformed(ActionEvent e) throws Throwable;
}
