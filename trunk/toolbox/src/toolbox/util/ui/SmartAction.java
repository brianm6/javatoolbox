package toolbox.util.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;

/**
 * Convenience class that removes the need to add a try/catch block to the 
 * actionPerformed() method of your AbstractAction implementations.
 * SmartAction catches all exceptions and displays an error with stack trace
 * into to the display. The exception is also logged via Log4J as an ERROR.
 */
public abstract class SmartAction extends AbstractAction
{
    private static final Logger logger_ = 
        Logger.getLogger(SmartAction.class);
    
    
    private boolean async_;
    private JComponent scope_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SmartAction
     * 
     * @param  name  Name of action
     */
    public SmartAction(String name, boolean async, JComponent scope)
    {
        super(name);
        async_ = async;
        scope_ = scope;
    }
    
    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ActionListener#
     *      actionPerformed(java.awt.event.ActionEvent)
     */
    public final void actionPerformed(final ActionEvent e)
    {
        Runnable r = new Runnable() {

            public void run()
            {
                try
                {
                    SwingUtil.setWaitCursor(scope_);

                    SmartAction.this.runAction(e);
                }
                catch (Throwable t)
                {
                    ExceptionUtil.handleUI(t, logger_);    
                }
                finally
                {
                    SwingUtil.setDefaultCursor(scope_);
                }
            }
        };

        if (!async_)
        {
            r.run();            
        }
        else
        {
            new Thread(r).start();
        }
    }

    //--------------------------------------------------------------------------
    // Abstract 
    //--------------------------------------------------------------------------
    
        
    /**
     * actionPerformed delegates to this method after surrounding it with a 
     * try/catch block to handle errors.
     */
    public abstract void runAction(ActionEvent e) throws Exception;
}
