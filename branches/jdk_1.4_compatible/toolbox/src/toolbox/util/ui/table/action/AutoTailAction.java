package toolbox.util.ui.table.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.table.JSmartTable;

/**
 * Creates an action for toggling the autotail flag in a JSmartTable. Comes
 * prewired with an icon and a shortcut.
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
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * Basically toggles the autotail flag.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        table_.setAutoTail(!table_.isAutoTail());
    }
}