package toolbox.jtail;

import java.util.Map;
import java.util.Properties;

import javax.swing.JComponent;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.PluginWorkspace;

/**
 * Plugin wrapper for {@link JTail}
 */
public class JTailPlugin implements IPlugin
{
    /** 
     * JTail delegate 
     */
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

    public JComponent getComponent()
    {
        return (JComponent) jtail_.getContentPane();
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

    public void applyPrefs(Properties prefs) throws Exception
    {
        jtail_.applyPrefs(prefs);
    }

    public void shutdown()
    {
    }
}