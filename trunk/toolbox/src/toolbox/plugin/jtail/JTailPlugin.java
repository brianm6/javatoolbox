package toolbox.jtail;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * Plugin wrapper for JTail
 */
public class JTailPlugin implements IPlugin
{
    /**
     * JTail Delegate
     */
    private JTail jtail_;

    /**
     * Hack for out of order initialization by register plugin
     */
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
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getName()
     */
    public String getName()
    {
        return "JTail";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public Component getComponent()
    {
        return jtail_.getContentPane();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getMenu()
     */
    public JMenu getMenu()
    {
        return jtail_.getJMenuBar().getMenu(0);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getMenuBar()
     */
    public JMenuBar getMenuBar()
    {
        return jtail_.getJMenuBar();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#init()
     */
    public void init()
    {
        jtail_ = new JTail();
        
        if (savedStatusBar_ != null)
            setStatusBar(savedStatusBar_);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
        jtail_.saveConfiguration(prefs);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        jtail_.applyConfiguration(prefs);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#setStatusBar(IStatusBar)
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        if (jtail_ == null)
            savedStatusBar_ = statusBar;
        else
            jtail_.setStatusBar(statusBar);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
        jtail_.saveConfiguration(null);
    }
}