package toolbox.tunnel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.dump.Dumper;
import toolbox.util.ui.JFlipPane;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * JTcpTunnel tunnels TCP traffic between a port on the localhost and a port
 * on a remote host. All bytes sent/received are displayed in the GUI for
 * visual inspection.
 */
public class JTcpTunnel extends JFrame
{
    private static final Logger logger_ = 
        Logger.getLogger(JTcpTunnel.class);
    
    private int         listenPort_;
    private String      tunnelHost_;
    private int         tunnelPort_;
    private JTextArea   listenText_;
    private JTextArea   tunnelText_;
    private JLabel      status_;
    private Relay       inRelay_;
    private Relay       outRelay_;
    private JSplitPane  splitter_;
    private JButton     clearButton_;

    private JTextField  localPortField_;
    private JTextField  remoteHostField_;
    private JTextField  remortPortField_;
    
    /**
     * Entry point
     * 
     * @param   args   [listen port, tunnel host, tunnel port]
     * @throws  Exception on error
     */
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();
        
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

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public JTcpTunnel()
    {
        buildView();
    }
    
    /**
     * Creates a JTCPTunnel with the given parameters
     * 
     * @param  listenPort  Port to listen on
     * @param  tunnelHost  Host to tunnel to
     * @param  tunnelPort  Port to tunnel to
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

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @return  Port to listen
     */
    public int getListenPort()
    {
        return listenPort_;
    }


    /**
     * @return  Listen text area
     */
    public JTextArea getListenText()
    {
        return listenText_;
    }


    /**
     * @return  Host to forward traffic to
     */
    public String getTunnelHost()
    {
        return tunnelHost_;
    }


    /**
     * @return  Port to forward traffic to
     */
    public int getTunnelPort()
    {
        return tunnelPort_;
    }


    /**
     * @return Tunnel text area
     */
    public JTextArea getTunnelText()
    {
        return tunnelText_;
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI 
     */
    protected void buildView()
    {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("[Tunnel] localhost:" + listenPort_ +  " ==> " + 
            tunnelHost_ + ":" + tunnelPort_);
        

        //===================== CENTER =========================================
    
        JPanel outputPane = new JPanel(new BorderLayout());
        
            //===================== NORTH ======================================

            JPanel labelPanel = new JPanel(new BorderLayout());
    
            JLabel localLabel = new JLabel("From localhost:" + listenPort_,
                JLabel.CENTER);
    
            JLabel remoteLabel = new JLabel("From " + tunnelHost_ + ":" +
                tunnelPort_, JLabel.CENTER);
    
            labelPanel.add(BorderLayout.WEST, localLabel);
            labelPanel.add(BorderLayout.EAST, remoteLabel);
    
            outputPane.add(BorderLayout.NORTH, labelPanel);
        
            //===================== CENTER =====================================
            
            listenText_ = new JSmartTextArea(true, false);
            listenText_.setFont(SwingUtil.getPreferredMonoFont());
            //listenText_.setRows(40);
            listenText_.setColumns(80);
            
            tunnelText_ = new JSmartTextArea(true, false);
            tunnelText_.setFont(SwingUtil.getPreferredMonoFont());
            //tunnelText_.setRows(40);
            tunnelText_.setColumns(80); 
            
            splitter_ = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                new JScrollPane(listenText_), new JScrollPane(tunnelText_));
                
            //splitter_.setDividerLocation(0.5);
        
            outputPane.add(BorderLayout.CENTER, splitter_);
              
        getContentPane().add(BorderLayout.CENTER, outputPane);

        //===================== SOUTH ==========================================
        
        // clear and status
        JPanel actionPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        clearButton_ = new JButton("Clear");
        buttonPanel.add(clearButton_);
        
        // Clear both textareas
        clearButton_.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                listenText_.setText("");
                tunnelText_.setText("");
            }
        });
        
        actionPanel.add(BorderLayout.CENTER, buttonPanel);
        actionPanel.add(BorderLayout.SOUTH, status_ = new JLabel());
        getContentPane().add(BorderLayout.SOUTH, actionPanel);
        
        //===================== WEST ==========================================
        
        JPanel configPanel = new JPanel(new ParagraphLayout());
        
        configPanel.add(
            new JLabel("Local Tunnel Port"), ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(localPortField_ = new JTextField(10));
        configPanel.add(
            new JLabel("Remote Host"), ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(remoteHostField_ = new JTextField(10));
        configPanel.add(
            new JLabel("Remote Port"), ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(remortPortField_ = new JTextField(10));      
        configPanel.add(
            new JButton(new StartTunnelAction()),ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(new JButton(new StopTunnelAction()));  
        
        JFlipPane configFlipPane = new JFlipPane(JFlipPane.LEFT);
        configFlipPane.addFlipper("Config", configPanel);
        configFlipPane.setExpanded(false);
        
        getContentPane().add(BorderLayout.WEST, configFlipPane);
        
        //======================================================================
        
        pack();
        SwingUtil.centerWindow(this);

        //======================================================================
        
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
             * Window close event
             */
            public void windowClosed(WindowEvent e)
            {
                System.exit(0);           
            }
        });
        
        
        //splitter_.setDividerLocation(0.5f);
        //splitter_.resetToPreferredSizes();
        splitter_.setContinuousLayout(true);
        JComponent c = splitter_;
        int i = (int)(c.getSize().getWidth()/2);
        logger_.info("Divider: " + i);
        logger_.info("Scrolly:" + c);
        logger_.info("Scrolly2:\n " + Dumper.dump(c));                
        splitter_.setDividerLocation(i);
        
    }
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------

    /**
     * Starts the tunnel
     */    
    class StartTunnelAction extends AbstractAction
    {
        public StartTunnelAction()
        {
            super("Start");
        }
        
        public void actionPerformed(ActionEvent e)
        {
        }
    }

    /**
     * Stops the tunnel
     */
    class StopTunnelAction extends AbstractAction
    {
        public StopTunnelAction()
        {
            super("Stop");
        }
        
        public void actionPerformed(ActionEvent e)
        {
        }
    }
    
    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
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
                    status_.setText("Listening for connections on port " + 
                        getListenPort());

                    // accept the connection from my client
                    Socket sc = ss.accept();

                    // connect to the thing I'm tunnelling for
                    Socket st = new Socket(getTunnelHost(), getTunnelPort());
                    
                    status_.setText("Tunnelling port "+ getListenPort()+ 
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
}
