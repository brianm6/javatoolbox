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

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.ResourceCloser;
import toolbox.util.io.EventOutputStream;
import toolbox.util.io.MulticastOutputStream;
import toolbox.util.io.NullOutputStream;

/**
 * A TcpTunnel serves as a transparent intermediary between a TCP conection on 
 * the current host and a remote host. By serving as an intermediary, traffic
 * on the TCP connection can be captured and saved for later analysis and aid 
 * in trouble shooting protocol level issues. The local listen port is the
 * port that you will redirect your client application to communicate with.
 * The remote host and port is the connection endpoint between.
 * 
 * <pre>
 * Sequence of events:
 * 
 * 1. socket client connects to TcpTunnel (localhost:port)
 * 2. TcpTunnel connects to remote host (remotehost:remoteport)
 * 3. socket client sends request to TcpTunnel
 * 4. TcpTunnel dumps request to screen and forwards to remote host
 * 5. Remote host receives request and replies to TcpTunnel
 * 6. TcpTunnel dumps response to screen and forwards to socket client
 * 7. Socket client receives response and processes as normal
 * </pre>
 */
public class TcpTunnel implements TcpTunnelListener
{
    private static final Logger logger_ = 
        Logger.getLogger(TcpTunnel.class);

    // Stream names used with EventOutputStream
    private static final String STREAM_IN  = "in";
    private static final String STREAM_OUT = "out";

    private ServerSocket ss_;
    private boolean      stop_ = false;
    private int          listenPort_;
    private String       remoteHost_;
    private int          remotePort_;
    private List         outgoingStreams_;
    private List         incomingStreams_;
    private List         listeners_;
    
    private int inTotal_;
    private int outTotal_;
    
    /**
     * Entrypoint 
     * 
     * @param  args  [0] = listenport
     *               [1] = host to tunnel to
     *               [2] = port to tunnel to
     * @throws IOException on IO error
     */
    public static void main(String args[]) throws IOException
    {
        String method = "[main  ] ";

        if (args.length != 3)
        {
            System.err.println("Usage: java " + TcpTunnel.class.getName() + 
                               " listenport tunnelhost tunnelport");
            System.exit(1);
        }

        int listenport = Integer.parseInt(args[0]);
        String tunnelhost = args[1];
        int tunnelport = Integer.parseInt(args[2]);

        System.out.println(
            "TcpTunnel: Ready to service connections on port " + listenport);

        TcpTunnel tunnel = new TcpTunnel(listenport, tunnelhost, tunnelport);
        tunnel.addOutgoingStream(System.out);
        tunnel.addIncomingStream(System.out);
        tunnel.addTcpTunnelListener(tunnel);
        tunnel.start();
    }

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TcpTunnel
     * 
     * @param  listenPort  Local port to listen on
     * @param  remoteHost  Remote host to connect to
     * @param  remotePort  Remote port to connect to
     */
    public TcpTunnel(int listenPort, String remoteHost, int remotePort)
    {
        listenPort_ = listenPort;
        remoteHost_ = remoteHost;
        remotePort_ = remotePort;
        
        incomingStreams_ = new ArrayList();
        outgoingStreams_ = new ArrayList();
        listeners_       = new ArrayList();
        inTotal_         = 0;
        outTotal_        = 0;
    }    
       
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
                  
    /**
     * Adds an output stream to the collection of incoming streams that
     * data from the remote host will be multicast to
     * 
     * @param  stream  Stream to add to multicast group
     */
    public void addIncomingStream(OutputStream stream)
    {
        incomingStreams_.add(stream);    
    }

    /**
     * Adds an output stream to the collection of outgoing streams that
     * sends data from the local socket client to the remote host
     * 
     * @param  stream  Stream to add to multicast group
     */
    public void addOutgoingStream(OutputStream stream)
    {
        outgoingStreams_.add(stream);    
    }
        
    /**
     * Starts the tunnel
     */
    public void start()
    {
        boolean alreadyListened = false;
        
        try
        {
            // Server socket on listenPort
            ss_ = new ServerSocket(listenPort_);
            ss_.setSoTimeout(5000);
            stop_ = false;
            
            while (!stop_)
            {
                try
                {
                    if (!ss_.isClosed())
                    {
                        if (!alreadyListened)
                            fireStatusChanged(
                                "Listening for connections on port " + 
                                    listenPort_);

                        // Client socket
                        Socket cs = ss_.accept();
    
                        // Remote socket
                        Socket rs = new Socket(remoteHost_,remotePort_);
                        
                        fireStatusChanged(
                            "Tunnelling port "+ listenPort_ + 
                            " to port " + remotePort_ + 
                            " on host " + remoteHost_ + " ...");
    
                        // relay the stuff thru. Make multicast output streams
                        // that send to the socket and also to the textarea
                        // for each direction
                        
                        MulticastOutputStream outStreams = 
                            new MulticastOutputStream();
                            
                        outStreams.addStream(rs.getOutputStream());
                        
                        EventOutputStream eos = 
                            new EventOutputStream(
                                STREAM_OUT, new NullOutputStream());
                                
                        eos.addListener(new OutputStreamListener());
                            
                        outStreams.addStream(eos);
                        
                        for (Iterator i = outgoingStreams_.iterator(); 
                            i.hasNext();)
                                outStreams.addStream((OutputStream)i.next());
                        
                        MulticastOutputStream inStreams = 
                            new MulticastOutputStream();
                            
                        inStreams.addStream(cs.getOutputStream());
                        
                        EventOutputStream eis = 
                            new EventOutputStream(
                                STREAM_IN, new NullOutputStream());
                        
                        eis.addListener(new OutputStreamListener());    
                        
                        inStreams.addStream(eis);
                        
                        for (Iterator i = incomingStreams_.iterator(); 
                            i.hasNext();)
                                inStreams.addStream((OutputStream)i.next());    

                        new Thread(new Relay(
                            new BufferedInputStream(cs.getInputStream()), 
                            new BufferedOutputStream(outStreams))).start();
                            
                        new Thread(new Relay(
                            new BufferedInputStream(rs.getInputStream()), 
                            new BufferedOutputStream(inStreams))).start();

                        // that's it .. they're off
                    }
                }
                catch (SocketTimeoutException ste)
                {
                    alreadyListened = true;
                }
                catch (Exception e)
                {
                    if (!stop_)
                        ExceptionUtil.handleUI(e, logger_);
                }
            }
        }
        catch (IOException ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }
    }
    
    /**
     * Stops the tunnel
     */              
    public void stop()
    {
        stop_ = true;
        ResourceCloser.close(ss_);    
        fireStatusChanged("Tunnel stopped");
    }
    
    //--------------------------------------------------------------------------
    // Event listener support
    //--------------------------------------------------------------------------
    
    /**
     * Adds a TcpTunnelListener
     * 
     * @param  listener  TcpTunnelListener to add
     */
    public void addTcpTunnelListener(TcpTunnelListener listener)
    {
        listeners_.add(listener);
    }
    
    /**
     * Fires notifcation that the status of the tunnel has changed to all
     * registered listeners.
     * 
     * @param  status  New status
     */
    protected void fireStatusChanged(String status)
    {
        for (Iterator i = listeners_.iterator(); i.hasNext(); )
            ((TcpTunnelListener) i.next()).statusChanged(this, status);    
    }

    /**
     * Fires notifcation that the number of bytes read has changed to all
     * registered listeners.
     * 
     * @param  connRead  Bytes read during the life of the last connection
     */
    protected void fireBytesRead(int connRead)
    {
        for (Iterator i=listeners_.iterator(); i.hasNext(); )
            ((TcpTunnelListener) i.next()).bytesRead(this, connRead, inTotal_);
    }

    /**
     * Fires notifcation that the number of bytes written has changed to all
     * registered listeners.
     * 
     * @param  connWritten  Bytes written during the life of the last connection
     */
    protected void fireBytesWritten(int connWritten)
    {
        for (Iterator i=listeners_.iterator(); i.hasNext(); )
            ((TcpTunnelListener) i.next()).bytesWritten(
                this, connWritten, outTotal_);
    }
    
    //--------------------------------------------------------------------------
    // Interface TcpTunnelListener
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
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    class OutputStreamListener implements EventOutputStream.Listener
    {
        /**
         * Tally up counts and generate bytesRead/Written events when
         * stream is closed
         * 
         * @param  stream  Stream that was closed
         */
        public void streamClosed(EventOutputStream stream)
        {
            String name = stream.getName();
            int count = stream.getCount();
            
            if (name.equals(STREAM_IN))
            {
                inTotal_ += count;
                fireBytesRead(count);
            }
            else if (name.equals(STREAM_OUT))
            {
                outTotal_ += count;
                fireBytesWritten(count);
            }
            else
            {
                throw new IllegalArgumentException(
                    "Invalid stream name:" + name);
            } 
        }

        public void byteWritten(EventOutputStream stream, int b)
        { 
        }
        
        public void streamFlushed(EventOutputStream stream)
        {
        }
    }
}