package toolbox.tunnel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.flippane.JFlipPane;
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
    
    private JTextArea   incomingTextArea_;
    private JTextArea   outgoingTextArea_;
    private IStatusBar  statusBar_;
    private JSplitPane  splitter_;
    private JButton     clearButton_;
    private JLabel      remoteLabel_;
    private JLabel      localLabel_;

    private JTextField  listenPortField_;
    private JTextField  remoteHostField_;
    private JTextField  remotePortField_;
    
    private TcpTunnel tunnel_;    

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
     * @param  listenPort   Port to listen on
     * @param  remoteHost   Host to tunnel to
     * @param  remotePort   Port to tunnel to
     */
    public JTcpTunnelPane(int listenPort, String remoteHost, int remotePort)
    {
        buildView();
                
        listenPortField_.setText(listenPort+"");
        remoteHostField_.setText(remoteHost);
        remotePortField_.setText(remotePort+"");
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Saves connection propreties to a Properties object
     * 
     * @param  prefs  Properties to save preferences to
     */
    public void savePrefs(Properties prefs)
    {
        if (!StringUtil.isNullOrEmpty(listenPortField_.getText()))
            prefs.setProperty(
                "tcptunnel.listenport",listenPortField_.getText());
            
        if (!StringUtil.isNullOrEmpty(remotePortField_.getText()))    
            prefs.setProperty(
                "tcptunnel.remoteport", remotePortField_.getText());
        
        if (!StringUtil.isNullOrEmpty(remoteHostField_.getText()))    
            prefs.setProperty(
                "tcptunnel.remotehost", remoteHostField_.getText());
    }

    /**
     * Applies the preferences
     * 
     * @param  prefs  Properties to read the preferences from
     */
    public void applyPrefs(Properties prefs)
    {
        remotePortField_.setText(prefs.getProperty("tcptunnel.remoteport",""));
        remoteHostField_.setText(prefs.getProperty("tcptunnel.remotehost",""));
        listenPortField_.setText(prefs.getProperty("tcptunnel.listenport",""));
    }
    
    /**
     * @return  Port to listen
     */
    public int getListenPort()
    {
        return Integer.parseInt(listenPortField_.getText().trim());
    }

    /**
     * @return  Text area for incoming data
     */
    public JTextArea getIncomingTextArea()
    {
        return incomingTextArea_;
    }

    /**
     * @return  Host to forward traffic to
     */
    public String getRemoteHost()
    {
        return remoteHostField_.getText().trim();
    }

    /**
     * @return  Port to forward traffic to
     */
    public int getRemotePort()
    {
        return Integer.parseInt(remotePortField_.getText().trim());
    }

    /**
     * @return Outgoing data text area
     */
    public JTextArea getOutgoingTextArea()
    {
        return outgoingTextArea_;
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
        
        // Center
        JPanel outputPane = new JPanel(new BorderLayout());
        
            // North
            JPanel labelPanel = new JPanel(new GridLayout(1,2));
    
            localLabel_ = 
                new JLabel("Sent to Remote Host", JLabel.CENTER);
    
            remoteLabel_ = 
                new JLabel("Received from Remote Host", JLabel.CENTER);
    
            labelPanel.add(localLabel_);
            labelPanel.add(remoteLabel_);
    
            outputPane.add(BorderLayout.NORTH, labelPanel);
        
            // Center
            incomingTextArea_ = new JSmartTextArea(true, false);
            incomingTextArea_.setFont(SwingUtil.getPreferredMonoFont());
            incomingTextArea_.setRows(40);
            incomingTextArea_.setColumns(80);
            
            outgoingTextArea_ = new JSmartTextArea(true, false);
            outgoingTextArea_.setFont(SwingUtil.getPreferredMonoFont());
            outgoingTextArea_.setRows(40);
            outgoingTextArea_.setColumns(80); 
            
            splitter_ = 
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                    new JScrollPane(incomingTextArea_), 
                    new JScrollPane(outgoingTextArea_));
                
            splitter_.setContinuousLayout(true);        
            outputPane.add(BorderLayout.CENTER, splitter_);
              
        add(BorderLayout.CENTER, outputPane);

        // South
        JPanel actionPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        buttonPanel.add(new JButton(new StartTunnelAction()));
        buttonPanel.add(new JButton(new StopTunnelAction()));
        buttonPanel.add(clearButton_ = new JButton(new ClearAction()));
        
        actionPanel.add(BorderLayout.CENTER, buttonPanel);
        add(BorderLayout.SOUTH, actionPanel);
        
        // West
        JPanel configPanel = new JPanel(new ParagraphLayout());
        
        configPanel.add(new JLabel("Local Tunnel Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
            
        configPanel.add(listenPortField_ = new JTextField(10));
        
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
        
        // Done
        
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
            incomingTextArea_.setText("");
            outgoingTextArea_.setText("");
        }
    }
    
    /**
     * Starts the tunnel
     */    
    class StartTunnelAction extends AbstractAction implements TcpTunnelListener
    {
        public StartTunnelAction()
        {
            super("Start");
        }
        
        public void statusChanged(TcpTunnel tunnel, String status)
        {
            statusBar_.setStatus(status);
        }

        public void bytesRead(TcpTunnel tunnel, int connBytesRead, 
            int totalBytesRead)
        {
            localLabel_.setText("Sent to Remote Host: " + connBytesRead +
                " conn  " + totalBytesRead + " total");
        }

        public void bytesWritten(TcpTunnel tunnel, int connBytesWritten,
            int totalBytesWritten)
        {
            remoteLabel_.setText("Received from Remote Host: " + 
                connBytesWritten + " conn  " + totalBytesWritten + " total");
        }

        public void actionPerformed(ActionEvent e)
        {
            try
            {
                if (StringUtil.isNullOrEmpty(getRemoteHost()))
                    throw new IllegalArgumentException(
                        "Please specify the remote hostname");
                        
                tunnel_ = 
                    new TcpTunnel(
                        getListenPort(), 
                        getRemoteHost(), 
                        getRemotePort());
                        
                tunnel_.addIncomingStream(
                    new JTextAreaOutputStream(outgoingTextArea_));
                
                tunnel_.addOutgoingStream(
                    new JTextAreaOutputStream(incomingTextArea_));
                    
                tunnel_.addTcpTunnelListener(this);
                
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        tunnel_.start();
                    }
                }).start();    
                
            } 
            catch (Exception ex)
            {
                ExceptionUtil.handleUI(ex, logger_);
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
            tunnel_.stop();
        }
    }
}