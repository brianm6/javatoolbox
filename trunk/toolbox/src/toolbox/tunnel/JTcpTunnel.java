package toolbox.tunnel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartOptionPane;

/**
 * A JTcpTunnel object listens on the given port,
 * and once Start is pressed, will forward all bytes
 * to the given host and port. All traffic is displayed in a
 * UI.
 */
public class JTcpTunnel extends JFrame
{
    private int         listenPort_;
    private String      tunnelHost_;
    private int         tunnelPort_;
    private JTextArea   listenText_;
    private JTextArea   tunnelText_;
    private JLabel      status_;
    private Relay       inRelay_;
    private Relay       outRelay_;
    private JSplitPane  splitter_;
    private JCheckBox   followCheckBox_;
    private JButton     clearButton_;
    
    /**
     * Entry point
     * 
     * @args    args   Args
     */
    public static void main(String[] args) throws IOException
    {
        if (args.length != 3)
        {
            System.err.println("Usage: java " + JTcpTunnel.class.getName() + 
                               " listenport tunnelhost tunnelport");
            System.exit(1);
        }

        // Parse arguments
        int    listenPort = Integer.parseInt(args[0]);
        String tunnelHost = args[1];
        int    tunnelPort = Integer.parseInt(args[2]);
        
        // Start the GUI
        JTcpTunnel gui = new JTcpTunnel(listenPort, tunnelHost, tunnelPort);
        gui.setVisible(true);
    }


    /**
     * Creates a JTCPTunnel with the given parameters
     * 
     * @param listenPort  Port to listen on
     * @param tunnelHost  Host to tunnel to
     * @param tunnelPort  Port to tunnel to
     */
    public JTcpTunnel(int listenPort, String tunnelHost, int tunnelPort)
    {
        listenPort_ = listenPort;
        tunnelHost_ = tunnelHost;
        tunnelPort_ = tunnelPort;

        // Build the GUI
        buildView();
        
        // Start the server
        Thread server = new Thread(new TunnelRunner());
        server.start();
    }

    /**
     * Tunnel thread
     */
    class TunnelRunner implements Runnable
    {
        /**
         * Creates server socket and reads
         */
        public void run()
        {
            ServerSocket ss = null;
            JLabel status = getStatus();

            try
            {
                ss = new ServerSocket(getListenPort());
            }
            catch (IOException ioe)
            {
                JSmartOptionPane.showExceptionMessageDialog(
                    JTcpTunnel.this, ioe);
                    
                System.exit(1);
            }

            while (true)
            {
                try
                {
                    status.setText("Listening for connections on port " + 
                        getListenPort());

                    // accept the connection from my client
                    Socket sc = ss.accept();

                    // connect to the thing I'm tunnelling for
                    Socket st = new Socket(getTunnelHost(), getTunnelPort());
                    
                    status.setText("Tunnelling port "+ getListenPort()+ 
                                   " to port " + getTunnelPort() + 
                                   " on host " + getTunnelHost() + 
                                   " ...");

                    // relay the stuff thru
                    new Relay(sc.getInputStream(), st.getOutputStream(), 
                        getListenText()).start();
                              
                    new Relay(st.getInputStream(), sc.getOutputStream(), 
                        getTunnelText()).start();

                    // that's it .. they're off
                }
                catch (Exception e)
                {
                    JSmartOptionPane.showExceptionMessageDialog(
                        JTcpTunnel.this, e);
                }
            }
        }
    }


    /**
     * Builds the GUI 
     */
    protected void buildView()
    {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // show info
        setTitle("TCP Tunnel/Monitor: Tunneling localhost:" + listenPort_ + 
            " to " + tunnelHost_ + ":" + tunnelPort_);

        JPanel p;
        
        // labels
        p = new JPanel();
        p.setLayout(new BorderLayout());

        JLabel l1;
        JLabel l2;
        
        p.add(BorderLayout.WEST, l1 = 
            new JLabel("From localhost:" + listenPort_, JLabel.CENTER));
            
        p.add(BorderLayout.EAST, l2 = 
            new JLabel("From " + tunnelHost_ + ":" + 
                tunnelPort_, JLabel.CENTER));
            
        getContentPane().add(BorderLayout.NORTH, p);

        // the monitor part
        
        listenText_ = new JTextArea();
        listenText_.setFont(SwingUtil.getPreferredMonoFont());
        listenText_.setRows(40);
        listenText_.setColumns(80);

        
        tunnelText_ = new JTextArea();
        tunnelText_.setFont(SwingUtil.getPreferredMonoFont());
        tunnelText_.setRows(40);
        tunnelText_.setColumns(80);
        tunnelText_.setAutoscrolls(true);
        
        splitter_ = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
            new JScrollPane(listenText_), new JScrollPane(tunnelText_));
            
        splitter_.setDividerLocation(0.5);
        
        getContentPane().add(BorderLayout.CENTER, splitter_);

        // clear and status
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p = new JPanel();

        clearButton_ = new JButton("Clear");
        followCheckBox_ = new JCheckBox("Follow output", true);
        p.add(clearButton_);
        p.add(followCheckBox_);
        
        clearButton_.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                listenText_.setText("");
                tunnelText_.setText("");
            }
        });

        
        followCheckBox_.addChangeListener(new ChangeListener()
        {
            /**
             * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
             */
            public void stateChanged(ChangeEvent e)
            {
                listenText_.setAutoscrolls(followCheckBox_.isSelected());           
                tunnelText_.setAutoscrolls(followCheckBox_.isSelected());
            }
        });
        
        p2.add(BorderLayout.CENTER, p);
        p2.add(BorderLayout.SOUTH, status_ = new JLabel());
        getContentPane().add(BorderLayout.SOUTH, p2);
        pack();
        SwingUtil.centerWindow(this);

        // Keep divider location in the middle if the window is resized
        addComponentListener(new ComponentAdapter()
        {
            /**
             * Windows has been resized
             */
            public void componentResized(ComponentEvent e)
            {
                splitter_.setDividerLocation(0.5);
            }
        });
        
        // Quick death
        addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowListener#windowClosed(WindowEvent)
             */
            public void windowClosed(WindowEvent e)
            {
                System.exit(0);           
            }
        });
    }

    public int getListenPort()
    {
        return listenPort_;
    }

    public JTextArea getListenText()
    {
        return listenText_;
    }

    public JLabel getStatus()
    {
        return status_;
    }

    public String getTunnelHost()
    {
        return tunnelHost_;
    }

    public int getTunnelPort()
    {
        return tunnelPort_;
    }

    public JTextArea getTunnelText()
    {
        return tunnelText_;
    }
}