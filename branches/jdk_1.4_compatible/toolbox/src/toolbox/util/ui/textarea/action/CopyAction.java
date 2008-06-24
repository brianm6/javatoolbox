package toolbox.util.ui.textarea.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.text.JTextComponent;

import toolbox.util.ui.ImageCache;

/**
 * Copies the contents of the currently selected text to the clipboard.
 */    
public class CopyAction extends AbstractTextComponentAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CopyAction.
     * 
     * @param textComponent Textcomponent to copy text from.
     */
    public CopyAction(JTextComponent textComponent)
    {
        super(textComponent, "Copy");
        putValue(Action.MNEMONIC_KEY, new Integer('C'));
        putValue(Action.SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_COPY));
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
        getTextComponent().copy();
    }
}