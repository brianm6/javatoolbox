package toolbox.plugin.findclass;

import java.util.Map;

import javax.swing.JComponent;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.workspace.IPlugin;

/**
 * Plugin wrapper for FindClassPane.
 */
public class FindClassPlugin implements IPlugin
{
    private static final Logger logger_ = 
        Logger.getLogger(FindClassPlugin.class);
        
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Delegate JFindClass. 
     */    
    private FindClassPane delegate_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /** 
     * Creates a JFindClassPlugin. 
     */
    public FindClassPlugin()
    {
        delegate_ = new FindClassPane();
    }
    
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPlugin#initialize(Map)
     */
    public void initialize(Map params)
    {
        try
        {
            delegate_.initialize(params);        
        }
        catch (Exception ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "Find Class";
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return (JComponent) delegate_;
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return 
          "Searches the classpath in addition to arbitrary archives and " +
          "directories for one or more classes. The search string can be " +
          "regular expression for added flexbility.";
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
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
    public void savePrefs(Element prefs) throws Exception
    {
        delegate_.savePrefs(prefs);    
    }

    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy()
    {
        delegate_.destroy();
    }
}