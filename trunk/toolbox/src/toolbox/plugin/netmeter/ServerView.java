package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.net.Socket;
import java.util.Collections;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.io.throughput.ThroughputEvent;
import toolbox.util.io.throughput.ThroughputListener;
import toolbox.util.net.AsyncConnectionHandler;
import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;
import toolbox.util.net.ISocketServerListener;
import toolbox.util.net.SocketServer;
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
public class ServerView extends JHeaderPanel 
    implements ServiceListener, ISocketServerListener, ThroughputListener
{
    private static final Logger logger_ = Logger.getLogger(ServerView.class);
    
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
        server_.initialize(Collections.EMPTY_MAP);
        server_.getSocketServer().addSocketServerListener(this);
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
     * @see toolbox.util.service.ServiceListener#serviceStateChanged(
     *      toolbox.util.service.Service)
     */
    public void serviceStateChanged(Service service) throws ServiceException
    {
        statusField_.setText(service.getState().toString());
    }
    
    //--------------------------------------------------------------------------
    // SocketServerListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.net.ISocketServerListener#connectionHandled(
     *      toolbox.util.net.IConnectionHandler)
     */
    public void connectionHandled(IConnectionHandler connectionHandler)
    {
        logger_.debug(StringUtil.addBars("connectionHandled"));
        
        AsyncConnectionHandler async =
            (AsyncConnectionHandler) connectionHandler;
        
        ServerConnectionHandler handler = 
            (ServerConnectionHandler) async.getConnectionHandler();
            
        handler.setServerView(this);
        
        synchronized(handler) 
        {
            handler.notify();
        }
    }


    /**
     * @see toolbox.util.net.ISocketServerListener#serverStarted(
     *      toolbox.util.net.SocketServer)
     */
    public void serverStarted(SocketServer server)
    {
    }


    /**
     * @see toolbox.util.net.ISocketServerListener#serverStopped(
     *      toolbox.util.net.SocketServer)
     */
    public void serverStopped(SocketServer server)
    {
    }


    /**
     * @see toolbox.util.net.ISocketServerListener#socketAccepted(
     *      java.net.Socket, toolbox.util.net.IConnection)
     */
    public void socketAccepted(Socket socket, IConnection connection)
    {
    }
    
    //--------------------------------------------------------------------------
    // ThroughputListener
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.io.throughput.ThroughputListener#currentThroughput(
     *      toolbox.util.io.throughput.ThroughputEvent)
     */
    public void currentThroughput(ThroughputEvent event)
    {
        throughPutField_.setText(event.getThroughput() + "");
    }
}