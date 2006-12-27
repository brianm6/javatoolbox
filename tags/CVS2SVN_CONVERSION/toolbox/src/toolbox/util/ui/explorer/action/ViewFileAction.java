package toolbox.util.ui.explorer.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import toolbox.plugin.docviewer.DocumentViewer;
import toolbox.plugin.docviewer.JEditViewer;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartFrame;
import toolbox.util.ui.explorer.JFileExplorer;

/**
 * ViewFileAction is responsible for viewing the selected file in a text editor. 
 */
public class ViewFileAction extends AbstractDirAction
{
    private static final Logger logger_ =
        Logger.getLogger(RenameDirAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ViewFileAction.
     * 
     * @param explorer File explorer.
     */
    public ViewFileAction(JFileExplorer explorer)
    {
        super("View..", explorer);
        putValue(MNEMONIC_KEY, new Integer('V'));
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
        File selected = new File(getExplorer().getFilePath());

        if (selected.isFile())
        {
            JFrame viewFrame = new JSmartFrame(selected.getAbsolutePath());
            viewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            DocumentViewer viewer = new JEditViewer(selected.getName());
            viewer.view(selected);
            viewFrame.setContentPane(viewer.getComponent());
            viewFrame.pack();
            
            SwingUtil.centerWindow(
                SwingUtil.getFrameAncestor(getExplorer()),
                viewFrame);
            
            viewFrame.setVisible(true);
        }
    }
}