package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.text.JTextComponent;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.textarea.action.AbstractTextComponentAction;

/**
 * Pastes the contents of the clipboard into the text component.
 */    
public class PasteAction extends AbstractTextComponentAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PasteAction.
     * 
     * @param textComponent Textcomponent to paste text into. 
     */
    public PasteAction(JTextComponent textComponent)
    {
        super(textComponent, "Paste");
        putValue(Action.MNEMONIC_KEY, new Integer('P'));
        putValue(Action.SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_PASTE));
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
        getTextComponent().paste();
    }
}