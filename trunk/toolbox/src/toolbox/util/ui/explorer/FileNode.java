package toolbox.util.ui.explorer;

import javax.swing.tree.DefaultMutableTreeNode;

import toolbox.util.Platform;

/**
 * FileNode used to represent directories in the directory tree. The purpose of
 * this class is to implement a useful equals() method that compares files based
 * on the their name.
 */
public class FileNode extends DefaultMutableTreeNode
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FileNode.
     * 
     * @param userObject Object to associate with the file node.
     */
    FileNode(Object userObject)
    {
        super(userObject);
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Compares based on directory/file name. Is sensetive to the host
     * platform w.r.t. case sensetivity.
     * 
     * @param obj Object to compare.
     * @return True if nodes are equal, false otherwise.
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof DefaultMutableTreeNode))
            return false;
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
        
        String file1 = (String) getUserObject();
        String file2 = (String) node.getUserObject();
        
        if (Platform.isUnix())
            return file1.equals(file2);
        else
            return file1.equalsIgnoreCase(file2);
    }
}