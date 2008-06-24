package toolbox.util.ui.explorer.action;

import java.awt.event.ActionEvent;
import java.io.File;

import toolbox.util.ui.explorer.JFileExplorer;

/**
 * UpOneLevelAction is responsible for navigating to the currently selected
 * directory's parent directory.
 */
public class UpOneLevelAction extends AbstractDirAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a UpOneLevelAction.
     */
    public UpOneLevelAction(JFileExplorer explorer)
    {
        super("Up One Level", explorer);
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * Delegates to <code>upOneLevel()</code>.
     *  
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        upOneLevel();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Navigates up one level is the directory hierarchy.
     */
    public void upOneLevel()
    {
        File current = new File(getExplorer().getFilePath());
        String parent = current.getParent();
        
        if (parent != null)
            getExplorer().selectFolder(parent);
    }
}