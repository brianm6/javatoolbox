package toolbox.workspace.prefs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.plaf.LookAndFeelPrefsView;
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
     * Maps a XML node's name to the corresponding PreferencesView class.
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
        
        PreferencesView proxyView = new ProxyView();
        nodeMap_.put(ProxyView.NODE_HTTP_PROXY, proxyView);
        SwingUtil.attachPhantom(proxyView.getView());
        
        PreferencesView lafView = new LookAndFeelPrefsView();
        nodeMap_.put(LookAndFeelPrefsView.NODE_LOOK_AND_FEEL, lafView);
        SwingUtil.attachPhantom(lafView.getView());
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns an array of the preferences views.
     * 
     * @return PreferencesView[]
     */
    public PreferencesView[] getPreferences()
    {
        return (PreferencesView[]) 
            nodeMap_.values().toArray(new PreferencesView[0]);
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
        // Iterate over the child nodes (each one represents a PreferencesView).
        // Reassosciate by the node's name, instantiate the class and read in 
        // read/apply the prefs.
        //
        
        for (Iterator i = nodeMap_.keySet().iterator(); i.hasNext();)
        {
            String node = (String) i.next();
            PreferencesView view = (PreferencesView) nodeMap_.get(node);
            
            if (view != null)
            {
                view.applyPrefs(root);
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
            PreferencesView view = (PreferencesView) i.next();
            view.savePrefs(root);
        }
        
        XOMUtil.insertOrReplace(prefs, root);
        
        logger_.debug(StringUtil.addBars(root.toXML()));
    }
}