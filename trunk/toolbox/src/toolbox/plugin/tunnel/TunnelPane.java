package toolbox.tunnel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.ExceptionUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.layout.ParagraphLayout;
import toolbox.util.ui.plugin.IPreferenced;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * Panel which houses the majority of the UI controls. 
 */
public class JTcpTunnelPane extends JPanel implements IPreferenced
{
    // TODO: Auto-clear text areas after # of bytes exceeded

    private static final Logger logger_ = 
        Logger.getLogger(JTcpTunnelPane.class);
    
    private static final String NODE_TCPTUNNEL_PLUGIN = "TCPTunnelPlugin";
    private static final String NODE_INCOMING = "Incoming";
    private static final String NODE_OUTGOING = "Outgoing";
    
    private static final String ATTR_REMOTE_PORT = "remoteport";
    private static final String ATTR_REMOTE_HOST = "remotehost";
    private static final String ATTR_LOCAL_PORT  = "localport";
    
    /** Textarea that incoming data from the tunnel is dumped to */
    private JSmartTextArea incomingTextArea_;
    
    /** Textarea that outgoing data to the tunnel is dumped to */ 
    private JSmartTextArea outgoingTextArea_;
    
    /** Workspace status bar */
    private IStatusBar statusBar_;
    
    /** Splits the input and output textareas */
    private JSplitPane splitter_;
    
    /** Clears the text areas */
    private JButton clearButton_;
    
    /** Status label for the incoming text area */
    private JLabel remoteLabel_;
    
    /** Status label for the outgoing text area */
    private JLabel localLabel_;

    /** Field for the port number of the local host */
    private JTextField listenPortField_;
    
    /** Field for the remote hostname */
    private JTextField remoteHostField_;
    
    /** Field for the remote port number */
    private JTextField remotePortField_;
    
    /** Tunnel object */
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
     * Returns the local listen port number.
     * 
     * @return int
     */
    public int getListenPort()
    {
        return Integer.parseInt(listenPortField_.getText().trim());
    }

    /**
     * Returns the text area for incoming data
     * 
     * @return JTextArea
     */
    public JTextArea getIncomingTextArea()
    {
        return incomingTextArea_;
    }

    /**
     * Returns host to forward traffic to
     * 
     * @return String
     */
    public String getRemoteHost()
    {
        return remoteHostField_.getText().trim();
    }

    /**
     * Returns port to forward traffic to
     * 
     * @return int
     */
    public int getRemotePort()
    {
        return Integer.parseInt(remotePortField_.getText().trim());
    }

    /**
     * Returns the text area for incoming data
     * 
     * @return JTextArea
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
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = prefs.getFirstChildElement(NODE_TCPTUNNEL_PLUGIN);
        
        remotePortField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_REMOTE_PORT, ""));

        remoteHostField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_REMOTE_HOST, ""));
            
        listenPortField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_LOCAL_PORT, ""));
    
        if (root != null)
        {
            incomingTextArea_.applyPrefs(
                root.getFirstChildElement(NODE_INCOMING));
                
            outgoingTextArea_.applyPrefs(
                root.getFirstChildElement(NODE_OUTGOING));
        }
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_TCPTUNNEL_PLUGIN);
        
        root.addAttribute(
            new Attribute(ATTR_LOCAL_PORT, listenPortField_.getText()));

        root.addAttribute(
            new Attribute(ATTR_REMOTE_PORT, remotePortField_.getText()));

        root.addAttribute(
            new Attribute(ATTR_REMOTE_HOST, remoteHostField_.getText()));
        
        Element incoming = new Element(NODE_INCOMING);
        incomingTextArea_.savePrefs(incoming);
        root.appendChild(incoming);

        Element outgoing = new Element(NODE_OUTGOING);
        outgoingTextArea_.savePrefs(outgoing);
        root.appendChild(outgoing);
        
        XOMUtil.insertOrReplace(prefs, root);
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
     * Clears the contents of the input and output text areas
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

        public void tunnelStarted(TcpTunnel tunnel)
        {
            statusBar_.setStatus("Tunnel started");
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
                        
                tunnel_.setIncomingSink(
                    new JTextAreaOutputStream(outgoingTextArea_));
                
                tunnel_.setOutgoingSink(
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