package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;

import toolbox.util.ui.ImageCache;

/**
 * Copies the contents of the currently selected indices to the clipboard.
 */    
public class CopyAction extends AbstractAction
{
    /**
     * Text component to copy from.
     */
    private final JTextComponent textComponent_;


    /**
     * Creates a CopyAction.
     */
    public CopyAction(JTextComponent textComponent)
    {
        super("Copy");
        textComponent_ = textComponent;
        putValue(Action.MNEMONIC_KEY, new Integer('C'));
        putValue(Action.SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_COPY));
    }
    
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        textComponent_.copy();
    }
}