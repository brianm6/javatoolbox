package toolbox.tunnel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.PreferencedUtil;
import toolbox.util.SocketUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.io.MulticastOutputStream;
import toolbox.util.io.PrintableOutputStream;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.service.Startable;
import toolbox.util.statemachine.StateMachine;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * Tunnels TCP traffic through a local proxy port before it is forwarded to
 * the intended recipient. This allows a view into a "real time" window
 * of the data being sent back and forth. Very useful for socket level
 * degugging.
 * <pre class="snippet">
 *
 * Without Tunnel:
 *
 *              Client                Server:80
 *             --------              --------
 *                 |                     |
 *                 |        read         |
 *                 |-------------------->|
 *                 |                     |--.
 *                 |                     |  | do something
 *                 |                     |<-'
 *                 |        write        |
 *                 |<--------------------|
 *                 |                     |
 *
 * With Tunnel:
 *
 *              Client        Tunnel         Server
 *            [localhost]  [localhost:80]  [server:80]
 *            -----------  --------------  -----------
 *                 |              |            |
 *                 |     write    |            |
 *                 |------------->|            |
 *                 |              |--.         |
 *                 |              |  |out sink |
 *                 |              |<-'         |
 *                 |              |            |
 *                 |              |   write    |
 *                 |              |----------->|
 *                 |              |            |--.
 *                 |              |            |  | do something
 *                 |              |            |<-'
 *                 |              |    write   |
 *                 |              |<-----------|
 *                 |              |            |
 *                 |              |--.         |
 *                 |              |  |in sink  |
 *                 |              |<-'         |
 *                 |     write    |            |
 *                 |<-------------|            |
 *                 |              |            |
 * </pre>
 *
 * Sequence of events:
 * <ol>
 *   <li>socket client connects to TcpTunnel (localhost:port)
 *   <li>TcpTunnel connects to remote host (remotehost:remoteport)
 *   <li>socket client sends request to TcpTunnel
 *   <li>TcpTunnel dumps request to screen and forwards to remote host
 *   <li>Remote host receives request and replies to TcpTunnel
 *   <li>TcpTunnel dumps response to screen and forwards to socket client
 *   <li>Socket client receives response and processes as normal
 * </ol>
 */
public class TcpTunnel implements TcpTunnelListener, Startable, IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(TcpTunnel.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    // Node and attributes
    public static final String NODE_TCPTUNNEL       = "TCPTunnel";
    public static final String PROP_SUPPRESS_BINARY =   "suppressBinary";
    public static final String PROP_REMOTE_PORT     =   "remotePort";
    public static final String PROP_REMOTE_HOST     =   "remoteHost";
    public static final String PROP_LOCAL_PORT      =   "localPort";
    
    /**
     * Java bean properties that are saved via the IPreferenced interface.
     */
    public static final String[] PROPS_SAVED = 
    {
        PROP_LOCAL_PORT,
        PROP_REMOTE_HOST,
        PROP_REMOTE_PORT,
        PROP_SUPPRESS_BINARY 
    };

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Stream name for event inputstream.
     */
    private static final String NAME_STREAM_IN = "client <-- tunnel";

    /**
     * Stream name for event outputstream.
     */
    private static final String NAME_STREAM_OUT = "tunnel --> server";
    
    /**
     * Substitution character used to suppress printout of non-ascii characters.
     */
    private static final String SUBSTITUTION_CHAR = ".";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Tunnel server socket on localhost.
     */
    private ServerSocket serverSocket_;

    /**
     * Tunnel port on localhost.
     */
    private int localPort_;

    /**
     * Destination hostname.
     */
    private String remoteHost_;

    /**
     * Destination port.
     */
    private int remotePort_;
    
    /**
     * Tunnel event listeners.
     */
    private List listeners_;

    /**
     * Total number of incoming bytes.
     */
    private int inTotal_;

    /**
     * Total number of outgoing bytes.
     */
    private int outTotal_;

    /**
     * Sink for incoming data from the remote host.
     */
    private OutputStream incomingSink_;

    /**
     * Sink for outgoing data to the remote host.
     */
    private OutputStream outgoingSink_;

    /**
     * Flag for supressing binary data.
     */
    private boolean supressBinary_;

    /**
     * Stream that suppresses incoming binary data.
     */
    private PrintableOutputStream printableIncomingSink_;

    /**
     * Stream that suppresses outgoing binary data.
     */
    private PrintableOutputStream printableOutgoingSink_;

    /**
     * State machine for this tunnels lifecycle.
     */
    private StateMachine machine_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint.
     *
     * @param args [0] = listenport
     *             [1] = host to tunnel to
     *             [2] = port to tunnel to
     */
    public static void main(String args[])
    {
        if (args.length != 3)
        {
            System.err.println(
                "Usage: java " 
                + TcpTunnel.class.getName() 
                + " listenport tunnelhost tunnelport");
            
            System.exit(1);
        }

        int localPort = Integer.parseInt(args[0]);
        String tunnelhost = args[1];
        int tunnelport = Integer.parseInt(args[2]);
        
        try
        {
            TcpTunnel tunnel = new TcpTunnel(localPort, tunnelhost, tunnelport);
            //tunnel.setIncomingSink(System.out);
            //tunnel.setOutgoingSink(System.out);
            tunnel.addTcpTunnelListener(tunnel);
            tunnel.start();
        }
        catch (ServiceException e)
        {
            logger_.error(e);
        }
        
        System.out.println(
            "TcpTunnel: Ready to service connections on port " + localPort);
        
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TcpTunnel listening on port 8888 and re-routing to port 9999
     * on the local host.
     */
    public TcpTunnel()
    {
        this(8888, "localhost", 9999);
    }

    
    /**
     * Creates a TcpTunnel with incoming/outgoing data echoed to System.out.
     *
     * @param listenPort Local port to listen on.
     * @param remoteHost Remote host to connect to.
     * @param remotePort Remote port to connect to.
     */
    public TcpTunnel(int listenPort, String remoteHost, int remotePort)
    {
        machine_ = ServiceUtil.createStateMachine(this);
        listeners_  = new ArrayList();
        inTotal_    = 0;
        outTotal_   = 0;

        setLocalPort(listenPort);
        setRemoteHost(remoteHost);
        setRemotePort(remotePort);
        setSuppressBinary(false);
        setIncomingSink(System.out);
        setOutgoingSink(System.out);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets the sink for incoming data.
     *
     * @param stream Sink for incoming data.
     */
    public void setIncomingSink(OutputStream stream)
    {
        incomingSink_ = stream;
    }


    /**
     * Sets the sink for outgoing data.
     *
     * @param stream Sink for outgoing data.
     */
    public void setOutgoingSink(OutputStream stream)
    {
        outgoingSink_ = stream;
    }


    /**
     * Returns the supressBinary.
     *
     * @return boolean
     */
    public boolean isSuppressBinary()
    {
        return supressBinary_;
    }


    /**
     * Sets the supressBinary.
     *
     * @param supressBinary The supressBinary to set.
     */
    public void setSuppressBinary(boolean supressBinary)
    {
        supressBinary_ = supressBinary;

        if (printableIncomingSink_ != null)
            printableIncomingSink_.setEnabled(supressBinary_);

        if (printableOutgoingSink_ != null)
            printableOutgoingSink_.setEnabled(supressBinary_);
    }

    
    /**
     * Returns the port on the local machine where the tunnel is listening for
     * connections to route to a remote machine.
     * 
     * @return int
     */
    public int getLocalPort()
    {
        return localPort_;
    }


    /**
     * Sets the local port number that the tunnel is listening on.
     * 
     * @param localPort Local port number.
     */
    public void setLocalPort(int localPort)
    {
        localPort_ = localPort;
    }


    /**
     * Returns the hostname of the remote machine to tunnel connections to.
     * 
     * @return String
     */
    public String getRemoteHost()
    {
        return remoteHost_;
    }


    /**
     * Sets the hostname of the remote machine to tunnel connections to.
     * 
     * @param remoteHost Remote host.
     */
    public void setRemoteHost(String remoteHost)
    {
        remoteHost_ = remoteHost;
    }


    /**
     * Returns the port on the remote machine to tunnel connections to.
     * 
     * @return int
     */
    public int getRemotePort()
    {
        return remotePort_;
    }


    /**
     * Sets the port on the remote machine to tunnel connections to.
     * 
     * @param remotePort Remote port.
     */
    public void setRemotePort(int remotePort)
    {
        remotePort_ = remotePort;
    }
    
    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Starts the tunnel.
     * 
     * @see toolbox.util.service.Startable#start()
     */
    public void start() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.START);
        
        try
        {
            // Server socket on listenPort
            serverSocket_ = new ServerSocket(localPort_);
            serverSocket_.setSoTimeout(5000);

            Thread t = new Thread(new ServerThread());
            t.start();
            fireTunnelStarted();
        }
        catch (IOException ioe)
        {
            throw new ServiceException(ioe);
        }
        
        machine_.transition(ServiceTransition.START);
    }

    
    /**
     * Stops the tunnel.
     * 
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop()
    {
        machine_.checkTransition(ServiceTransition.STOP);
        SocketUtil.close(serverSocket_);
        fireStatusChanged("Tunnel stopped");
        machine_.transition(ServiceTransition.STOP);
    }

    
    /**
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return getState() == ServiceState.RUNNING;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_TCPTUNNEL, new Element(NODE_TCPTUNNEL));
        PreferencedUtil.readPreferences(this, root, PROPS_SAVED);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_TCPTUNNEL);
        PreferencedUtil.writePreferences(this, root, PROPS_SAVED);
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // Event listener support
    //--------------------------------------------------------------------------

    /**
     * Adds a TcpTunnelListener.
     *
     * @param listener TcpTunnelListener to add.
     */
    public void addTcpTunnelListener(TcpTunnelListener listener)
    {
        listeners_.add(listener);
    }


    /**
     * Fires notifcation that the status of the tunnel has changed to all
     * registered listeners.
     *
     * @param status New status
     */
    protected void fireStatusChanged(String status)
    {
        for (Iterator i = listeners_.iterator(); i.hasNext();)
             ((TcpTunnelListener) i.next()).statusChanged(this, status);
    }


    /**
     * Fires notifcation that the number of bytes read has changed to all
     * registered listeners.
     *
     * @param connRead Bytes read during the life of the last connection.
     */
    protected void fireBytesRead(int connRead)
    {
        for (Iterator i = listeners_.iterator(); i.hasNext();)
            ((TcpTunnelListener) i.next()).bytesRead(this, connRead, inTotal_);
    }


    /**
     * Fires notifcation that the number of bytes written has changed to all
     * registered listeners.
     *
     * @param connWritten Bytes written during the life of the last connection.
     */
    protected void fireBytesWritten(int connWritten)
    {
        for (Iterator i = listeners_.iterator(); i.hasNext();)
            ((TcpTunnelListener) i.next()).bytesWritten(
                this, connWritten, outTotal_);
    }


    /**
     * Fires notifcation that the tunnel has started.
     */
    protected void fireTunnelStarted()
    {
        for (Iterator i = listeners_.iterator(); i.hasNext();)
            ((TcpTunnelListener) i.next()).tunnelStarted(this);
    }

    //--------------------------------------------------------------------------
    // TcpTunnelListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.tunnel.TcpTunnelListener#statusChanged(
     *      toolbox.tunnel.TcpTunnel, java.lang.String)
     */
    public void statusChanged(TcpTunnel tunnel, String status)
    {
        System.out.println(status);
    }


    /**
     * @see toolbox.tunnel.TcpTunnelListener#bytesRead(
     *      toolbox.tunnel.TcpTunnel, int, int)
     */
    public void bytesRead(TcpTunnel tunnel, int connBytesRead,
                          int totalBytesRead)
    {
        System.out.println("[Bytes read: " + connBytesRead + "]");
    }


    /**
     * @see toolbox.tunnel.TcpTunnelListener#bytesWritten(
     *      toolbox.tunnel.TcpTunnel, int, int)
     */
    public void bytesWritten(TcpTunnel tunnel, int connBytesWritten,
                             int totalBytesWritten)
    {
        System.out.println("[Bytes written: " + connBytesWritten + "]");
    }


    /**
     * @see toolbox.tunnel.TcpTunnelListener#tunnelStarted(
     *      toolbox.tunnel.TcpTunnel)
     */
    public void tunnelStarted(TcpTunnel tunnel)
    {
        System.out.println("Tunnel started");
    }

    //--------------------------------------------------------------------------
    // ServerThread
    //--------------------------------------------------------------------------
    
    /**
     * Thread that creates the tunnel connections.
     */
    class ServerThread implements Runnable
    {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            boolean alreadyListened = false;
            
            while (isRunning())
            {
                try
                {
                    if (serverSocket_.isClosed())
                    {
                        logger_.debug("Tunnel socket server is closed. Exiting..");
                        return;
                    }
                    
                    if (!alreadyListened)
                        fireStatusChanged( 
                            "Listening for connections on port " + localPort_);

                    // Client socket
                    Socket client = serverSocket_.accept();

                    // Remote socket
                    Socket remote = new Socket(remoteHost_, remotePort_);

                    fireStatusChanged(
                        "Tunnelling port " + localPort_ +
                        " to port " + remotePort_ +
                        " on host " + remoteHost_ + " ...");
                    
//==============================================================================
//
//                                                              +--------> outgoingSink_
//                                                              |
//                                                              +--------> eos (event output stream)
//                                                 |outStreams  |
//        cs.getInputStream()------------>| =====> |---------------------> rs.getOutputStream()
//                                        |        |
//                            instreams   | tunnel |
//                                        |        |
//     cs.getOutputStream()<--------------| <===== |<------------ rs.geInputStream()
//                          |             |        |
//     eventinputstream<----+
//                          |
//     incomingSink_<-------+
//
// =============================================================================

                    //----------Outgoing streams--------------------------------
                    
                    MulticastOutputStream outStreams = new MulticastOutputStream();
                    outStreams.addStream(remote.getOutputStream());

                    MonitoredOutputStream mos = new MonitoredOutputStream(
                        NAME_STREAM_OUT, new NullOutputStream());

                    mos.addOutputStreamListener(new MyOutputStreamListener());
                    outStreams.addStream(mos);

                    printableOutgoingSink_ = new PrintableOutputStream(
                        outgoingSink_, supressBinary_, SUBSTITUTION_CHAR);

                    outStreams.addStream(printableOutgoingSink_);

                    //----------Incoming streams--------------------------------
                    
                    MulticastOutputStream inStreams = new MulticastOutputStream();
                    inStreams.addStream(client.getOutputStream());

                    MonitoredOutputStream mis = new MonitoredOutputStream(
                        NAME_STREAM_IN, new NullOutputStream());

                    mis.addOutputStreamListener(new MyOutputStreamListener());
                    inStreams.addStream(mis);

                    printableIncomingSink_ = new PrintableOutputStream(
                        incomingSink_, supressBinary_, SUBSTITUTION_CHAR);

                    inStreams.addStream(printableIncomingSink_);

                    //----------------------------------------------------------
                    
                    new Thread(new Relay(
                        new BufferedInputStream(client.getInputStream()),
                        new BufferedOutputStream(outStreams)),
                        "TcpTunnel:incomingSink").start();

                    new Thread(new Relay(
                        new BufferedInputStream(remote.getInputStream()),
                        new BufferedOutputStream(inStreams)),
                        "TcpTunnel:outgoingSink").start();
                }
                catch (SocketTimeoutException ste)
                {
                    alreadyListened = true;
                }
                catch (Exception e)
                {
                    if (getState() != ServiceState.STOPPED)
                        ExceptionUtil.handleUI(e, logger_);
                }
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // OutputStreamListener
    //--------------------------------------------------------------------------

    /**
     * Listener that reports total number of bytes written/read from the tunnel
     * after the connection is closed.
     */
    class MyOutputStreamListener 
        implements MonitoredOutputStream.OutputStreamListener
    {
        /**
         * Tallies up counts and generates bytesRead/Written events when the
         * stream is closed.
         *
         * @see toolbox.util.io.MonitoredOutputStream.OutputStreamListener
         *      #streamClosed(MonitoredOutputStream)
         */
        public void streamClosed(MonitoredOutputStream stream)
        {
            String name = stream.getName();
            int count = (int) stream.getCount();

            if (name.equals(NAME_STREAM_IN))
            {
                //logger_.debug(
                //  "Tallying bytes on stream close event: " +stream.getName());

                inTotal_ += count;
                fireBytesRead(count);
            }
            else if (name.equals(NAME_STREAM_OUT))
            {
                //logger_.debug(
                //  "Tallying bytes on stream close event: " +stream.getName());

                outTotal_ += count;
                fireBytesWritten(count);
            }
            else
            {
                throw new IllegalArgumentException(
                    "Invalid stream name:" + name);
            }
        }
     
        
        /**
         * @see toolbox.util.io.MonitoredOutputStream.OutputStreamListener
         *      #streamFlushed(toolbox.util.io.MonitoredOutputStream)
         */
        public void streamFlushed(MonitoredOutputStream stream)
        {
            ; // NO-OP
        }
    }
}