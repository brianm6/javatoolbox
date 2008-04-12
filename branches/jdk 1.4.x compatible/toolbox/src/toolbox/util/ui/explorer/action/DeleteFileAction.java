package toolbox.util.ui.explorer.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.ui.explorer.JFileExplorer;

/**
 * DeleteFileAction is responsible for deleting the currently selected files.
 */
public class DeleteFileAction extends AbstractDirAction
{
    private static final Logger logger_ = 
        Logger.getLogger(DeleteFileAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DeleteFileAction.
     * 
     * @param explorer File explorer.
     */
    public DeleteFileAction(JFileExplorer explorer)
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
        Object[] files = getExplorer().getFileList().getSelectedValues();
        
        String path = 
            FileUtil.trailWithSeparator(getExplorer().getCurrentPath());

        logger_.debug("Deleting " + files.length + " files...");
        
        for (int i = 0; i < files.length; i++)
        {
            String filePath = path + files[i];
            File file = new File(filePath);
            
            if (file.exists() && file.canWrite() && file.isFile())
            {
                logger_.debug("Deleting " + file.getAbsolutePath() + "...");
                file.delete();
            }
        }
        
        refresh(path);
    }
}