package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import nu.xom.Element;
import toolbox.util.ui.layout.GridLayoutPlus;
import toolbox.workspace.IPlugin;

/**
 * NetMeter Plugin.
 * <br>
 * <ul>
 * <li>NetMeterPlugin contains one and only one ClientFactoryView.
 * <li>NetMeterPlugin contains one and only one ServerFactoryView.
 * <li>NetMeterPlugin handles presentation and layout of ClientViews.
 * <li>NetMeterPlugin handles presentation and layout of ServerViews.
 * <li>NetmeterPlugin arranges ClientViews and ServerViews in a stacked grid.
 * <ul>
 */
public class NetMeterPlugin extends JPanel implements IPlugin
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Default server hostname if none is specified.
     */
    public static final String DEFAULT_HOSTNAME = "localhost";
    
    /**
     * Default server port if none is specified.
     */
    public static final int DEFAULT_PORT = 9999;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Grid that factory created clients and servers are placed on.
     */
    private JPanel grid_;
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a NetMeterPlugin.
     */
    public NetMeterPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds another compartment to the grid.
     * 
     * @param compartment Component to the grid.
     */
    public void addCompartment(JComponent compartment)
    {
        grid_.add(compartment);
        grid_.validate();
    }
        
    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    public void buildView()
    {
        setLayout(new BorderLayout());
        
        JComponent clientFactory = new ClientFactoryView(this);
        JComponent serverFactory = new ServerFactoryView(this);
        
        JComponent factoryPanel = new JPanel(new GridLayoutPlus(2, 1));
        factoryPanel.add(clientFactory);
        factoryPanel.add(serverFactory);
        
        grid_ = new JPanel(new GridLayoutPlus(2, 2));
        
        add(factoryPanel, BorderLayout.WEST);
        add(grid_, BorderLayout.CENTER);
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
        buildView();
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