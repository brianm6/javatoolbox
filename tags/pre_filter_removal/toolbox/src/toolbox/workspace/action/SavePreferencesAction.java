package toolbox.workspace.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ui.ImageCache;
import toolbox.workspace.PluginWorkspace;

/**
 * Saves the preferences for the workspace, plugins, and various other 
 * IPreferenced implementors to an XML document. 
 */
public class SavePreferencesAction extends BaseAction
{
    private static final Logger logger_ = 
        Logger.getLogger(SavePreferencesAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SavePreferencesAction.
     * 
     * @param workspace Plugin workspace.
     */
    public SavePreferencesAction(PluginWorkspace workspace)
    {
        super(workspace, "Save Preferences");
        putValue(Action.MNEMONIC_KEY, new Integer('S'));
        putValue(Action.SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_SAVE));
    }

    //--------------------------------------------------------------------------
    // SmartAction Interface
    //--------------------------------------------------------------------------
    
    /**
     * Delegates to savePrefs().
     * 
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        savePrefs();
    }
 
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Convenience method when called without an ActionEvent.
     * 
     * @throws Exception on error.
     */
    public void savePrefs() throws Exception
    {
        getWorkspace().savePrefs((Element) getWorkspace().getPreferences());
    }
}