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
 * CreateDirAction is responsible for creating a new directory in the 
 * JFileExplorer.
 */
public class CreateDirAction extends AbstractAction
{
    private static final Logger logger_ = 
        Logger.getLogger(CreateDirAction.class);
    
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
     * Creates a CreateDirAction.
     * 
     * @param explorer File explorer proxy.
     */
    public CreateDirAction(JFileExplorerProxy explorer)
    {
        super("Create");
        putValue(Action.MNEMONIC_KEY, new Integer('C'));
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
        
        String newDir = 
            JOptionPane.showInputDialog(
                explorer_.getOriginal(), 
                "Enter the new name",
                lastDir);
        
        if (!StringUtils.isBlank(newDir))
        {
            File parent = new File(path);
            File createdDir = new File(parent, newDir);
            logger_.debug("Creating dir " + createdDir);
            boolean success = createdDir.mkdir();

            if (success)
            {
                String folder = createdDir.getAbsolutePath();
                
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
                    "Create directory failed",
                    "Error - Create",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}