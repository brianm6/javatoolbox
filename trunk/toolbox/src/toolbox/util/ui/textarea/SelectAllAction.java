package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;

/**
 * Selects all items in the list box.
 */
public class SelectAllAction extends AbstractAction
{
    /**
     * Text component in which to select all the text.
     */
    private final JTextComponent textComponent_;


    /**
     * Creates a SelectAllAction.
     */
    public SelectAllAction(JTextComponent textComponent)
    {
        super("Select All");
        textComponent_ = textComponent;
    }
    
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        textComponent_.selectAll();
    }
}