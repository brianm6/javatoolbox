package toolbox.workspace.action;

import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.WorkspaceAction;

/**
 * Base class action for actions used internally by the workspace.
 */
public abstract class BaseAction extends WorkspaceAction
{ 
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a BaseAction
     */
    public BaseAction(PluginWorkspace workspace, String name)
    {
        super(name, false, null, null);
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
