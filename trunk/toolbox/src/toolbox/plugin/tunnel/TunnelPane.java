package toolbox.plugin.tunnel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.tunnel.TcpTunnel;
import toolbox.tunnel.TcpTunnelListener;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.layout.ParagraphLayout;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.IStatusBar;

/**
 * Panel which houses the majority of the UI controls. 
 */
public class TunnelPane extends JPanel implements IPreferenced
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    private static final Logger logger_ = Logger.getLogger(TunnelPane.class);
    
    // Preferences
    private static final String NODE_TCPTUNNEL_PLUGIN = "TCPTunnelPlugin";
    private static final String   ATTR_REMOTE_PORT    = "remoteport";
    private static final String   ATTR_REMOTE_HOST    = "remotehost";
    private static final String   ATTR_LOCAL_PORT     = "localport";
    private static final String   NODE_INCOMING       = "Incoming";
    private static final String   NODE_OUTGOING       = "Outgoing";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Textarea that incoming data from the tunnel is dumped to. 
     */
    private JSmartTextArea incomingTextArea_;
    
    /** 
     * Textarea that outgoing data to the tunnel is dumped to. 
     */ 
    private JSmartTextArea outgoingTextArea_;
    
    /** 
     * Refernce to the workspace status bar.
     */
    private IStatusBar statusBar_;
    
    /** 
     * Splits the input and output textareas. 
     */
    private JSmartSplitPane splitter_;
    
    /** 
     * Status label for the incoming text area. 
     */
    private JLabel remoteLabel_;
    
    /** 
     * Status label for the outgoing text area. 
     */
    private JLabel localLabel_;

    /**
     * Flip pane that contains the configuration information.
     */
    private JFlipPane configFlipPane_;

    /** 
     * Field for the port number of the local host. 
     */
    private JTextField listenPortField_;
    
    /** 
     * Field for the remote hostname.
     */
    private JTextField remoteHostField_;
    
    /** 
     * Field for the remote port number. 
     */
    private JTextField remotePortField_;
    
    /**
     * Field that captures the max capacity of the incoming/outgoing textareas.
     */
    private JTextField capacityField_;
    
    /** 
     * Non-UI Tunnel component.
     */
    private TcpTunnel tunnel_;    

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TunnelPane.
     */
    public TunnelPane()
    {
        buildView();
    }
        
    
    /**
     * Creates a TunnelPane with the given parameters.
     * 
     * @param listenPort Port to listen on
     * @param remoteHost Host to tunnel to
     * @param remotePort Port to tunnel to
     */
    public TunnelPane(int listenPort, String remoteHost, int remotePort)
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
     * Returns the text area for incoming data.
     * 
     * @return JTextArea
     */
    public JTextArea getIncomingTextArea()
    {
        return incomingTextArea_;
    }

    
    /**
     * Returns host to forward traffic to.
     * 
     * @return String
     */
    public String getRemoteHost()
    {
        return remoteHostField_.getText().trim();
    }

    
    /**
     * Returns port to forward traffic to.
     * 
     * @return int
     */
    public int getRemotePort()
    {
        return Integer.parseInt(remotePortField_.getText().trim());
    }

    
    /**
     * Returns the text area for incoming data.
     * 
     * @return JTextArea
     */
    public JTextArea getOutgoingTextArea()
    {
        return outgoingTextArea_;
    }

    
    /**
     * Sets the status bar.
     *
     * @param statusBar Statusbar
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }

    //--------------------------------------------------------------------------
    // Protected
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
                new JSmartLabel("Sent to Remote Host", JLabel.CENTER);
    
            remoteLabel_ = 
                new JSmartLabel("Received from Remote Host", JLabel.CENTER);
    
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
                new JSmartSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                    new JScrollPane(incomingTextArea_), 
                    new JScrollPane(outgoingTextArea_));
                
            splitter_.setContinuousLayout(true);        
            outputPane.add(BorderLayout.CENTER, splitter_);
              
        add(BorderLayout.CENTER, outputPane);

        // South
        JPanel actionPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        buttonPanel.add(new JSmartButton(new StartTunnelAction()));
        buttonPanel.add(new JSmartButton(new StopTunnelAction()));
        buttonPanel.add(new JSmartButton(new ClearAction()));
        
        actionPanel.add(BorderLayout.CENTER, buttonPanel);
        add(BorderLayout.SOUTH, actionPanel);
        
        // West
        JPanel configPanel = new JPanel(new ParagraphLayout());
        
        configPanel.add(new JSmartLabel("Local Tunnel Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(listenPortField_ = new JSmartTextField(10));
        
        configPanel.add(new JSmartLabel("Remote Host"), 
            ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(remoteHostField_ = new JSmartTextField(10));
        
        configPanel.add(new JSmartLabel("Remote Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(remotePortField_ = new JSmartTextField(10));      
        
        configPanel.add(new JSmartLabel("Max Capacity"),
            ParagraphLayout.NEW_PARAGRAPH);
        configPanel.add(capacityField_ = new JSmartTextField(10));
                        
        configFlipPane_ = new JFlipPane(JFlipPane.LEFT);
        configFlipPane_.addFlipper("Config", configPanel);
        configFlipPane_.setExpanded(false);
        
        add(BorderLayout.WEST, configFlipPane_);
        
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
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_TCPTUNNEL_PLUGIN, new Element(NODE_TCPTUNNEL_PLUGIN));
        
        remotePortField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_REMOTE_PORT, ""));

        remoteHostField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_REMOTE_HOST, ""));
            
        listenPortField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_LOCAL_PORT, ""));
    
        configFlipPane_.applyPrefs(root);
        splitter_.applyPrefs(root);
    
        incomingTextArea_.applyPrefs(XOMUtil.getFirstChildElement(
            root, NODE_INCOMING, new Element(NODE_INCOMING)));

        capacityField_.setText(incomingTextArea_.getCapacity()+"");
            
        outgoingTextArea_.applyPrefs(XOMUtil.getFirstChildElement(
            root, NODE_OUTGOING, new Element(NODE_OUTGOING)));
            
        incomingTextArea_.setPruneFactor(50);
        outgoingTextArea_.setPruneFactor(50);
    }

    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
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

        configFlipPane_.savePrefs(root);
        splitter_.savePrefs(root);
            
        Element incoming = new Element(NODE_INCOMING);
        incomingTextArea_.setCapacity(
            Integer.parseInt(capacityField_.getText()));
        incomingTextArea_.savePrefs(incoming);
        root.appendChild(incoming);

        Element outgoing = new Element(NODE_OUTGOING);
        outgoingTextArea_.setCapacity(
            Integer.parseInt(capacityField_.getText()));
        outgoingTextArea_.savePrefs(outgoing);
        root.appendChild(outgoing);
        
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // ClearAction
    //--------------------------------------------------------------------------

    /**
     * Clears the contents of the input and output text areas
     */
    class ClearAction extends AbstractAction
    {
        ClearAction()
        {
            super("Clear");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            incomingTextArea_.setText("");
            outgoingTextArea_.setText("");
        }
    }

    //--------------------------------------------------------------------------
    // StartTunnelAction
    //--------------------------------------------------------------------------

    /**
     * Starts the tunnel.
     */    
    class StartTunnelAction extends SmartAction implements TcpTunnelListener
    {
        StartTunnelAction()
        {
            super("Start", true, false, null);
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

        public void runAction(ActionEvent e) throws Exception
        {
            if (StringUtil.isNullOrEmpty(getRemoteHost()))
                throw new IllegalArgumentException(
                    "Please specify the remote hostname");
                    
            tunnel_ = 
                new TcpTunnel(
                    getListenPort(), 
                    getRemoteHost(), 
                    getRemotePort());
                    
            tunnel_.setIncomingSink(new BufferedOutputStream(
                new JTextAreaOutputStream(outgoingTextArea_)));
            
            tunnel_.setOutgoingSink(new BufferedOutputStream(
                new JTextAreaOutputStream(incomingTextArea_)));
                
            tunnel_.addTcpTunnelListener(this);
            
            new Thread(new Runnable()
            {
                public void run()
                {
                    tunnel_.start();
                }
            }).start();    
        }
    }

    //--------------------------------------------------------------------------
    // StopTunnelAction
    //--------------------------------------------------------------------------
    
    /**
     * Stops the tunnel.
     */
    class StopTunnelAction extends AbstractAction
    {
        StopTunnelAction()
        {
            super("Stop");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            tunnel_.stop();
        }
    }
}