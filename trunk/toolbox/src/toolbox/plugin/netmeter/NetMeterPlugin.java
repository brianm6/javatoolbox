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
 */
public class NetMeterPlugin extends JPanel implements IPlugin
{
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
    // Protected 
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI.
     */
    public void buildView()
    {
        setLayout(new BorderLayout());
        
        ClientReceiver cr = new ClientReceiver();
        ClientFactoryView clientFactory = new ClientFactoryView();
        clientFactory.addRecipient(cr);
        
        //JPanel serverFactory = new ServerFactoryView();
        
        JComponent factoryPanel = new JPanel(new GridLayoutPlus(1,2));
        factoryPanel.add(clientFactory);
        //factoryPanel.add(serverFactory);
        
        grid_ = new JPanel(new GridLayoutPlus(2,2));
        
        
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
    
    class ClientReceiver implements ClientFactoryView.ClientDelivery
    {
        public void acceptDelivery(ClientView clientView)
        {
            grid_.add(clientView);
            grid_.validate();
        }
    }
    
//    class ServerReceiver implements ServerFactoryView.ServerDelivery
//    {
//        public void acceptDelivery(ServerView serverView)
//        {
//            // Do something with this!!!
//        }
//    }
}