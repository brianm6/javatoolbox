package toolbox.jtail;

import java.awt.Component;
import java.util.Map;
import java.util.Properties;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.PluginWorkspace;

/**
 * Plugin wrapper for {@link JTail}
 */
public class JTailPlugin implements IPlugin
{
    /** JTail Delegate */
    private JTail jtail_;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor     
     */
    public JTailPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    public String getName()
    {
        return "JTail";
    }

    public Component getComponent()
    {
        return jtail_.getContentPane();
    }

    public String getDescription()
    {
        return "Tails files as they grow. Similar to 'tail -f' on Unix";
    }

    public void startup(Map params)
    {
        IStatusBar statusBar = null;
        
        if (params != null)
            statusBar = (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
        
        jtail_ = new JTail();
        jtail_.setStatusBar(statusBar);
    }

    public void savePrefs(Properties prefs)
    {
        jtail_.savePrefs(prefs);
    }

    public void applyPrefs(Properties prefs)
    {
        jtail_.applyPrefs(prefs);
    }

    public void shutdown()
    {
    }
}