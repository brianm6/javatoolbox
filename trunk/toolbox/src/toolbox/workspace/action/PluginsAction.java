package toolbox.workspace.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JDialog;

import toolbox.workspace.PluginDialog;
import toolbox.workspace.PluginWorkspace;

/**
 * Brings up a dialog box that used to adds/remove plugins. The newer
 * PluginMenu is much easier though. 
 */
public class PluginsAction extends BaseAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PluginsAction.
     * 
     * @param workspace Plugin workspace.
     */
    public PluginsAction(PluginWorkspace workspace)
    {
        super(workspace, "Plugins..");
        putValue(Action.MNEMONIC_KEY, new Integer('P'));
    }

    //--------------------------------------------------------------------------
    // SmartAction Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        JDialog dialog = new PluginDialog(getWorkspace());
        dialog.setVisible(true);
    }
}