package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartTextArea;

/**
 * Toggles autoscrolling in a JSmartTextArea.
 */    
public class AutoScrollAction extends AbstractAction 
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
	/**
     * Text area.
     */
    private final JSmartTextArea area_;

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
        super("AutoScroll", ImageCache.getIcon(ImageCache.IMAGE_LOCK));
        area_ = area;
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
        area_.setAutoScroll(!area_.isAutoScroll());
        
        if (area_.isAutoScroll())
            area_.scrollToEnd();
    }
}