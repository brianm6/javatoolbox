package toolbox.util.ui.explorer;

import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * JFileExplorerProxy is responsible for ___.
 */
public class JFileExplorerProxy
{
    private JFileExplorer explorer_;
    
    /**
     * Creates a JFileExplorerProxy.
     */
    public JFileExplorerProxy(JFileExplorer explorer)
    {
        explorer_ = explorer;
    }
    
    public JFileExplorer getOriginal()
    {
        return explorer_;
    }
    
    
    public void fireFolderDoubleClicked(String folder)
    {
        explorer_.fireFolderDoubleClicked(folder);
    }

    
    public void setTreeFolders(
        String pathToAddFolders,
        DefaultMutableTreeNode currentNode)
    {
        explorer_.setTreeFolders(pathToAddFolders, currentNode);    
    }

    
    public void setFileList(String path)
    {
        explorer_.setFileList(path);
    }

    
    public void fireFolderSelected(String folder)
    {
        explorer_.fireFolderSelected(folder);
    }
    
    
    public void selectFolder(String path)
    {
        explorer_.selectFolder(path);
    }
    
    
    public JPopupMenu getFolderPopup() 
    {
        return explorer_.getFolderPopup();
    }
    
    
    public JComboBox getRootsComboBox()
    {
        return explorer_.getRootsComboBox();
    }

    /**
     * @param popup
     */
    public void setFolderPopup(JPopupMenu popup)
    {
        explorer_.setFolderPopup(popup);
    }

    /**
     * 
     */
    public void clear()
    {
        explorer_.clear();
    }

    /**
     * @param fileRoot
     */
    public void setTreeRoot(String fileRoot)
    {
        explorer_.setTreeRoot(fileRoot);
    }

    /**
     * 
     */
    public void fireFileDoubleClicked()
    {
        explorer_.fireFileDoubleClicked();
    }

    /**
     * 
     */
    public void fireFileSelected()
    {
        explorer_.fireFileSelected();
    }
}