package toolbox.workspace.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nu.xom.Element;

import toolbox.util.ui.ImageCache;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.WorkspaceAction;

/**
 * Saves the preferences for the workspace, plugins, and various other 
 * IPreferenced implementors to an XML document. 
 */
public class SavePreferencesAction extends WorkspaceAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
   
    /**
     * Reference to the PluginWorkspace.
     */
    private final PluginWorkspace workspace_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SavePreferencesAction.
     */
    public SavePreferencesAction(PluginWorkspace workspace)
    {
        super("Save prefs", false, null, null);
        workspace_ = workspace;
        putValue(Action.MNEMONIC_KEY, new Integer('S'));
        putValue(Action.SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_SAVE));
    }

    //--------------------------------------------------------------------------
    // SmartAction Abstract Class
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(
     *      java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        runAction();
    }
 
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Convenience method when called without an ActionEvent.
     */
    public void runAction() throws Exception
    {
        workspace_.savePrefs((Element) workspace_.getPreferences().getParent());
    }
}