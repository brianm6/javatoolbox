package toolbox.tunnel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JFlipPane;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * JTcpTunnel tunnels TCP traffic between a port on the localhost and a port
 * on a remote host. All bytes sent/received are displayed in the GUI for
 * visual inspection.
 * <p>
 * TODO: Add filtering
 */
public class JTcpTunnelPane extends JPanel
{
	public static final Logger logger_ = Logger.getLogger(JTcpTunnelPane.class);
    
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
    private JTextField  remotePortField_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public JTcpTunnelPane()
    {
        buildView();
    }
    
    /**
     * Creates a JTCPTunnel with the given parameters
     * 
     * @param listenPort  Port to listen on
     * @param tunnelHost  Host to tunnel to
     * @param tunnelPort  Port to tunnel to
     */
    public JTcpTunnelPane(int listenPort, String tunnelHost, int tunnelPort)
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
        setLayout(new BorderLayout());
        
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
            listenText_.setRows(40);
            listenText_.setColumns(80);
            
            tunnelText_ = new JSmartTextArea(true, false);
            tunnelText_.setFont(SwingUtil.getPreferredMonoFont());
            tunnelText_.setRows(40);
            tunnelText_.setColumns(80); 
            
            splitter_ = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                new JScrollPane(listenText_), new JScrollPane(tunnelText_));
                
            splitter_.setContinuousLayout(true);        
            outputPane.add(BorderLayout.CENTER, splitter_);
              
        add(BorderLayout.CENTER, outputPane);

        //===================== SOUTH ==========================================
        
        // clear and status
        JPanel actionPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        buttonPanel.add(new JButton(new StartTunnelAction()));
        buttonPanel.add(new JButton(new StopTunnelAction()));
        buttonPanel.add(clearButton_ = new JButton(new ClearAction()));
        
        actionPanel.add(BorderLayout.CENTER, buttonPanel);
        actionPanel.add(BorderLayout.SOUTH, status_ = new JLabel());
        add(BorderLayout.SOUTH, actionPanel);
        
        //===================== WEST ==========================================
        
        JPanel configPanel = new JPanel(new ParagraphLayout());
        
        configPanel.add(new JLabel("Local Tunnel Port"), ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(localPortField_ = new JTextField(10));
        configPanel.add(new JLabel("Remote Host"), ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(remoteHostField_ = new JTextField(10));
        configPanel.add(new JLabel("Remote Port"), ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(remotePortField_ = new JTextField(10));      
        
        JFlipPane configFlipPane = new JFlipPane(JFlipPane.LEFT);
        configFlipPane.addFlipper("Config", configPanel);
        configFlipPane.setExpanded(false);
        
        add(BorderLayout.WEST, configFlipPane);
        
        //======================================================================
        
        // Keep divider location in the middle if the window is resized
        addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent e)
            {
                splitter_.setDividerLocation(0.5f);
            }
        });
    }
    
    //--------------------------------------------------------------------------
	//  Actions
	//--------------------------------------------------------------------------

    /**
     * Clears the contents of the two output text areas
     */
    class ClearAction extends AbstractAction
    {
        public ClearAction()
        {
            super("Clear");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            listenText_.setText("");
            tunnelText_.setText("");
        }
    }
    
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
            try
            {
                listenPort_ = Integer.parseInt(localPortField_.getText().trim());
                tunnelHost_ = remoteHostField_.getText().trim();
                tunnelPort_ = Integer.parseInt(remotePortField_.getText().trim());
                
                if (StringUtil.isNullOrEmpty(tunnelHost_))
                    throw new IllegalArgumentException(
                        "Please specify the tunnel hostname");
            } 
            catch (Exception ex)
            {
                JSmartOptionPane.showExceptionMessageDialog(
                    JTcpTunnelPane.this, ex);
            }
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
                    JTcpTunnelPane.this, ioe);
                    
                //System.exit(1);
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
                        JTcpTunnelPane.this, e);
                }
            }
        }
    }
}
