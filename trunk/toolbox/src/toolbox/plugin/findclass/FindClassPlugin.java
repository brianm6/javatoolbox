package toolbox.findclass;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JMenuBar;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * Plugin that wraps JFindClass
 */
public class JFindClassPlugin implements IPlugin
{
    /** Logger */
    public static final Logger logger_ =
        Logger.getLogger(JFindClassPlugin.class);
        
    private JFindClass jfindClass_;
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getName()
     */
    public String getName()
    {
        return "Find Class";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public Component getComponent()
    {
        return jfindClass_.getContentPane();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getMenuBar()
     */
    public JMenuBar getMenuBar()
    {
        return jfindClass_.getJMenuBar();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#init()
     */
    public void init()
    {
        try
        {
            jfindClass_ = new JFindClass();        
        }
        catch (Exception ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
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
    }
}
