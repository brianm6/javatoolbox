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
     * Converts the current selection path into an absolute file path.
     * 
     * @see javax.swing.event.TreeSelectionListener#valueChanged(
     *      javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        StringBuffer folderPath = new StringBuffer();
        TreePath treePath = e.getPath();
        Object[] pathElements = treePath.getPath();

        DefaultMutableTreeNode currentNode =
            (DefaultMutableTreeNode) treePath.getLastPathComponent();

        // First element of the path
        folderPath.append(pathElements[0]);
        
        // From 2nd element to end
        for (int i = 1; i < pathElements.length; i++)
        {
            if (!pathElements[i - 1].toString().endsWith(File.separator))
                folderPath.append(File.separator);
                
            folderPath.append(pathElements[i]);
        }

        String folder = folderPath.toString();
        getProxy().setTreeFolders(folder, currentNode);
        getProxy().setFileList(folder);
        getProxy().fireFolderSelected(folder);
        getExplorer().selectFolder(folder);
    }
}