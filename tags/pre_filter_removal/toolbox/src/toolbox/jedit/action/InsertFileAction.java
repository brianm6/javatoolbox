package toolbox.jedit.action;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.jedit.JEditTextArea;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartFileChooser;

/**
 * Inserts the text of a file at the current cursor location.
 */
public class InsertFileAction extends AbstractJEditAction
{
    private static final Logger logger_ = 
        Logger.getLogger(InsertFileAction.class);
    
    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    private static final String NODE_INSERT_FILE_ACTION = "InsertFileAction";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private JSmartFileChooser chooser_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a InsertFileAction.
     * 
     * @param area Target textarea.
     */
    public InsertFileAction(JEditTextArea area)
    {
        super("Insert..", area);
        chooser_ = new JSmartFileChooser();
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
        try
        {
            if (chooser_.showOpenDialog(area_) == JFileChooser.APPROVE_OPTION) 
            {
                String txt = FileUtil.getFileContents(
                    chooser_.getSelectedFile().getCanonicalPath());
                
                int curPos = area_.getCaretPosition();    
                
                area_.getDocument().insertString(curPos, txt, null);                        
            }
        }
        catch (BadLocationException ble)
        {
            ExceptionUtil.handleUI(ble, logger_);
        }
        catch (FileNotFoundException fnfe)
        {
            ExceptionUtil.handleUI(fnfe, logger_);
        }
        catch (IOException ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(prefs, 
            NODE_INSERT_FILE_ACTION, new Element(NODE_INSERT_FILE_ACTION));
        
        chooser_.applyPrefs(root);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_INSERT_FILE_ACTION);
        chooser_.savePrefs(root);
        XOMUtil.insertOrReplace(prefs, root);
    }
}