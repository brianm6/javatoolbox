package toolbox.util.ui.textarea;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.text.JTextComponent;

import toolbox.util.FileUtil;
import toolbox.util.ui.SmartAction;

/**
 * Inserts the text of a file at the currnet cursor location.
 */
public class InsertFileAction extends SmartAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Last selected directory in the FileChooser.
     */
    private static File lastDir_;
    
    /**
     * Text component to insert the files contents into.
     */
    private JTextComponent textComponent_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a InsertFileAction.
     * 
     * @param textComponent Text component to insert to.
     */
    public InsertFileAction(JTextComponent textComponent)
    {
        super("Insert..", true, false, null);
        textComponent_ = textComponent;
    }

    //--------------------------------------------------------------------------
    // Overrides SmartAction
    //--------------------------------------------------------------------------

    /**
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

        if (chooser.showOpenDialog(textComponent_) == 
            JFileChooser.APPROVE_OPTION) 
        {
            String txt = FileUtil.getFileContents(
                chooser.getSelectedFile().getCanonicalPath());
            
            int curPos = textComponent_.getCaretPosition();    
            
            textComponent_.getDocument().
                insertString(curPos, txt, null);                        
        }
        
        lastDir_ = chooser.getCurrentDirectory();
    }
}