package toolbox.jsourceview;

import java.util.Map;
import java.util.Properties;

import javax.swing.JComponent;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.PluginWorkspace;

/**
 * Plugin wrapper for JSourceView
 */
public class JSourceViewPlugin implements IPlugin
{
    /** Delegate */
    private JSourceView sourceView_;
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getName()
     */
    public String getName()
    {
        return "JSourceview";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return (JComponent) sourceView_.getContentPane();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Provides statistics on java source code.";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#startup(Map)
     */
    public void startup(Map params)
    {
        IStatusBar statusBar = null;
        
        if (params != null)
            statusBar = (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
        
        sourceView_ = new JSourceView();
        sourceView_.setStatusBar(statusBar);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
        sourceView_.savePrefs(prefs);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        sourceView_.applyPrefs(prefs);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }
}