package toolbox.util.ui.explorer.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.explorer.JFileExplorerProxy;
import toolbox.util.ui.explorer.listener.DriveComboListener;

/**
 * RenameDirAction is responsible for renaming the currently selected directory
 * in the JFileExplorer.
 */
public class RenameDirAction extends AbstractAction
{
    private static final Logger logger_ = 
        Logger.getLogger(RenameDirAction.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Parent explorer.
     */
    private final JFileExplorerProxy explorer_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a RenameDirAction.
     * 
     * @param explorer File explorer proxy.
     */
    public RenameDirAction(JFileExplorerProxy explorer)
    {
        super("Rename");
        putValue(Action.MNEMONIC_KEY, new Integer('R'));
        explorer_ = explorer;
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
            explorer_.getOriginal().getCurrentPath(), File.separator);
        
        int lastDirIdx = path.lastIndexOf(File.separator);
        String lastDir = lastDirIdx < 0 ? path : path.substring(lastDirIdx + 1);
        
        String newName = 
            JOptionPane.showInputDialog(
                explorer_.getOriginal(), 
                "Enter the new name",
                lastDir);
        
        if (!StringUtils.isBlank(newName))
        {
            File from = new File(path);
            File parent = from.getParentFile();
            File to = new File(parent, newName);
            
            logger_.debug("From: " + from + "  To: " + to);
            
            boolean success  = from.renameTo(to);

            if (success)
            {
                String folder = to.getAbsolutePath();
                
                new DriveComboListener(
                    explorer_.getOriginal()).itemStateChanged(
                        new ItemEvent(
                            explorer_.getRootsComboBox(), 
                            0, 
                            null, 
                            ItemEvent.SELECTED));
                        
                explorer_.selectFolder(folder);
                
                //setFileList(folder);
                //getFileList().setSelectedValue(file, true);
            }
            else
            {
                JSmartOptionPane.showMessageDialog(
                    SwingUtil.getFrameAncestor(explorer_.getOriginal()),
                    "File rename failed",
                    "Error - Rename",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}