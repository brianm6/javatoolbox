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

import org.apache.commons.io.output.NullOutputStream;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.SocketUtil;
import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.io.MulticastOutputStream;
import toolbox.util.io.PrintableOutputStream;

/**
 * Tunnels TCP traffic through a local proxy port before it is forwarded to
 * the intended recipient. This allows a view into a "real time" window
 * of the data being sent back and forth. Very useful for socket level
 * degugging.
 * <pre>
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
 *
 *
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
    // TODO: Implements Startable
    
    private static final Logger logger_ = Logger.getLogger(TcpTunnel.class);

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

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Server socket for tunnel port on localhost.
     */
    private ServerSocket ss_;

    /**
     * Tunnel port on localhost.
     */
    private int listenPort_;

    /**
     * Flag to shutdown.
     */
    private boolean stopped_;

    /**
     * Intended recipient hostname.
     */
    private String remoteHost_;

    /**
     * Intended recipient port.
     */
    private int remotePort_;

    /**
     * Listeners of tunnel events.
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
        tunnel.setIncomingSink(System.out);
        tunnel.setOutgoingSink(System.out);
        tunnel.addTcpTunnelListener(tunnel);
        tunnel.start();
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TcpTunnel with incoming/outgoing data echoed to System.out.
     *
     * @param listenPort Local port to listen on.
     * @param remoteHost Remote host to connect to.
     * @param remotePort Remote port to connect to.
     */
    public TcpTunnel(int listenPort, String remoteHost, int remotePort)
    {
        listenPort_ = listenPort;
        remoteHost_ = remoteHost;
        remotePort_ = remotePort;
        listeners_  = new ArrayList();
        inTotal_    = 0;
        outTotal_   = 0;

        setSupressBinary(false);
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
    public boolean isSupressBinary()
    {
        return supressBinary_;
    }


    /**
     * Sets the supressBinary.
     *
     * @param supressBinary The supressBinary to set.
     */
    public void setSupressBinary(boolean supressBinary)
    {
        supressBinary_ = supressBinary;

        if (printableIncomingSink_ != null)
            printableIncomingSink_.setEnabled(supressBinary_);

        if (printableOutgoingSink_ != null)
            printableOutgoingSink_.setEnabled(supressBinary_);
    }


    /**
     * Starts the tunnel.
     */
    public void start()
    {
        boolean alreadyListened = false;

        try
        {
            // Server socket on listenPort
            ss_ = new ServerSocket(listenPort_);
            ss_.setSoTimeout(5000);
            stopped_ = false;

            fireTunnelStarted();

            while (!stopped_)
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
                        Socket rs = new Socket(remoteHost_, remotePort_);

                        fireStatusChanged(
                            "Tunnelling port " + listenPort_ +
                            " to port " + remotePort_ +
                            " on host " + remoteHost_ + " ...");

//                                                          +--------> outgoingSink_
//                                                          |
//                                                          +--------> eos (event output stream)
//                                             |outStreams  |
//    cs.getInputStream()------------>| =====> |---------------------> rs.getOutputStream()
//                                    |        |
//                        instreams   | tunnel |
//                                    |        |
// cs.getOutputStream()<--------------| <===== |<------------ rs.geInputStream()
//                      |             |        |
// eventinputstream<----+
//                      |
// incomingSink_<-------+
//

                        MulticastOutputStream outStreams =
                            new MulticastOutputStream();

                        outStreams.addStream(rs.getOutputStream());

                        MonitoredOutputStream mos = 
                            new MonitoredOutputStream(
                                NAME_STREAM_OUT, new NullOutputStream());

                        mos.addOutputStreamListener(
                            new MyOutputStreamListener());

                        outStreams.addStream(mos);

                        printableOutgoingSink_ =
                            new PrintableOutputStream(
                                outgoingSink_, supressBinary_, ".");

                        outStreams.addStream(printableOutgoingSink_);

                        MulticastOutputStream inStreams =
                            new MulticastOutputStream();

                        inStreams.addStream(cs.getOutputStream());

                        MonitoredOutputStream mis =
                            new MonitoredOutputStream(
                                NAME_STREAM_IN, new NullOutputStream());

                        mis.addOutputStreamListener(
                            new MyOutputStreamListener());

                        inStreams.addStream(mis);

                        printableIncomingSink_ =
                            new PrintableOutputStream(
                                incomingSink_, supressBinary_, ".");

                        inStreams.addStream(printableIncomingSink_);

                        new Thread(new Relay(
                            new BufferedInputStream(cs.getInputStream()),
                            new BufferedOutputStream(outStreams)),
                            "TcpTunnel:incomingSink").start();

                        new Thread(new Relay(
                            new BufferedInputStream(rs.getInputStream()),
                            new BufferedOutputStream(inStreams)),
                            "TcpTunnel:outgoingSink").start();
                    }
                }
                catch (SocketTimeoutException ste)
                {
                    alreadyListened = true;
                }
                catch (Exception e)
                {
                    if (!stopped_)
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
     * Stops the tunnel.
     */
    public void stop()
    {
        stopped_ = true;
        SocketUtil.close(ss_);
        fireStatusChanged("Tunnel stopped");
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


    /**
     * @see toolbox.tunnel.TcpTunnelListener#tunnelStarted(
     *      toolbox.tunnel.TcpTunnel)
     */
    public void tunnelStarted(TcpTunnel tunnel)
    {
        System.out.println("Tunnel started");
    }

    //--------------------------------------------------------------------------
    // OutputStreamListener
    //--------------------------------------------------------------------------

    /**
     * Listener that reports totla number of bytes written/read from the tunnel
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