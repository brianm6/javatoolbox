package toolbox.util.ui.textarea.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.SmartAction;

/**
 * Saves the contents of the textarea to a file after the user makes a file
 * selection via a file chooser dialog. The action comes prewired with an
 * icon and a shortcut (Alt-A). 
 */
public class SaveAsAction extends SmartAction
{
    private static final Logger logger_ = Logger.getLogger(SaveAsAction.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Remember last visited directory via the FileChooser so subsequent 
     * navigations start where the user left off.
     */
    private static File lastDir_;
    
    /**
     * Text component whose contents we're going to save to a file.
     */
    private JTextComponent textComponent_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a SaveAsAction.
     * 
     * @param textComponent Text component with contents to save.
     */
    public SaveAsAction(JTextComponent textComponent)
    {
        super("Save As..", true, false, null);
        putValue(Action.MNEMONIC_KEY, new Integer('A'));
        putValue(Action.SMALL_ICON, 
            ImageCache.getIcon(ImageCache.IMAGE_SAVEAS));
        textComponent_ = textComponent;
    }

    //--------------------------------------------------------------------------
    // Overrides SmartAction
    //--------------------------------------------------------------------------

    /**
     * Pops up a file chooser dialog and saves the text area contents to the
     * selected file.
     * 
     * @see toolbox.util.ui.SmartAction#runAction(
     *      java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        JFileChooser chooser = null;
        
        if (lastDir_ == null)
            chooser = new JFileChooser();
        else
            chooser = new JFileChooser(lastDir_);

        if (chooser.showSaveDialog(textComponent_) == 
            JFileChooser.APPROVE_OPTION) 
        {
            String saveFile = chooser.getSelectedFile().getCanonicalPath();
            logger_.debug("save file=" + saveFile);
            FileUtil.setFileContents(saveFile, textComponent_.getText(), false);
        }
        
        lastDir_ = chooser.getCurrentDirectory();
    }
}