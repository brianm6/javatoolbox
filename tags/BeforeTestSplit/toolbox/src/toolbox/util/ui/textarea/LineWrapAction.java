package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.textarea.action.AbstractTextComponentAction;

/**
 * Toggles linewrapping in a JSmartTextArea.
 */    
public class LineWrapAction extends AbstractTextComponentAction 
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AutoScrollAction.
     * 
     * @param area Text area that supports line wrap.
     */
    public LineWrapAction(JSmartTextArea area)
    {
        super(
            area, 
            "Wrap Lines", 
            ImageCache.getIcon(ImageCache.IMAGE_LINEWRAP));
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
        JTextArea ta = (JTextArea) getTextComponent();
        ta.setLineWrap(!ta.getLineWrap());
    }
}