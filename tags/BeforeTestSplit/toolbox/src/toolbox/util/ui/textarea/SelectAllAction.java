package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.text.JTextComponent;

import toolbox.util.ui.textarea.action.AbstractTextComponentAction;

/**
 * Selects all items in the list box.
 */
public class SelectAllAction extends AbstractTextComponentAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SelectAllAction.
     * 
     * @param textComponent Textcomponent in which to select all text.
     */
    public SelectAllAction(JTextComponent textComponent)
    {
        super(textComponent, "Select All");
    }
    
    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        getTextComponent().selectAll();
    }
}