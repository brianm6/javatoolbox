package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartTextArea;

/**
 * Toggles linewrapping in a JSmartTextArea.
 */    
public class LineWrapAction extends AbstractAction 
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
	/**
     * Text area to mutate passed in at time of construction.
     */
    private final JSmartTextArea area_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AutoScrollAction.
     */
    public LineWrapAction(JSmartTextArea area)
    {
        super("Wrap Lines", ImageCache.getIcon(ImageCache.IMAGE_LINEWRAP));
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
        area_.setLineWrap(!area_.getLineWrap());
    }
}