package toolbox.util.ui.textarea.action;

import java.awt.event.ActionEvent;

import javax.swing.text.JTextComponent;

import toolbox.util.ui.ImageCache;

/**
 * Clears the contents of a text component.
 */
public class ClearAction extends AbstractTextComponentAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ClearAction.
     * 
     * @param textComponent Textcomponent to clear.
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
        super(textComponent, name, ImageCache.getIcon(ImageCache.IMAGE_CLEAR)); 
        putValue(MNEMONIC_KEY, new Integer('C'));
        putValue(SHORT_DESCRIPTION, "Clear");
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
        getTextComponent().setText("");
    }
}