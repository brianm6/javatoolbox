package toolbox.workspace.prefs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.plaf.LookAndFeelConfigurator;
import toolbox.workspace.IPreferenced;

/**
 * PreferencesManager is the single point of reference used by the
 * workspace to manage workspace preferences (not plugins..yet).
 */
public class PreferencesManager implements IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(PreferencesManager.class);
    
    //--------------------------------------------------------------------------
    // XML Constants
    //--------------------------------------------------------------------------
    
    public static final String NODE_PREFS = "Preferences";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Maps a XML node's name to the corresponding Preferences class.
     */
    private Map nodeMap_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PreferencesManager.
     */
    public PreferencesManager()
    {
        nodeMap_ = new HashMap();
        
        IConfigurator proxy = new HttpProxyConfigurator();
        nodeMap_.put(HttpProxyConfigurator.NODE_HTTP_PROXY, proxy);
        SwingUtil.attachPhantom(proxy.getView());
        
        IConfigurator lookAndFeel = new LookAndFeelConfigurator();
        nodeMap_.put(LookAndFeelConfigurator.NODE_LOOK_AND_FEEL, lookAndFeel);
        SwingUtil.attachPhantom(lookAndFeel.getView());
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns an array of the configurators registered with this manager.
     * 
     * @return IConfigurator[]
     */
    public IConfigurator[] getConfigurators()
    {
        return (IConfigurator[]) 
            nodeMap_.values().toArray(new IConfigurator[0]);
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = 
            XOMUtil.getFirstChildElement(
                prefs, 
                NODE_PREFS, 
                new Element(NODE_PREFS));
        
        //
        // Iterate over the child nodes (each one represents a Preferences).
        // Reassosciate by the node's name, instantiate the class and read in 
        // read/apply the prefs.
        //
        
        for (Iterator i = nodeMap_.keySet().iterator(); i.hasNext();)
        {
            String node = (String) i.next();
            IConfigurator config = (IConfigurator) nodeMap_.get(node);
            
            if (config != null)
            {
                config.applyPrefs(root);
            }
            else
            {
                logger_.warn("Preferences for " + node + " not found.");
            }
        }
    }

    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_PREFS);
        
        for (Iterator i = nodeMap_.values().iterator(); i.hasNext();)
        {
            IConfigurator config = (IConfigurator) i.next();
            config.savePrefs(root);
        }
        
        XOMUtil.insertOrReplace(prefs, root);
        
        logger_.debug(StringUtil.banner(root.toXML()));
    }
}