package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.textarea.action.AbstractTextComponentAction;

/**
 * Toggles autoscrolling in a JSmartTextArea.
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