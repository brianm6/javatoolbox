package toolbox.util.ui.textarea.action;

import java.awt.event.ActionEvent;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartTextArea;

/**
 * Toggles autoscrolling in a JSmartTextArea.
 * 
 * @see toolbox.util.ui.JSmartTextArea
 */    
public class AutoScrollAction extends AbstractTextComponentAction 
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AutoScrollAction.
     * 
     * @param area Text area to link this action to.
     */
    public AutoScrollAction(JSmartTextArea area)
    {
        super(area, "AutoScroll", ImageCache.getIcon(ImageCache.IMAGE_LOCK));
        
        putValue(
            SHORT_DESCRIPTION, 
            "Toggles autoscrolling of text");
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
        JSmartTextArea ta = (JSmartTextArea) getTextComponent();
        
        ta.setAutoScroll(!ta.isAutoScroll());
        
        if (ta.isAutoScroll())
            ta.scrollToEnd();
    }
}