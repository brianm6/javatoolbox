package toolbox.plugin.jdbc.action;

import java.awt.Component;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.WorkspaceAction;

/**
 * Base class for QueryPlugin actions.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public abstract class BaseAction extends WorkspaceAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Reference to the Query plugin which invoked this action.
     */
    private QueryPlugin plugin_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a BaseAction.
     * 
     * @param plugin Query plugin which invoked this action.
     * @param name Action name.
     * @param async True to run asynchronously.
     * @param scope Scope of the busy cursor.
     * @param statusBar Status bar for updates.
     */
    public BaseAction(
        QueryPlugin plugin, 
        String name, 
        boolean async, 
        Component scope, 
        IStatusBar statusBar)
    {
        super(name, false, async, scope, statusBar);
        setPlugin(plugin);
        addErrorAction(new ErrorHandlerAction(getPlugin(), this));
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the plugin.
     * 
     * @return QueryPlugin
     */
    public QueryPlugin getPlugin()
    {
        return plugin_;
    }
    
    
    /**
     * Sets the plugin.
     * 
     * @param plugin The plugin to set.
     */
    public void setPlugin(QueryPlugin plugin)
    {
        plugin_ = plugin;
    }
}