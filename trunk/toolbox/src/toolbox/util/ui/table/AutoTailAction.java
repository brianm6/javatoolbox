package toolbox.util.ui.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import toolbox.util.ui.ImageCache;

/**
 * Creates an action for toggling the autotail flag in a JSmartTable.
 */    
public class AutoTailAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Table to tail.
     */
    private final JSmartTable table_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AutoTailAction.
     * 
     * @param table Table to tail.
     */
    public AutoTailAction(JSmartTable table)
    {
        super("Tail");
        table_ = table;
        putValue(Action.MNEMONIC_KEY, new Integer('A'));
        putValue(Action.SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_LOCK));
    }
    
    //--------------------------------------------------------------------------
    // ActionPerformed Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        table_.setAutoTail(!table_.isAutoTail());
    }
}