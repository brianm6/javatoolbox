package toolbox.plugin.netmeter;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import toolbox.util.StringUtil;
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
 * <li>ClientView is a read-only UI component that displays throughput stats.
 * <li>ClientView is a view and controller in MVC terms.
 * <li>ClientView contains a non-UI embedded Client which serves as the MVC 
 *     Model
 * <li>ClientView contains an embedded ServiceView UI component that allows
 *     manipulation of the service state.
 * </ul>
 */
public class ClientView extends JHeaderPanel implements ServiceListener, 
                                                        StatsListener
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
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ClientView.
     * 
     * @param client Client attached to this view.
     */
    public ClientView(Client client)
    {
        super("Client");
        client_ = client;
        client_.addServiceListener(this);
        client_.addStatsListener(this);
        buildView();
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
        content.add(buildInputPanel(), BorderLayout.CENTER);
        content.add(new ServiceView(client_), BorderLayout.SOUTH);
        
        if (client_ != null)
        {
            if (StringUtil.isNullOrEmpty(client_.getHostname()))
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
    // StatusListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.netmeter.StatsListener#throughput(int)
     */
    public void throughput(int kbs)
    {
        throughputField_.setText(kbs + " KB/s");
    }
    
    //--------------------------------------------------------------------------
    // ServiceListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.ServiceListener#serviceStarted(
     *      toolbox.util.service.Service)
     */
    public void serviceStarted(Service service) throws ServiceException
    {
        statusField_.setText("Running...");
    }
    
    
    /**
     * @see toolbox.util.service.ServiceListener#servicePaused(
     *      toolbox.util.service.Service)
     */
    public void servicePaused(Service service) throws ServiceException
    {
        statusField_.setText("Paused");
    }
    
    
    /**
     * @see toolbox.util.service.ServiceListener#serviceResumed(
     *      toolbox.util.service.Service)
     */
    public void serviceResumed(Service service) throws ServiceException
    {
        statusField_.setText("Running...");
    }
    
    
    /**
     * @see toolbox.util.service.ServiceListener#serviceStopped(
     *      toolbox.util.service.Service)
     */
    public void serviceStopped(Service service) throws ServiceException
    {
        statusField_.setText("Stopped");
    }
}