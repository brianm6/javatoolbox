package toolbox.jtail;

import java.util.Map;

import javax.swing.JComponent;

import nu.xom.Element;

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

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        jtail_.applyPrefs(prefs);
    }
    
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        jtail_.savePrefs(prefs);
    }

    public void shutdown()
    {
    }
}