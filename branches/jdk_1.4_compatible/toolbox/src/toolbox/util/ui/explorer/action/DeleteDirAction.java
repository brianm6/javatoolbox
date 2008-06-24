package toolbox.util.ui.explorer.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.explorer.JFileExplorer;

/**
 * DeleteDirAction is responsible for deleting the current directory and all of
 * its contents.
 */
public class DeleteDirAction extends AbstractDirAction
{
    private static final Logger logger_ = 
        Logger.getLogger(DeleteDirAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DeleteDirAction.
     * 
     * @param explorer File explorer.
     */
    public DeleteDirAction(JFileExplorer explorer)
    {
        super("Delete", explorer);
        putValue(Action.MNEMONIC_KEY, new Integer('D'));
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
        
        int response = 
            JSmartOptionPane.showConfirmDialog(
                getExplorer(), 
                "Are you sure you want to delete " + lastDir + "?",
                "Delete Directory",
                JSmartOptionPane.YES_NO_OPTION,
                JSmartOptionPane.QUESTION_MESSAGE);
                
        switch (response)
        {
            case JSmartOptionPane.YES_OPTION:
                
                File deleteMe = new File(path);
                String parent = deleteMe.getParent();
                
                try
                {
                    FileUtils.forceDelete(deleteMe);
                    refresh(parent);
                }
                catch (Exception ex)
                {
                    JSmartOptionPane.showDetailedMessageDialog(
                        SwingUtil.getFrameAncestor(getExplorer()),
                        "Deletion of directory " + path + " failed",
                        ex,
                        "Error - Delete Directory",
                        JOptionPane.ERROR_MESSAGE);
                    
                    refresh(path);
                }
                break;
            
            case JSmartOptionPane.NO_OPTION: 
                logger_.debug("Decided not to delete dir " + lastDir); 
                break;
                
            default:
                logger_.error("Delete conf response invalid:" + response);
                break;
        }
    }
}