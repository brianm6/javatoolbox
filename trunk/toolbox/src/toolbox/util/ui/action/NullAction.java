package toolbox.util.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * NullAction is for those times when no action is a good action.
 */
public class NullAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a NullAction.
     */
    public NullAction()
    {
    }


    /**
     * Creates a NullAction.
     * 
     * @param name Name of the action.
     */
    public NullAction(String name)
    {
        super(name);
    }


    /**
     * Creates a NullAction.
     * 
     * @param name Name of this action.
     * @param icon Icon of this action.
     */
    public NullAction(String name, Icon icon)
    {
        super(name, icon);
    }

    //--------------------------------------------------------------------------
    // ActinoListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
    }
}