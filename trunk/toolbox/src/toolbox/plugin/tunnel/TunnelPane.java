package toolbox.tunnel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.ResourceCloser;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.io.MulticastOutputStream;
import toolbox.util.ui.JFlipPane;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.layout.ParagraphLayout;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * JTcpTunnel tunnels TCP traffic between a port on the localhost and a port
 * on a remote host. All bytes sent/received are displayed in the GUI for
 * visual inspection.
 */
public class JTcpTunnelPane extends JPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(JTcpTunnelPane.class);
    
    private int         listenPort_;
    private String      tunnelHost_;
    private int         tunnelPort_;
    private JTextArea   listenText_;
    private JTextArea   tunnelText_;
    private IStatusBar  statusBar_;
    private JSplitPane  splitter_;
    private JButton     clearButton_;

    private JTextField  localPortField_;
    private JTextField  remoteHostField_;
    private JTextField  remotePortField_;
    
	private Thread server_;    
    private TunnelRunner tunnelRunner_;

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
     * @param  listenPort  Port to listen on
     * @param  tunnelHost  Host to tunnel to
     * @param  tunnelPort  Port to tunnel to
     */
    public JTcpTunnelPane(int listenPort, String tunnelHost, int tunnelPort)
    {
        listenPort_ = listenPort;
        tunnelHost_ = tunnelHost;
        tunnelPort_ = tunnelPort;

        buildView();
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

    /**
     * Sets the status bar
     *
     * @param statusBar  Statusbar
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
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
        add(BorderLayout.SOUTH, actionPanel);
        
        //===================== WEST ==========================================
        
        JPanel configPanel = new JPanel(new ParagraphLayout());
        
        configPanel.add(new JLabel("Local Tunnel Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(localPortField_ = new JTextField(10));
        configPanel.add(new JLabel("Remote Host"), 
            ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(remoteHostField_ = new JTextField(10));
        configPanel.add(new JLabel("Remote Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
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
    
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
        if (!StringUtil.isNullOrEmpty(localPortField_.getText()))
            prefs.setProperty("tcptunnel.listenport",localPortField_.getText());
            
        if (!StringUtil.isNullOrEmpty(remotePortField_.getText()))    
            prefs.setProperty("tcptunnel.remoteport", remotePortField_.getText());
        
        if (!StringUtil.isNullOrEmpty(remoteHostField_.getText()))    
            prefs.setProperty("tcptunnel.remotehost", remoteHostField_.getText());
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        remotePortField_.setText(prefs.getProperty("tcptunnel.remoteport",""));
        remoteHostField_.setText(prefs.getProperty("tcptunnel.remotehost",""));
        localPortField_.setText(prefs.getProperty("tcptunnel.listenport",""));
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
                listenPort_ = 
                    Integer.parseInt(localPortField_.getText().trim());
                    
                tunnelHost_ = remoteHostField_.getText().trim();
                
                tunnelPort_ = 
                    Integer.parseInt(remotePortField_.getText().trim());
                
                if (StringUtil.isNullOrEmpty(tunnelHost_))
                    throw new IllegalArgumentException(
                        "Please specify the tunnel hostname");
                        
                // Start the server
                tunnelRunner_ = new TunnelRunner();
                server_ = new Thread(tunnelRunner_);
                server_.start();
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
            tunnelRunner_.stop();
            
        	ThreadUtil.stop(server_, 5000);
        	
        	if (server_ != null)
        	{
        		if (server_.isAlive())
        			statusBar_.setStatus("Could not kill server.");
        		else
                    statusBar_.setStatus("Server may have stopped.");
        	}
        	else
                statusBar_.setStatus("Server stopped.");
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
        ServerSocket serverSocket_;
        boolean stop_ = false;
        
        public void stop()
        {
            stop_ = true;
            ResourceCloser.close(serverSocket_);    
        }
        
        /**
         * Creates server socket and reads
         */
        public void run()
        {
            serverSocket_ = null;

            try
            {
                serverSocket_ = new ServerSocket(getListenPort());
                serverSocket_.setSoTimeout(2000);
            }
            catch (IOException ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }

            while (!stop_)
            {
                try
                {
                    statusBar_.setStatus("Listening for connections on port " + 
                        getListenPort());

                    // accept the connection from my client
                    if (!serverSocket_.isClosed())
                    {
                        Socket sc = serverSocket_.accept();
    
                        // connect to the thing I'm tunnelling for
                        Socket st = new Socket(getTunnelHost(),getTunnelPort());
                        
                        statusBar_.setStatus(
                            "Tunnelling port "+ getListenPort()+ 
                            " to port " + getTunnelPort() + 
                            " on host " + getTunnelHost() + " ...");
    
                        // relay the stuff thru. Make multicast output streams
                        // that send to the socket and also to the textarea
                        // for each direction
                            
                        MulticastOutputStream tos = new MulticastOutputStream();
                        tos.addStream(st.getOutputStream());
                        
                        tos.addStream(
                            new JTextAreaOutputStream(getListenText()));
                        
                        MulticastOutputStream sos = new MulticastOutputStream();
                        sos.addStream(sc.getOutputStream());
                        
                        sos.addStream(
                            new JTextAreaOutputStream(getTunnelText()));
                        
//                        new Relay(sc.getInputStream(), tos).start();
//                        new Relay(st.getInputStream(), sos).start();

                        new Thread(
                            new Relay(
                                new BufferedInputStream(sc.getInputStream()), 
                                new BufferedOutputStream(tos))).start();
                            
                        new Thread(
                            new Relay(
                                new BufferedInputStream(st.getInputStream()), 
                                new BufferedOutputStream(sos))).start();

                        // that's it .. they're off
                    }
                }
                catch (SocketTimeoutException ste)
                {
                    logger_.warn("run", ste);
                }
                catch (Exception e)
                {
                    ExceptionUtil.handleUI(e, logger_);
                }
            }
        }
    }
}