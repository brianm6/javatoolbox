package toolbox.util.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * PopupMenu for a JList
 */
public class JListPopupMenu extends JPopupMenu
{
    private JList list_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for JListPopupMenu.
     * 
     * @param  list  JList to add popup to
     */
    public JListPopupMenu(JList list)
    {
        this("", list);
    }

    /**
     * Constructor for JListPopupMenu.
     * 
     * @param label  Popupmenu label
     * @param list   JList to add popup to
     */
    public JListPopupMenu(String label, JList list)
    {
        super(label);
        list_ = list;
        buildView();
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Builds popupmenu and adds mouse listener to listbox
     */
    protected void buildView()
    {
        add(new JMenuItem(new CopyAction()));
        add(new JMenuItem(new SelectAllAction()));
        list_.addMouseListener(new JPopupListener(this));
    }
    
    //--------------------------------------------------------------------------
    //  Action Inner Classes
    //--------------------------------------------------------------------------

    /**
     * Copies the contents of the currently selected indices to the clipboard
     */    
    protected class CopyAction extends AbstractAction
    {
        public CopyAction()
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

    /**
     * Selects all items in the list box 
     */
    protected class SelectAllAction extends AbstractAction
    {
        public SelectAllAction()
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