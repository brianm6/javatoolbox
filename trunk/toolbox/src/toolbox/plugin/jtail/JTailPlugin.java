package toolbox.jtail;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * Plugin Wrapper for JTail
 */
public class JTailPlugin implements IPlugin
{
    private JTail jtail_;
    
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
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
    	jtail_.saveConfiguration();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
    	jtail_.applyConfiguration();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#setStatusBar(IStatusBar)
     */
    public void setStatusBar(IStatusBar statusBar)
    {
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
		jtail_.saveConfiguration();
    }
}