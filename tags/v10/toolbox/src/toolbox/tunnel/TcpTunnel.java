package toolbox.tunnel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A <code>TcpTunnel</code> object listens on the given port,
 * and once <code>Start</code> is pressed, will forward all bytes
 * to the given host and port.
 */
public class TcpTunnel
{
	/**
	 * Entrypoint 
	 */
    public static void main(String args[]) throws IOException
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

        System.out.println("TcpTunnel: ready to rock and roll on port " + listenport);

        ServerSocket ss = new ServerSocket(listenport);

        while (true)
        {
            // accept the connection from my client
            Socket sc = ss.accept();

            // connect to the thing I'm tunnelling for
            Socket st = new Socket(tunnelhost, tunnelport);

            System.out.println("TcpTunnel: tunnelling port " + listenport + 
                               " to port " + tunnelport + " on host " + 
                               tunnelhost);

            // relay the stuff thru
            new Relay(sc.getInputStream(), st.getOutputStream(), null).start();
            new Relay(st.getInputStream(), sc.getOutputStream(), null).start();

            // that's it .. they're off; now I go back to my stuff.
        }
    }
}