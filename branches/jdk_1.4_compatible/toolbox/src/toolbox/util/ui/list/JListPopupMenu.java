package toolbox.util.ui.list;

import javax.swing.JList;

import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartPopupMenu;
import toolbox.util.ui.list.action.CopyAction;
import toolbox.util.ui.list.action.SelectAllAction;

/**
 * PopupMenu for a JList that contains default implemenations of the Copy and
 * Select All operations.
 * 
 * @see javax.swing.JList
 * @see javax.swing.JPopupMenu
 * @see toolbox.util.ui.list.action.CopyAction
 * @see toolbox.util.ui.list.action.SelectAllAction
 */
public class JListPopupMenu extends JSmartPopupMenu
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * List that the popup menu is associated with.
     */
    private JList list_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JListPopupMenu.
     * 
     * @param list JList to add popup to.
     */
    public JListPopupMenu(JList list)
    {
        this("", list);
    }


    /**
     * Creates a JListPopupMenu.
     * 
     * @param label Popupmenu label.
     * @param list JList to add popup to.
     */
    public JListPopupMenu(String label, JList list)
    {
        super(label);
        list_ = list;
        buildView();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Builds this popupmenu and adds a mouse listener to listbox so the
     * popup can be activated on a right mouse button click.
     */
    protected void buildView()
    {
        add(new JSmartMenuItem(new CopyAction(list_)));
        add(new JSmartMenuItem(new SelectAllAction(list_)));
        list_.addMouseListener(new JPopupListener(this));
    }
}