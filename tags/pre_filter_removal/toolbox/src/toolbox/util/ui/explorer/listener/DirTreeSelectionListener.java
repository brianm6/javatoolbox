package toolbox.util.ui.explorer.listener;

import java.io.File;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import toolbox.util.ui.explorer.JFileExplorer;

/**
 * Updates the contents of the file list when the directory selection changes.
 */
public class DirTreeSelectionListener extends AbstractListener 
    implements TreeSelectionListener
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DirTreeSelectionListener.
     * 
     * @param explorer File explorer.
     */
    public DirTreeSelectionListener(JFileExplorer explorer)
    {
        super(explorer);
    }

    //--------------------------------------------------------------------------
    // TreeSelectionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(
     *      javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        StringBuffer s = new StringBuffer();
        TreePath path = e.getPath();
        Object[] o = path.getPath();

        DefaultMutableTreeNode currentNode =
            (DefaultMutableTreeNode) (path.getLastPathComponent());

        // Should optimize
        s.append(o[0]);
        
        for (int i = 1; i < o.length; i++)
        {
            if (!o[i - 1].toString().endsWith(File.separator))
                s.append(File.separator);
                
            s.append(o[i]);
        }

        String folder = s.toString();
        getProxy().setTreeFolders(folder, currentNode);
        getProxy().setFileList(folder);
        getProxy().fireFolderSelected(folder);
        getExplorer().selectFolder(folder);
    }
}