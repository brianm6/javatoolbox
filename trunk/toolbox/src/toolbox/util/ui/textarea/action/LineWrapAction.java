package toolbox.util.ui.textarea.action;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartTextArea;

/**
 * Toggles linewrapping in a JSmartTextArea. Comes with an icon associated with
 * the action.
 * 
 * @see toolbox.util.ui.JSmartTextArea
 */    
public class LineWrapAction extends AbstractTextComponentAction 
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a LineWrapAction.
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
     * Just flips the linewrap flag.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        JTextArea ta = (JTextArea) getTextComponent();
        ta.setLineWrap(!ta.getLineWrap());
    }
}