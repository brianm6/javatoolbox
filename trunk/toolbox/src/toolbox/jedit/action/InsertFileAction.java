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
import toolbox.util.PreferencedUtil;
import toolbox.util.XOMUtil;

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
    private static final String PROP_LAST_DIR = "lastDir";
    private static final String[] SAVED_PROPS = {PROP_LAST_DIR};
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Last directory chosed in the file chooser.
     */
    private String lastDir_;
    
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
            JFileChooser chooser = null;
            
            if (getLastDir() == null)
                chooser = new JFileChooser();
            else
                chooser = new JFileChooser(getLastDir());

            if (chooser.showOpenDialog(area_) == 
                JFileChooser.APPROVE_OPTION) 
            {
                String txt = FileUtil.getFileContents(
                    chooser.getSelectedFile().getCanonicalPath());
                
                int curPos = area_.getCaretPosition();    
                
                area_.getDocument().insertString(curPos, txt, null);                        
            }
            
            setLastDir(chooser.getCurrentDirectory().getCanonicalPath());
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
        
        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_INSERT_FILE_ACTION);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // JavaBean Properties
    //--------------------------------------------------------------------------
    
    /**
     * Returns the last directory navigated to using the file chooser.
     * 
     * @return String
     */
    public String getLastDir()
    {
        return lastDir_;
    }
 
    
    /**
     * Sets the last directory navigated to using the file chooser.
     * 
     * @param lastDir Last directory in absolute form.
     */
    public void setLastDir(String lastDir)
    {
        lastDir_ = lastDir;
    }
    
}