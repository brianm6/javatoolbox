package toolbox.plugin.netmeter;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * ServerView.
 */
public class ServerView extends JPanel implements ServiceListener
{
    /**
     * Composite ServiceView.
     */
    private ServiceView serviceView_;
    
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
     */
    public ServerView(Server server)
    {
        server_ = server;
        server_.addServiceListener(this);
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI.
     */
    protected void buildView()
    {
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BorderLayout());
        add(buildInputPanel(), BorderLayout.CENTER);
        add(new ServiceView(server_), BorderLayout.SOUTH);
        
        if (server_ != null)
            serverPortField_.setText(server_.getPort() + "");
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
     * @see toolbox.plugin.netmeter.ServiceListener#serviceStarted(
     *      toolbox.plugin.netmeter.Service)
     */
    public void serviceStarted(Service service) throws ServiceException
    {
        statusField_.setText("Running...");
    }
    
    
    /**
     * @see toolbox.plugin.netmeter.ServiceListener#servicePaused(
     *      toolbox.plugin.netmeter.Service)
     */
    public void servicePaused(Service service) throws ServiceException
    {
        statusField_.setText("Paused");
    }
    
    
    /**
     * @see toolbox.plugin.netmeter.ServiceListener#serviceResumed(
     *      toolbox.plugin.netmeter.Service)
     */
    public void serviceResumed(Service service) throws ServiceException
    {
        statusField_.setText("Running...");
    }
    
    
    /**
     * @see toolbox.plugin.netmeter.ServiceListener#serviceStopped(
     *      toolbox.plugin.netmeter.Service)
     */
    public void serviceStopped(Service service) throws ServiceException
    {
        statusField_.setText("Stopped");
    }
}