package toolbox.plugin.netmeter;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import toolbox.util.service.Service;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceListener;
import toolbox.util.service.ServiceView;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * ServerView is UI component that presents an updateable view of the Server 
 * object.
 * 
 * @see toolbox.plugin.netmeter.Server 
 */
public class ServerView extends JHeaderPanel implements ServiceListener
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Non-UI server component.
     */
    private Server server_;
    
    /**
     * Server port field.
     */
    private JTextField serverPortField_;
    
    /**
     * Service statis field.
     */
    private JTextField statusField_;
    
    /**
     * Throughput field.
     */
    private JTextField throughPutField_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a new ServerView.
     * 
     * @param server Server to attach to the view.
     * @throws ServiceException on construction error.
     */
    public ServerView(Server server) throws ServiceException
    {
        super("Server");
        server_ = server;
        buildView();
        server_.addServiceListener(this);
        server_.initialize();
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
        content.add(new ServiceView(server_), BorderLayout.SOUTH);
        
        if (server_ != null)
            serverPortField_.setText(server_.getPort() + "");
        
        setContent(content);
    }
    
    
    /**
     * Builds the input panel.
     * 
     * @return JPanel
     */
    protected JPanel buildInputPanel()
    {
        // Port
        JPanel p = new JPanel(new ParagraphLayout());
        p.add(new JSmartLabel("Port"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(serverPortField_ = new JSmartTextField(10));
        serverPortField_.setEditable(false);
        
        // Throughput
        p.add(new JSmartLabel("Server Throughput"),
            ParagraphLayout.NEW_PARAGRAPH);
        throughPutField_ = new JSmartTextField(10);
        throughPutField_.setEditable(false);
        p.add(throughPutField_);

        // Status
        p.add(new JSmartLabel("Status"), ParagraphLayout.NEW_PARAGRAPH);
        statusField_ = new JSmartTextField(10);
        statusField_.setText("Stopped");
        statusField_.setEditable(false);
        p.add(statusField_);
        
        return p;
    }
    
    //--------------------------------------------------------------------------
    // ServiceListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.ServiceListener#serviceInitialized(
     *      toolbox.util.service.Service)
     */
    public void serviceInitialized(Service service) throws ServiceException
    {
        statusField_.setText("Initialized");
    }

    
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