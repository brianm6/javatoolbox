package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.util.Collections;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import toolbox.util.io.throughput.ThroughputEvent;
import toolbox.util.io.throughput.ThroughputListener;
import toolbox.util.service.Service;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceListener;
import toolbox.util.service.ServiceView;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;

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
        
        ThrottleView throttleView = new ThrottleView(client_.getBandwidth());
        content.add(throttleView, BorderLayout.CENTER);
        content.add(new ServiceView(client_), BorderLayout.SOUTH);
        
        if (client_ != null)
        {
            if (StringUtils.isEmpty(client_.getHostname()))
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
     * Creates the input panel for the server information.
     * 
     * @return JPanel
     */
    protected JPanel buildInputPanel()
    {
        JPanel p = new JPanel(new ParagraphLayout());
        
        // Hostname
        p.add(new JSmartLabel("Server"), ParagraphLayout.NEW_PARAGRAPH);
        serverHostnameField_ = new JSmartTextField(10);
        serverHostnameField_.setEditable(false);
        p.add(serverHostnameField_);

        // Port
        p.add(new JSmartLabel("Port"), ParagraphLayout.NEW_PARAGRAPH);
        serverPortField_ = new JSmartTextField(10);
        serverPortField_.setEditable(false);
        p.add(serverPortField_);
        
        // Throughput
        p.add(new JSmartLabel("Server Throughput"),
            ParagraphLayout.NEW_PARAGRAPH);
        throughputField_ = new JSmartTextField(10);
        throughputField_.setEditable(false);
        p.add(throughputField_);

        // Status
        p.add(new JSmartLabel("Status"), ParagraphLayout.NEW_PARAGRAPH);
        statusField_ = new JSmartTextField(10);
        statusField_.setText("Stopped");
        statusField_.setEditable(false);
        p.add(statusField_);
        
        return p;
    }

    //--------------------------------------------------------------------------
    // ThroughputListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.io.throughput.ThroughputListener#currentThroughput(
     *      toolbox.util.io.throughput.ThroughputEvent)
     */
    public void currentThroughput(ThroughputEvent event)
    {
        throughputField_.setText(event.getThroughput() + " bytes/s");
    }
    
    //--------------------------------------------------------------------------
    // ServiceListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.ServiceListener#serviceStateChanged(
     *      toolbox.util.service.Service, toolbox.util.service.ServiceState, 
     *      toolbox.util.service.ServiceState)
     */
    public void serviceStateChanged(Service service) throws ServiceException
    {
        statusField_.setText(service.getState().toString());
    }
}