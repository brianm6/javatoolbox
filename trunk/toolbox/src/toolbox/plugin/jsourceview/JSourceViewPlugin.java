package toolbox.jsourceview;

import java.util.Map;

import javax.swing.JComponent;

import nu.xom.Element;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.PluginWorkspace;

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
     * @see toolbox.util.ui.plugin.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "JSourceview";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return delegate_;
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
        
        delegate_ = new JSourceView();
        delegate_.setStatusBar(statusBar);
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        delegate_.applyPrefs(prefs);    
    }
    
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        delegate_.savePrefs(prefs);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }
}