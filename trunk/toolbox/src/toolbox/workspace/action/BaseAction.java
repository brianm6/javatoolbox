package toolbox.workspace.action;

import javax.swing.AbstractAction;

import toolbox.workspace.PluginWorkspace;

/**
 * Base class action for workspace actions that don't require SmartAction or
 * WorkspaceAction behavior.
 */
public abstract class BaseAction extends AbstractAction
{ 
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AboutAction.
     */
    public BaseAction(PluginWorkspace workspace)
    {
        putValue("workspace", workspace);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the plugin workspace.
     * 
     * @return PluginWorkspace
     */
    public PluginWorkspace getWorkspace()
    {
        return (PluginWorkspace) getValue("workspace");
    }
}
