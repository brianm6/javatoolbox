package toolbox.tunnel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nu.xom.Element;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.StringUtils;
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
 * Tunnels TCP traffic through a local proxy port before it is forwarded to the 
 * intended recipient. This allows a view into a "real time" window of the data 
 * being sent back and forth. Very useful for socket level degugging.
 * 
 * <pre class="snippet">
 *
 * Without Tunnel:
 *
 *              Client                Server:80
 *             --------              --------
 *                 |                     |
 *                 |        write        |
 *                 |-------------------->|
 *                 |                     |--.
 *                 |                     |  | do something
 *                 |                     |<-'
 *                 |        read         |
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
 *                 |              |    read    |
 *                 |              |<-----------|
 *                 |              |            |
 *                 |              |--.         |
 *                 |              |  |in sink  |
 *                 |              |<-'         |
 *                 |     read     |            |
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
public class TcpTunnel implements Startable, IPreferenced {
    
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
    public static final String[] PROPS_SAVED = {
        PROP_LOCAL_PORT,
        PROP_REMOTE_HOST,
        PROP_REMOTE_PORT,
        PROP_SUPPRESS_BINARY 
    };

    // --------------------------------------------------------------------------
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
     * Null value OK 
     */
    private String bindAddress_;
    
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
    public static void main(String args[]) {
        try {
            CommandLineParser parser = new PosixParser();
            Options options = new Options();

            // Create command line options            
            Option quietOption = 
                new Option("q", "quiet", false, "Quiet (don't print tunneled data)");
            
            Option transferredOption = 
                new Option("t", "transferred", false, "Prints bytes transferred in real time");
            
            Option binaryOption = 
                new Option("b", "suppressBinary", false, "Suppresses binary output");
            
            Option helpOption = 
                new Option("h", "help", false, "Print usage");
            
            Option bindAddressOption = 
                new Option("l", "bindAddress", true, "Address on this machine to bind to if more than one available");
            
            bindAddressOption.setArgName("Bind address");
            bindAddressOption.setArgs(1);
            
            options.addOption(bindAddressOption);
            options.addOption(helpOption);
            options.addOption(quietOption);        
            options.addOption(transferredOption);
            options.addOption(binaryOption);
    
            // Parse options
            CommandLine cmdLine = parser.parse(options, args, true);
            
            // Create tunnel w/o specifying config
            final TcpTunnel tunnel = new TcpTunnel();
            
            // Handle options
            for (Iterator i = cmdLine.iterator(); i.hasNext();) {
                Option option = (Option) i.next();
                String opt = option.getOpt();

                if (opt.equals(quietOption.getOpt())) {
                    tunnel.setIncomingSink(new NullOutputStream());
                    tunnel.setOutgoingSink(new NullOutputStream());
                }
                else if (opt.equals(binaryOption.getOpt())) {
                    tunnel.setSuppressBinary(true);
                }
                else if (opt.equals(transferredOption.getOpt())) {
                    Timer t = new Timer(true);
                    t.schedule(new ShowTransferredTask(tunnel), 1000, 1000);
                }
                else if (opt.equals(bindAddressOption.getOpt())) {
                    tunnel.setBindAddress(option.getValue());
                    logger_.warn("Bind address set to: " + option.getValue());
                }
                else if (opt.equals(helpOption.getOpt())) {
                    printUsage(options);
                    return;
                }
                else {
                    logger_.error("Option not handled: " + option);
                }
                
                logger_.info("handling option: " + option);
            }

            // 3 args are : local port, remote host, remote port
            switch (cmdLine.getArgs().length) {
                
                case 3:
                    int localPort = Integer.parseInt(cmdLine.getArgs()[0]);
                    String tunnelhost = cmdLine.getArgs()[1];
                    int tunnelport = Integer.parseInt(cmdLine.getArgs()[2]);

                    tunnel.setLocalPort(localPort);
                    tunnel.setRemoteHost(tunnelhost);
                    tunnel.setRemotePort(tunnelport);
                    tunnel.addTcpTunnelListener(new InternalTcpTunnelListener());
                    tunnel.start();

                    System.out
                        .println("TcpTunnel: Ready to service connections on port "
                            + tunnel.getLocalPort());

                    break;

                // Invalid
                default:
                    printUsage(options);
                    return;
            }
        }
        catch (Exception e) {
            logger_.error("main", e);
        }
        
        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
            logger_.warn("blocking so program does not terminate", e);
        }
    }


    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TcpTunnel listening on port 8888 and re-routing to port 9999 on
     * the local host.
     */
    public TcpTunnel() {
        this(8888, "localhost", 9999);
    }

    
    /**
     * Creates a TcpTunnel with incoming/outgoing data echoed to System.out.
     *
     * @param listenPort Local port to listen on.
     * @param remoteHost Remote host to connect to.
     * @param remotePort Remote port to connect to.
     */
    public TcpTunnel(int listenPort, String remoteHost, int remotePort) {
        this(null, listenPort, remoteHost, remotePort);
    }

    
    /**
     * Takes additional bind address.
     */
    public TcpTunnel(String bindAddress, int listenPort, String remoteHost, int remotePort) {
        machine_ = ServiceUtil.createStateMachine(this);
        listeners_ = new ArrayList();
        inTotal_ = 0;
        outTotal_ = 0;

        setLocalPort(listenPort);
        setRemoteHost(remoteHost);
        setRemotePort(remotePort);
        setSuppressBinary(false);
        setIncomingSink(System.out);
        setOutgoingSink(System.out);
        setBindAddress(bindAddress);
    }
    
    
    // --------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets the sink for incoming data.
     * 
     * @param stream Sink for incoming data.
     */
    public void setIncomingSink(OutputStream stream) {
        incomingSink_ = stream;
    }


    /**
     * Sets the sink for outgoing data.
     * 
     * @param stream Sink for outgoing data.
     */
    public void setOutgoingSink(OutputStream stream) {
        outgoingSink_ = stream;
    }


    /**
     * Returns the supressBinary.
     * 
     * @return boolean
     */
    public boolean isSuppressBinary() {
        return supressBinary_;
    }


    /**
     * Sets the supressBinary.
     * 
     * @param supressBinary The supressBinary to set.
     */
    public void setSuppressBinary(boolean supressBinary) {
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
    public int getLocalPort() {
        return localPort_;
    }


    /**
     * Sets the local port number that the tunnel is listening on.
     * 
     * @param localPort Local port number.
     */
    public void setLocalPort(int localPort) {
        localPort_ = localPort;
    }


    /**
     * Returns the hostname of the remote machine to tunnel connections to.
     * 
     * @return String
     */
    public String getRemoteHost() {
        return remoteHost_;
    }


    /**
     * Sets the hostname of the remote machine to tunnel connections to.
     * 
     * @param remoteHost Remote host.
     */
    public void setRemoteHost(String remoteHost) {
        remoteHost_ = remoteHost;
    }


    /**
     * Returns the port on the remote machine to tunnel connections to.
     * 
     * @return int
     */
    public int getRemotePort() {
        return remotePort_;
    }


    /**
     * Sets the port on the remote machine to tunnel connections to.
     * 
     * @param remotePort Remote port.
     */
    public void setRemotePort(int remotePort) {
        remotePort_ = remotePort;
    }

    
    public String getBindAddress() {
        return bindAddress_;
    }

    
    public void setBindAddress(String bindAddress) {
        bindAddress_ = bindAddress;
    }


    public long getTotalBytesRead() {
        return inTotal_;
    }

    public long getTotalBytesWritten() {
        return outTotal_;
    }
    
    // --------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Starts the tunnel.
     * 
     * @see toolbox.util.service.Startable#start()
     */
    public void start() throws ServiceException {
        machine_.checkTransition(ServiceTransition.START);

        try {
            // Server socket on listenPort
            InetAddress bindAddress = null;

            if (getBindAddress() != null) {
                bindAddress = InetAddress.getByName(getBindAddress());
                logger_.debug("xxx using address: " + bindAddress);
                logger_.debug("canonical hostname: " + bindAddress.getCanonicalHostName());
                logger_.debug("host address hostname: " + bindAddress.getHostAddress());
                logger_.debug("host name: " + bindAddress.getHostName());
                logger_.debug("xxx");
            }
            
            serverSocket_ = new ServerSocket(localPort_, 0, bindAddress);
            serverSocket_.setSoTimeout(5000);

            Thread t = new Thread(new ServerThread());
            t.start();
            fireTunnelStarted();
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }

        machine_.transition(ServiceTransition.START);
    }

    
    /**
     * Stops the tunnel.
     * 
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() {
        machine_.checkTransition(ServiceTransition.STOP);
        SocketUtil.close(serverSocket_);
        fireStatusChanged("Tunnel stopped");
        machine_.transition(ServiceTransition.STOP);
    }

    
    /*
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning() {
        return getState() == ServiceState.RUNNING;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState() {
        return (ServiceState) machine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_TCPTUNNEL, new Element(NODE_TCPTUNNEL));
        PreferencedUtil.readPreferences(this, root, PROPS_SAVED);
    }


    /*
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException {
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
    public void addTcpTunnelListener(TcpTunnelListener listener) {
        listeners_.add(listener);
    }


    /**
     * Fires notifcation that the status of the tunnel has changed to all
     * registered listeners.
     * 
     * @param status New status
     */
    protected void fireStatusChanged(String status) {
        for (Iterator i = listeners_.iterator(); i.hasNext();)
            ((TcpTunnelListener) i.next()).statusChanged(this, status);
    }


    /**
     * Fires notifcation that the number of bytes read has changed to all
     * registered listeners.
     * 
     * @param connRead Bytes read during the life of the last connection.
     */
    protected void fireBytesRead(int connRead) {
        for (Iterator i = listeners_.iterator(); i.hasNext();)
            ((TcpTunnelListener) i.next()).bytesRead(this, connRead, inTotal_);
    }


    /**
     * Fires notifcation that the number of bytes written has changed to all
     * registered listeners.
     * 
     * @param connWritten Bytes written during the life of the last connection.
     */
    protected void fireBytesWritten(int connWritten) {
        for (Iterator i = listeners_.iterator(); i.hasNext();)
            ((TcpTunnelListener) i.next()).bytesWritten(
                this, connWritten, outTotal_);
    }


    /**
     * Fires notifcation that the tunnel has started.
     */
    protected void fireTunnelStarted() {
        for (Iterator i = listeners_.iterator(); i.hasNext();)
            ((TcpTunnelListener) i.next()).tunnelStarted(this);
    }


    /**
     * Fires notification that a new connection has been accepted by the 
     * tunnel.
     * 
     * @param mis Monitor for the incoming sink.
     * @param mos Monitor for the outgoign sink.
     */
    protected void fireNewConnection(
        MonitoredOutputStream mis,
        MonitoredOutputStream mos) {

        logger_.debug("fireNewConnection: " + listeners_);

        for (Iterator i = listeners_.iterator(); i.hasNext();)
            ((TcpTunnelListener) i.next()).newConnection(mis, mos);
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Prints program usage. 
     */
    private static void printUsage(Options options) {
        HelpFormatter f = new HelpFormatter();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        f.printHelp(
            pw, 
            80, 
            "tcptunnel " + "[option]" + " [port] [rhost] [rport]", 
            "", 
            options, 
            2, 
            4,
            "",
            false);
        
        System.out.println(sw.toString());
    }
    
    //--------------------------------------------------------------------------
    // ServerThread
    //--------------------------------------------------------------------------
    
    /**
     * Thread that creates the tunnel connections.
     */
    class ServerThread implements Runnable {

        /*
         * @see java.lang.Runnable#run()
         */
        public void run() {
            boolean alreadyListened = false;

            while (isRunning()) {
                try {
                    if (serverSocket_.isClosed()) {
                        logger_.debug("Tunnel socket server is closed. Exiting..");
                        return;
                    }
                    
                    if (!alreadyListened) {
                        fireStatusChanged(
                            "Listening for connections on " + serverSocket_.getInetAddress() + ":" + serverSocket_.getLocalPort());
//                            (getBindAddress() == null 
//                                 ? InetAddress.getLocalHost().getHostAddress()
//                                 : getBindAddress()) + ":" + localPort_);
                    }

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
//                                                              +---output to console------> printableOutgoingSink_(outgoingSink_)
//                                        | tunnel |            |
//                                        |        |            +---monitor stream---------> MonitoredOutputStream mos
//                                        |        | outStreams |
//    client.getInputStream()------------>| r--w-> |----------------intended recipient-----> remote.getOutputStream()
//                                        |        |
//                                        |        |
//                            inStreams   |        |
// client.getOutputStream()<--------------| <-w--r |<------------ remote.geInputStream()
//                          |             |        |
//     eventinputstream<----+             |        |
//                          |             |        |
//     incomingSink_<-------+
//
// =============================================================================

                    //----------Outgoing streams--------------------------------

                    MonitoredOutputStream mos = new MonitoredOutputStream(
                        NAME_STREAM_OUT, new NullOutputStream());

                    mos.addOutputStreamListener(new InternalOutputStreamListener());

                    printableOutgoingSink_ = new PrintableOutputStream(
                        outgoingSink_, supressBinary_, SUBSTITUTION_CHAR);

                    MulticastOutputStream outStreams = new MulticastOutputStream();
                    outStreams.addStream(remote.getOutputStream());
                    outStreams.addStream(mos);
                    outStreams.addStream(printableOutgoingSink_);

                    //----------Incoming streams--------------------------------
                    
                    MulticastOutputStream inStreams = new MulticastOutputStream();
                    inStreams.addStream(client.getOutputStream());

                    MonitoredOutputStream mis = new MonitoredOutputStream(
                        NAME_STREAM_IN, new NullOutputStream());

                    mis.addOutputStreamListener(new InternalOutputStreamListener());
                    inStreams.addStream(mis);

                    printableIncomingSink_ = new PrintableOutputStream(
                        incomingSink_, supressBinary_, SUBSTITUTION_CHAR);

                    inStreams.addStream(printableIncomingSink_);

                    //----------------------------------------------------------
                    
                    fireNewConnection(mis, mos);
                    
                    new Thread(new Relay(
                        new BufferedInputStream(client.getInputStream()),
                        new BufferedOutputStream(outStreams)),
                        "TcpTunnel:incomingSink").start();

                    new Thread(new Relay(
                        new BufferedInputStream(remote.getInputStream()),
                        new BufferedOutputStream(inStreams)),
                        "TcpTunnel:outgoingSink").start();
                }
                catch (SocketTimeoutException ste) {
                    alreadyListened = true;
                }
                catch (Exception e) {
                    if (getState() != ServiceState.STOPPED)
                        ExceptionUtil.handleUI(e, logger_);
                }
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // InternalOutputStreamListener
    //--------------------------------------------------------------------------

    /**
     * Listener that reports total number of bytes written/read from the tunnel
     * after the connection is closed.
     */
    class InternalOutputStreamListener implements
        MonitoredOutputStream.OutputStreamListener {

        /**
         * Tallies up counts and generates bytesRead/Written events when the
         * stream is closed.
         *
         * @see toolbox.util.io.MonitoredOutputStream.OutputStreamListener#streamClosed(MonitoredOutputStream)
         */
        public void streamClosed(MonitoredOutputStream stream) {
//            String name = stream.getName();
//            int count = (int) stream.getCount();
//
//            if (name.equals(NAME_STREAM_IN)) {
//                //logger_.debug(
//                //  "Tallying bytes on stream close event: " +stream.getName());
//
//                inTotal_ += count;
//                fireBytesRead(count);
//            }
//            else if (name.equals(NAME_STREAM_OUT)) {
//                //logger_.debug(
//                //  "Tallying bytes on stream close event: " +stream.getName());
//
//                outTotal_ += count;
//                fireBytesWritten(count);
//            }
//            else {
//                throw new IllegalArgumentException("Invalid stream name:"
//                    + name);
//            }
        }

        public void streamFlushed(MonitoredOutputStream stream) {
            String name = stream.getName();
            int count = (int) stream.getCount();

            if (NAME_STREAM_IN.equals(name)) {
                fireBytesRead(count - inTotal_);
                inTotal_ = count;
            }
            else if (NAME_STREAM_OUT.equals(name)) {
                fireBytesWritten(count - outTotal_);
                outTotal_ = count;
            }
            else {
                throw new IllegalArgumentException("Invalid stream name:" + name);
            }
        }
    }
    
    // --------------------------------------------------------------------------
    // InternalTcpTunnelListener
    // --------------------------------------------------------------------------
    
    static class InternalTcpTunnelListener implements TcpTunnelListener {

        public void statusChanged(TcpTunnel tunnel, String status) {
            System.out.println("[Status changed: " + status + "]");
        }


        public void bytesRead(
            TcpTunnel tunnel,
            int connBytesRead,
            int totalBytesRead) {
            //System.out.println("[Bytes read: " + connBytesRead + "]");
        }


        public void bytesWritten(
            TcpTunnel tunnel,
            int connBytesWritten,
            int totalBytesWritten) {
            //System.out.println("[Bytes written: " + connBytesWritten + "]");
        }


        public void tunnelStarted(TcpTunnel tunnel) {
            System.out.println("Tunnel started");
        }

        
        public void newConnection(
            MonitoredOutputStream incomingSink, 
            MonitoredOutputStream outgoingSink) {
            System.out.println("New connection!");
        }
    }
    
    // --------------------------------------------------------------------------
    // ShowTransferredTask
    // --------------------------------------------------------------------------
    
    static class ShowTransferredTask extends TimerTask {

        private TcpTunnel tunnel;
        
        public ShowTransferredTask(TcpTunnel tunnel) {
            this.tunnel = tunnel;
        }
        
        public void run() {
            
            String stat = 
                "in = " 
                + DecimalFormat.getIntegerInstance().format(tunnel.getTotalBytesRead()) 
                + " out = " 
                + DecimalFormat.getIntegerInstance().format(tunnel.getTotalBytesWritten());
            
            System.out.print(stat);
            System.out.print(StringUtils.repeat("\b", stat.length()));
        }
    }
}