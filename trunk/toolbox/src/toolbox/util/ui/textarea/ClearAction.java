package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;

/**
 * Clears the contents of a text component.
 */
public class ClearAction extends AbstractAction
{
    /**
     * Target text component.
     */
    private final JTextComponent textComponent_;

    
    /**
     * Creates a ClearAction.
     */
    public ClearAction(JTextComponent textComponent)
    {
        this(textComponent, "Clear");
    }
    
    
    /**
     * Creates a ClearAction.
     * 
     * @param textComponent Text component.
     * @param name Label of the action target.
     */
    public ClearAction(JTextComponent textComponent, String name)
    {
        super(name);
        textComponent_ = textComponent;
        putValue(MNEMONIC_KEY, new Integer('C'));
        putValue(SHORT_DESCRIPTION, "Clears");
    }
    
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        textComponent_.setText("");
    }
}