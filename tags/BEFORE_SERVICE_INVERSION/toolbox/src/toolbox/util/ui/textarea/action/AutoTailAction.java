package toolbox.util.ui.textarea.action;

import java.awt.event.ActionEvent;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartTextArea;

/**
 * Toggles autotailing of output in a JSmartTextArea.
 * 
 * @see toolbox.util.ui.JSmartTextArea
 */    
public class AutoTailAction extends AbstractTextComponentAction 
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AutoTailAction.
     * 
     * @param area Text area to link this action to.
     */
    public AutoTailAction(JSmartTextArea area)
    {
        super(area, "AutoTail", ImageCache.getIcon(ImageCache.IMAGE_LOCK));
        putValue(SHORT_DESCRIPTION, "Toggles autotailing of text.");
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
        
        ta.setAutoTail(!ta.isAutoTail());
        
        if (ta.isAutoTail())
            ta.scrollToEnd();
    }
}