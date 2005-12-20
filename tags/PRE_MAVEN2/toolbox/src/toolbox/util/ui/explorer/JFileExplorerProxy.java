package toolbox.util.ui.explorer;

import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * JFileExplorerProxy is a workaround for classes in subpackages of 
 * JFileExplorer that need 'protected' like access to JFileExplorer.
 */
public class JFileExplorerProxy
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Explorer delegate.
     */
    private JFileExplorer explorer_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JFileExplorerProxy.
     * 
     * @param explorer Explorer to create a proxy for.
     */
    public JFileExplorerProxy(JFileExplorer explorer)
    {
        explorer_ = explorer;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the explorer that is being proxied.
     * 
     * @return JFileExplorer
     */
    public JFileExplorer getOriginal()
    {
        return explorer_;
    }
    
    
    /**
     * Delegates to original explorer.
     */
    public void fireFolderDoubleClicked(String folder)
    {
        explorer_.fireFolderDoubleClicked(folder);
    }

    
    /**
     * Delegates to the original explorer.
     */
    public void setTreeFolders(
        String pathToAddFolders,
        DefaultMutableTreeNode currentNode)
    {
        explorer_.setTreeFolders(pathToAddFolders, currentNode);    
    }

    
    /**
     * Delegates to the original explorer.
     */
    public void setFileList(String path)
    {
        explorer_.setFileList(path);
    }

    
    /**
     * Delegates to the original explorer.
     */
    public void fireFolderSelected(String folder)
    {
        explorer_.fireFolderSelected(folder);
    }
    
    
    /**
     * Delegates to the original explorer.
     */
    public void selectFolder(String path)
    {
        explorer_.selectFolder(path);
    }
    
    
    /**
     * Delegates to the original explorer.
     */
    public JPopupMenu getFolderPopup() 
    {
        return explorer_.getFolderPopup();
    }
    
    
    /**
     * Delegates to the original explorer.
     */
    public JComboBox getRootsComboBox()
    {
        return explorer_.getRootsComboBox();
    }

    
    /**
     * Delegates to the original explorer.
     */
    public void setFolderPopup(JPopupMenu popup)
    {
        explorer_.setFolderPopup(popup);
    }

    
    /**
     * Delegates to the original explorer.
     */
    public void clear()
    {
        explorer_.clear();
    }

    
    /**
     * Delegates to the original explorer.
     */
    public void setTreeRoot(String fileRoot)
    {
        explorer_.setTreeRoot(fileRoot);
    }

    
    /**
     * Delegates to the original explorer.
     */
    public void fireFileDoubleClicked()
    {
        explorer_.fireFileDoubleClicked();
    }

    
    /**
     * Delegates to the original explorer.
     */
    public void fireFileSelected()
    {
        explorer_.fireFileSelected();
    }
}