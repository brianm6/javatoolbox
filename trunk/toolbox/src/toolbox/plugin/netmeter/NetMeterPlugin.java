package toolbox.plugin.netmeter;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import nu.xom.Element;
import toolbox.workspace.IPlugin;

/**
 * NetMeter Plugin
 */
public class NetMeterPlugin extends JPanel implements IPlugin
{
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a NetMeterPlugin
     */
    public NetMeterPlugin()
    {
    }
    
    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI
     */
    public void buildView()
    {
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "NetMeter";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "NetMeter";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#startup(java.util.Map)
     */
    public void startup(Map props)
    {
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface 
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
    }
}
