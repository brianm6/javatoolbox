package toolbox.findclass;

import java.util.Map;

import javax.swing.JComponent;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.workspace.IPlugin;

/**
 * Plugin wrapper for JFindClass
 */
public class JFindClassPlugin implements IPlugin
{
    public static final Logger logger_ =
        Logger.getLogger(JFindClassPlugin.class);
        
    /** 
     * Delegate JFindClass 
     */    
    private JFindClass delegate_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /** 
     * Creates a JFindClassPlugin 
     */
    public JFindClassPlugin()
    {
        delegate_ = new JFindClass();
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "Find Class";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return (JComponent) delegate_.getContentPane();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Searches the classpath in addition to arbitrary archives and " +
               "directories for one or more classes. The search string can be "+
               "regular expression for added flexbility.";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#startup(Map)
     */
    public void startup(Map params)
    {
        try
        {
            delegate_.init(params);        
        }
        catch (Exception ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }
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
