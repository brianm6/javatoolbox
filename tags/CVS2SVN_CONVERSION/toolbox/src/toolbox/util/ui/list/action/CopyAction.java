package toolbox.util.ui.list.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;

/**
 * Copies the contents of the currently selected indices to the clipboard
 * as toString() results separated by newlines.
 */    
public class CopyAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * List from which the selected items are to be copied.
     */
    private JList list_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

	/**
     * Creates a CopyAction.
     * 
     * @param list List from which the selected items are to be copied.
     */
    public CopyAction(JList list)
    {
        super("Copy");
		list_ = list;
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------

    /**
     * Gets list of selected list items, convertes then to strings and shoves
     * into the clipboard.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        Object[] selected = list_.getSelectedValues();
        StringBuffer sb = new StringBuffer();
        
        // Concat selected items into a text string
        for (int i = 0; i < selected.length; i++)
        {
            sb.append(selected[i].toString());
            
            if (i != selected.length - 1)
                sb.append("\n");
        }
        
        StringSelection ss = new StringSelection(sb.toString());
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        clip.setContents(ss, ss);
    }
}