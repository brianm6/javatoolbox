package toolbox.tunnel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * A TcpTunnel object listens on the given port,
 * and once Start is pressed, will forward all bytes
 * to the given host and port.
 */
public class TcpTunnel
{
    private static final Logger logger_ = 
        Logger.getLogger(TcpTunnel.class);
    
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

        System.out.println("TcpTunnel: ready to rock and roll on port " + 
            listenport);

        ServerSocket ss = new ServerSocket(listenport);

        while (true)
        {
            // accept the connection from my client
            logger_.debug(method + "Waiting to accept...");
            Socket sc = ss.accept();

            // connect to the thing I'm tunnelling for
            logger_.debug(method + "Creating new socket to tunnel " + 
                tunnelhost + ":" + tunnelport);
                
            Socket st = new Socket(tunnelhost, tunnelport);

            logger_.debug(method + "Tunnelling port " + listenport + 
                " to port " + tunnelport + " on host " + tunnelhost);

            // relay the stuff thru
            logger_.debug(method + "Settup in relays...");
            
            new Relay(sc.getInputStream(), st.getOutputStream()).start();
            new Relay(st.getInputStream(), sc.getOutputStream()).start();

            logger_.debug(method + "Done!");
            // that's it .. they're off; now I go back to my stuff.
        }
    }
}