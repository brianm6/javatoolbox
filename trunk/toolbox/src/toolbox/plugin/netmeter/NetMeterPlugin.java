package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import nu.xom.Element;

import toolbox.util.ui.layout.GridLayoutPlus;
import toolbox.workspace.AbstractPlugin;
import toolbox.workspace.PreferencedException;

/**
 * A NetMeter Plugin connects to another instance of NetMeter Plugin for the
 * purposes of determining the max data transfer rate between the two nodes.
 * <p>
 * <ul>
 *  <li>NetMeterPlugin contains one and only one ClientFactoryView.
 *  <li>NetMeterPlugin contains one and only one ServerFactoryView.
 *  <li>NetMeterPlugin handles presentation and layout of ClientViews.
 *  <li>NetMeterPlugin handles presentation and layout of ServerViews.
 *  <li>NetmeterPlugin arranges ClientViews and ServerViews in a stacked grid.
 * </ul>
 * 
 * @see toolbox.plugin.netmeter.Client
 * @see toolbox.plugin.netmeter.Server
 */
public class NetMeterPlugin extends AbstractPlugin
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
    
    /**
     * User interface view.
     */
    private JComponent view_;
    
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
        view_ = new JPanel(new BorderLayout());
        
        FormLayout layout = new FormLayout("f:p:g","f:p:n");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.append(new ClientFactoryView(this));
        builder.append(new ServerFactoryView(this));
        
        grid_ = new JPanel(new GridLayoutPlus(2, 2));
        view_.add(builder.getPanel(), BorderLayout.WEST);
        view_.add(grid_, BorderLayout.CENTER);
    }

    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map props)
    {
        buildView();
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "NetMeter";
    }

    
    /*
     * @see toolbox.workspace.IPlugin#getView()
     */
    public JComponent getView()
    {
        return view_;
    }

    
    /*
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "NetMeter";
    }

    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy()
    {
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface 
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
    }

    
    /*
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
    }
}