package toolbox.util.ui.list;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;

import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartPopupMenu;

/**
 * PopupMenu for a JList.
 */
public class JListPopupMenu extends JSmartPopupMenu
{
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
     * @param list JList to add popup to
     */
    public JListPopupMenu(JList list)
    {
        this("", list);
    }


    /**
     * Creates a JListPopupMenu.
     * 
     * @param label Popupmenu label
     * @param list JList to add popup to
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
     * Builds popupmenu and adds mouse listener to listbox.
     */
    protected void buildView()
    {
        add(new JSmartMenuItem(new CopyAction()));
        add(new JSmartMenuItem(new SelectAllAction()));
        list_.addMouseListener(new JPopupListener(this));
    }
    
    //--------------------------------------------------------------------------
    // CopyAction
    //--------------------------------------------------------------------------

    /**
     * Copies the contents of the currently selected indices to the clipboard.
     */    
    class CopyAction extends AbstractAction
    {
        CopyAction()
        {
            super("Copy");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            Object[] selected = list_.getSelectedValues();
            StringBuffer sb = new StringBuffer();
            
            // Concat selected items into a text string
            for (int i=0; i<selected.length; i++)
            {
                sb.append(selected[i].toString());
                
                if (i != selected.length -1)
                    sb.append("\n");
            }
            
            StringSelection ss = new StringSelection(sb.toString());
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            clip.setContents(ss, ss);
        }
    }

    //--------------------------------------------------------------------------
    // SelectAllAction
    //--------------------------------------------------------------------------
    
    /**
     * Selects all items in the list box.
     */
    class SelectAllAction extends AbstractAction
    {
        SelectAllAction()
        {
            super("Select All");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            int[] indexes = new int[list_.getModel().getSize()];
            for(int i=0; i<indexes.length; indexes[i] = i++);
            list_.setSelectedIndices(indexes);
        }
    }
}