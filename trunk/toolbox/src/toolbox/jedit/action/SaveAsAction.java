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
import toolbox.util.PreferencedUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
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
    
    private static final String NODE_SAVEAS_ACTION = "SaveAsAction";
    private static final String PROP_LAST_DIR = "lastDir";
    private static final String[] SAVED_PROPS = {PROP_LAST_DIR};
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Remembers last active directory.
     */
    private String lastDir_;
    
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
        super(
            "Save As..", 
            ImageCache.getIcon(ImageCache.IMAGE_SAVEAS), 
            area);
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

            if (chooser.showSaveDialog(area_)
                == JFileChooser.APPROVE_OPTION) 
            {
                String saveFile = 
                    chooser.getSelectedFile().getCanonicalPath();
                    
                FileUtil.setFileContents(saveFile, area_.getText(), false);
            }
            
            setLastDir(chooser.getCurrentDirectory().getCanonicalPath());
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
        Element root = XOMUtil.getFirstChildElement(prefs, NODE_SAVEAS_ACTION,
            new Element(NODE_SAVEAS_ACTION));
        
        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_SAVEAS_ACTION);
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