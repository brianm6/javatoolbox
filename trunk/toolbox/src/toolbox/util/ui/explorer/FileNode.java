package toolbox.util.ui.explorer;

import javax.swing.tree.DefaultMutableTreeNode;

import toolbox.util.Platform;

/**
 * FileNode is a TreeNode that has specific behavior when compared to another 
 * FileNode. The equality is based on the name of the file and also takes into 
 * account the platform. If running on a unix system, the filename comparison 
 * is case-insensitive.
 * 
 * @see toolbox.util.ui.explorer.JFileExplorer
 */
public class FileNode extends DefaultMutableTreeNode
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a FileNode.
     *
     * @param someObject Object to associate with this file node.
     */
    public FileNode(Object someObject)
    {
        super(someObject);
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * Compares based on directory/file name. Is sensitive to the host
     * platform w.r.t. case sensitivity.
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


    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return super.hashCode();
    }
}