package toolbox.jtail;

import java.awt.Component;
import java.util.Properties;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * Plugin wrapper for {@link JTail}
 */
public class JTailPlugin implements IPlugin
{
    /** JTail Delegate */
    private JTail jtail_;

    /** Hack for out of order initialization by register plugin */
    private IStatusBar savedStatusBar_;

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

    public void init()
    {
        jtail_ = new JTail();
        
        if (savedStatusBar_ != null)
            setStatusBar(savedStatusBar_);
    }

    public void savePrefs(Properties prefs)
    {
        jtail_.saveConfiguration(prefs);
    }

    public void applyPrefs(Properties prefs)
    {
        jtail_.applyConfiguration(prefs);
    }

    public void setStatusBar(IStatusBar statusBar)
    {
        if (jtail_ == null)
            savedStatusBar_ = statusBar;
        else
            jtail_.setStatusBar(statusBar);
    }

    public void shutdown()
    {
    }
}