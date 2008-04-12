package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.util.Collections;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import org.apache.commons.lang.StringUtils;

import toolbox.forms.SmartComponentFactory;
import toolbox.util.io.throughput.ThroughputEvent;
import toolbox.util.io.throughput.ThroughputListener;
import toolbox.util.service.Service;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceListener;
import toolbox.util.service.ServiceView;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartTextField;

/**
 * ClientView concepts.
 * <ul>
 *  <li>ClientView is a read-only UI component that displays throughput stats.
 *  <li>ClientView is a view and controller in MVC terms.
 *  <li>ClientView contains a non-UI embedded Client which serves as the MVC 
 *      Model
 *  <li>ClientView contains an embedded ServiceView UI component that allows
 *      manipulation of the service state.
 * </ul>
 * 
 * @see toolbox.plugin.netmeter.ClientFactoryView
 */
public class ClientView extends JHeaderPanel 
    implements ServiceListener, ThroughputListener
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Non-UI client object.
     */
    private Client client_;
    
    /**
     * Server hostname field.
     */
    private JSmartTextField serverHostnameField_;
    
    /**
     * Server port field.
     */
    private JSmartTextField serverPortField_;
    
    /**
     * Throuhgput field in kilobytes per second.
     */
    private JTextField throughputField_;
    
    /**
     * Status of the service. Running, stopped, etc...
     */
    private JTextField statusField_;
    
    /**
     * Bandwidth throttle
     */
    private JSlider throttle_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ClientView.
     * 
     * @param client Client attached to this view.
     * @throws ServiceException on service error.
     */
    public ClientView(Client client) throws ServiceException 
    {
        super("Client");
        client_ = client;
        
        // Order or initialization is very important! 

        // buildView() before all so that widgets are non-null so that this
        // component can react to notifications that mutate widgets
        buildView();
        
        // addServiceListener() before initialize() so this component can
        // react to the firing of the initialized event.
        client_.addServiceListener(this);
        
        // initialize() before addThroughputListener() so that clients 
        // internal monitor is non-null.
        client_.initialize(Collections.EMPTY_MAP);
        
        // last
        client_.addThroughputListener(this);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        JPanel content = new JPanel(new BorderLayout());
        content.add(buildInputPanel(), BorderLayout.NORTH);
        
        //content.add(throttleView, BorderLayout.CENTER);
        content.add(new ServiceView(client_), BorderLayout.SOUTH);
        
        if (client_ != null)
        {
            if (StringUtils.isBlank(client_.getHostname()))
                serverHostnameField_.setText("localhost");
            else
                serverHostnameField_.setText(client_.getHostname());

            if (client_.getPort() == 0)
                serverPortField_.setText("9999");
            else
                serverPortField_.setText(client_.getPort() + "");
        }
        
        setContent(content);
    }
    
    
    /**
     * Creates the panel that displays server information.
     * 
     * @return JPanel
     */
    protected JPanel buildInputPanel()
    {
        FormLayout layout = new FormLayout("r:p:g, p, l:p:g", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(SmartComponentFactory.getInstance());
        builder.setDefaultDialogBorder();

        serverHostnameField_ = new JSmartTextField(10);
        serverHostnameField_.setEditable(false);
        builder.append("Server", serverHostnameField_);
        builder.nextLine();
        
        serverPortField_ = new JSmartTextField(10);
        serverPortField_.setEditable(false);
        builder.append("Port", serverPortField_);
        builder.nextLine();

        throughputField_ = new JSmartTextField(10);
        throughputField_.setEditable(false);
        builder.append("Server Throughput", throughputField_);
        builder.nextLine();

        statusField_ = new JSmartTextField(10);
        statusField_.setText("Stopped");
        statusField_.setEditable(false);
        builder.append("Status", statusField_);
        builder.nextLine();

        builder.appendRelatedComponentsGapRow();
        builder.nextLine();
        builder.appendRelatedComponentsGapRow();
        builder.nextLine();

        builder.appendRow("c:p:g");
        CellConstraints cc = new CellConstraints();
        
        builder.add(
            new ThrottleView(client_.getBandwidth()),
            cc.xyw(builder.getColumn(), builder.getRow(), 3, "c,f"));
        
        return builder.getPanel();
    }

    //--------------------------------------------------------------------------
    // ThroughputListener Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.io.throughput.ThroughputListener#currentThroughput(toolbox.util.io.throughput.ThroughputEvent)
     */
    public void currentThroughput(ThroughputEvent event)
    {
        throughputField_.setText(event.getThroughput() + " bytes/s");
    }
    
    //--------------------------------------------------------------------------
    // ServiceListener Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.util.service.ServiceListener#serviceStateChanged(toolbox.util.service.Service)
     */
    public void serviceStateChanged(Service service) throws ServiceException
    {
        statusField_.setText(service.getState().toString());
    }
}