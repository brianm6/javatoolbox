package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import toolbox.util.ui.JSmartTextArea;


/**
 * Toggles autoscrolling in a JSmartTextArea.
 */    
public class AutoScrollAction extends AbstractAction 
{
	/**
     * Text area.
     */
    private final JSmartTextArea area_;


    /**
     * Creates a AutoScrollAction.
     */
    public AutoScrollAction(JSmartTextArea area)
    {
        super("AutoScroll");
        area_ = area;
    }
    
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (area_.isAutoScroll())
            area_.scrollToEnd(); 
    }
}