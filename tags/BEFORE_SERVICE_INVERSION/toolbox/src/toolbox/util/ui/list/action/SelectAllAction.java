package toolbox.util.ui.list.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;

/**
 * Selects all items in a list box.
 */
public class SelectAllAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * List in which to select all the items.
     */
    private final JList list_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
	/**
     * Creates a SelectAllAction.
     * 
     * @param list List in which to select all the items.
     */
    public SelectAllAction(JList list)
    {
        super("Select All");
		list_ = list;
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * Simple selection of all the list items.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        int[] indexes = new int[list_.getModel().getSize()];
        for (int i = 0; i < indexes.length; indexes[i] = i++);
        this.list_.setSelectedIndices(indexes);
    }
}