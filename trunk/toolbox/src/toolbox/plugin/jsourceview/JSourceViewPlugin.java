package toolbox.jsourceview;

import java.util.Map;

import javax.swing.JComponent;

import nu.xom.Element;

import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;

/**
 * Plugin wrapper for JSourceView
 */
public class JSourceViewPlugin implements IPlugin
{
    /** 
     * Delegate 
     */
    private JSourceView delegate_;
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "JSourceview";
    }

    /**
     * @see toolbox.workspace.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return delegate_;
    }

    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Provides statistics on java source code.";
    }

    /**
     * @see toolbox.workspace.IPlugin#startup(Map)
     */
    public void startup(Map params)
    {
        IStatusBar statusBar = null;
        
        if (params != null)
            statusBar = (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
        
        delegate_ = new JSourceView();
        delegate_.setStatusBar(statusBar);
    }

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        delegate_.applyPrefs(prefs);    
    }
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        delegate_.savePrefs(prefs);
    }

    /**
     * @see toolbox.workspace.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }
}