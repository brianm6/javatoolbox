package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;

import toolbox.util.ui.ImageCache;

/**
 * Pastes the contents of the clipboard into the text component.
 */    
public class PasteAction extends AbstractAction
{
    /**
     * Text component to paste to.
     */
    private final JTextComponent textComponent_;

    
    /**
     * Creates a PasteAction.
     */
    public PasteAction(JTextComponent textComponent)
    {
        super("Paste");
        textComponent_ = textComponent;
        putValue(Action.MNEMONIC_KEY, new Integer('P'));
        putValue(Action.SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_PASTE));
    }
    
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        textComponent_.paste();
    }
}