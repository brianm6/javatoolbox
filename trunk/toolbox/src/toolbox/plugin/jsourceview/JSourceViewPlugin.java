package toolbox.jsourceview;

import java.awt.Component;
import java.util.Properties;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;

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
    public Component getComponent()
    {
        return sourceView_.getContentPane();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Provides statistics on java source code.";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#init()
     */
    public void init()
    {
        sourceView_ = new JSourceView();
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
     * @see toolbox.util.ui.plugin.IPlugin#setStatusBar(IStatusBar)
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        sourceView_.setStatusBar(statusBar);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }
}