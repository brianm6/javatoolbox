package toolbox.jedit.action;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.jedit.JEditTextArea;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartFileChooser;
import toolbox.workspace.IPreferenced;

/**
 * Saves the contents of the text area to a file.
 */
public class SaveAsAction extends AbstractJEditAction implements IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(SaveAsAction.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    /**
     * This class itself does not have any prefs to save, but the contained
     * file chooser does.
     */
    private static final String NODE_SAVEAS_ACTION = "SaveAsAction";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * File chooser used to select the location for saving the file.
     */
    private JSmartFileChooser chooser_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SaveAsAction.
     * 
     * @param area Target text area.
     */
    public SaveAsAction(JEditTextArea area)
    {
        super("Save As..", ImageCache.getIcon(ImageCache.IMAGE_SAVEAS), area);
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
            if (chooser_.showSaveDialog(area_) == JFileChooser.APPROVE_OPTION) 
            {
                String saveFile = chooser_.getSelectedFile().getCanonicalPath();
                FileUtil.setFileContents(saveFile, area_.getText(), false);
            }
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
        Element root = 
            XOMUtil.getFirstChildElement(
                prefs, 
                NODE_SAVEAS_ACTION,
                new Element(NODE_SAVEAS_ACTION));
        
        chooser_.applyPrefs(root);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_SAVEAS_ACTION);
        chooser_.savePrefs(root);
        XOMUtil.insertOrReplace(prefs, root);
    }
}