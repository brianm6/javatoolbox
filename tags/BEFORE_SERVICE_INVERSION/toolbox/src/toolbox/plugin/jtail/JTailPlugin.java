package toolbox.plugin.jtail;

import java.util.Map;

import javax.swing.JComponent;

import nu.xom.Element;

import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;

/**
 * Plugin wrapper for {@link JTail}.
 */
public class JTailPlugin implements IPlugin
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * JTail delegate. 
     */
    private JTail jtail_;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JTailPlugin.     
     */
    public JTailPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map params)
    {
        IStatusBar statusBar = null;
        
        if (params != null)
            statusBar = (IStatusBar) params.get(PluginWorkspace.KEY_STATUSBAR);
        
        jtail_ = new JTail();
        jtail_.setStatusBar(statusBar);
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "JTail";
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return (JComponent) jtail_;
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Tails files as they grow. Similar to 'tail -f' on Unix";
    }

    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy()
    {
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        jtail_.applyPrefs(prefs);
    }
    
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        jtail_.savePrefs(prefs);
    }
}