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
 * CreateDirAction is responsible for creating a new directory in the 
 * currently selected directory. A dialog box is used to prompt the user for the
 * name of the new directory.
 */
public class CreateDirAction extends AbstractDirAction
{
    private static final Logger logger_ = 
        Logger.getLogger(CreateDirAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CreateDirAction.
     * 
     * @param explorer File explorer.
     */
    public CreateDirAction(JFileExplorer explorer)
    {
        super("Create", explorer);
        putValue(Action.MNEMONIC_KEY, new Integer('C'));
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
        
        String newDir = 
            JOptionPane.showInputDialog(
                getExplorer(), 
                "Enter the name of the directory to create",
                lastDir);
        
        if (!StringUtils.isBlank(newDir))
        {
            File parent = new File(path);
            File createdDir = new File(parent, newDir);
            logger_.debug("Creating dir " + createdDir);
            

            if (createdDir.mkdir())
            {
                refresh(createdDir.getAbsolutePath());
            }
            else
            {
                JSmartOptionPane.showMessageDialog(
                    SwingUtil.getFrameAncestor(getExplorer()),
                    "Create directory failed",
                    "Error - Create",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}