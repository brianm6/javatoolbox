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
     */
    public PluginsAction(PluginWorkspace workspace)
    {
        super(workspace);
        putValue(Action.NAME, "Plugins..");
        putValue(Action.MNEMONIC_KEY, new Integer('P'));
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae)
    {
        JDialog dialog = new PluginDialog(getWorkspace());
        dialog.setVisible(true);
    }
}