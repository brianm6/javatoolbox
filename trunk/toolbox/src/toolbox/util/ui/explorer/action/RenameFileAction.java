package toolbox.util.ui.explorer.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.explorer.JFileExplorer;

/**
 * RenameFileAction is responsible for renaming the currently selected file in 
 * the JFileExplorer.
 */
public class RenameFileAction extends AbstractDirAction
{
    private static final Logger logger_ = 
        Logger.getLogger(RenameDirAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a RenameFileAction.
     * 
     * @param explorer File explorer.
     */
    public RenameFileAction(JFileExplorer explorer)
    {
        super("Rename", explorer);
        putValue(MNEMONIC_KEY, new Integer('R'));
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        File fromFile = new File(getExplorer().getFilePath());

        String fromName = fromFile.getName();
            
        String toName =
            JOptionPane.showInputDialog(
                getExplorer(), 
                "Enter the new name",
                fromName);
        
        // Catch blanks...
        if (StringUtils.isBlank(toName))
        {
            JSmartOptionPane.showMessageDialog(
                getExplorer(),
                "New name cannot be empty.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Catch no changes...
        if (toName.equals(fromName))
        {
            JSmartOptionPane.showMessageDialog(
                getExplorer(),
                "New name cannot be identical to the old name.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
            
        File parent = fromFile.getParentFile();
        File toFile = new File(parent, toName);
        
        logger_.debug("Renameing From: " + fromFile + "  To: " + toFile);
        
        if (fromFile.renameTo(toFile))
        {
            refresh(toFile.getAbsolutePath());
        }
        else
        {
            JSmartOptionPane.showMessageDialog(
                SwingUtil.getFrameAncestor(getExplorer()),
                "File rename failed",
                "Error - Rename",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}