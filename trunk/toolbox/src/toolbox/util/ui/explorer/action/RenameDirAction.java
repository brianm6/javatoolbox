package toolbox.util.ui.explorer.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.explorer.JFileExplorer;

/**
 * RenameDirAction is responsible for renaming the currently selected directory
 * in the JFileExplorer.
 */
public class RenameDirAction extends AbstractDirAction
{
    private static final Logger logger_ = 
        Logger.getLogger(RenameDirAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a RenameDirAction.
     * 
     * @param explorer File explorer.
     */
    public RenameDirAction(JFileExplorer explorer)
    {
        super("Rename", explorer);
        putValue(Action.MNEMONIC_KEY, new Integer('R'));
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
        String path = StringUtils.chomp(
            getExplorer().getCurrentPath(), File.separator);
        
        int lastDirIdx = path.lastIndexOf(File.separator);
        String lastDir = lastDirIdx < 0 ? path : path.substring(lastDirIdx + 1);
        
        String newName = 
            JOptionPane.showInputDialog(
                getExplorer(), 
                "Enter the new name",
                lastDir);
        
        if (!StringUtils.isBlank(newName))
        {
            File from = new File(path);
            File parent = from.getParentFile();
            File to = new File(parent, newName);
            
            logger_.debug("From: " + from + "  To: " + to);
            
            if (from.renameTo(to))
            {
                refresh(to.getAbsolutePath());
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
}