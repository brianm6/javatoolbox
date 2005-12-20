package toolbox.plugin.tunnel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import nu.xom.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import toolbox.forms.SmartComponentFactory;
import toolbox.tunnel.TcpTunnel;
import toolbox.tunnel.TcpTunnelListener;
import toolbox.util.FontUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.DetachableOutputStream;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.layout.VerticalLayout;
import toolbox.util.ui.textarea.action.AutoTailAction;
import toolbox.util.ui.textarea.action.LineWrapAction;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PreferencedException;

/**
 * User interface for TCPTunnel.
 * 
 * @see toolbox.tunnel.TcpTunnel
 */
public class TunnelPane extends JPanel implements IPreferenced {
    
    private static final Logger logger_ = Logger.getLogger(TunnelPane.class);

    // -------------------------------------------------------------------------
    // IPreferenced Constants
    // -------------------------------------------------------------------------

    public static final String NODE_TCPTUNNEL_PLUGIN = "TCPTunnelPlugin";
    public static final String   NODE_INCOMING       =   "Incoming";
    public static final String   NODE_OUTGOING       =   "Outgoing";
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * Textarea that incoming data from the tunnel is dumped to.
     */
    private JSmartTextArea incomingArea_;

    /**
     * Textarea that outgoing data to the tunnel is dumped to.
     */
    private JSmartTextArea outgoingArea_;

    /**
     * Reference to the workspace status bar.
     */
    private IStatusBar statusBar_;

    /**
     * Splits the input and output textareas.
     */
    private JSmartSplitPane splitter_;

    /**
     * Header panel for the incoming text area. Needed to updated the title.
     */
    private JHeaderPanel incomingHeader_;

    /**
     * Header panel for the outgoing text area. Needed to update the title.
     */
    private JHeaderPanel outgoingHeader_;

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

    private JComponent trackerView_;

    private DetachableOutputStream outgoingDetachable;
    private DetachableOutputStream incomingDetachable;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a TunnelPane.
     */
    public TunnelPane() {
        tunnel_ = new TcpTunnel();
        buildView();
    }


    /**
     * Creates a TunnelPane with the given parameters.
     * 
     * @param listenPort Port to listen on.
     * @param remoteHost Host to tunnel to.
     * @param remotePort Port to tunnel to.
     */
    public TunnelPane(int listenPort, String remoteHost, int remotePort) {
        buildView();
        setListenPort(listenPort);
        setRemoteHost(remoteHost);
        setRemotePort(remotePort);
        tunnel_ = new TcpTunnel(listenPort, remoteHost, remotePort);
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------

    /**
     * Returns the local listen port number.
     * 
     * @return int
     */
    public int getListenPort() {
        return Integer.parseInt(listenPortField_.getText().trim());
    }


    /**
     * Sets the value of the listen port.
     * 
     * @param listenPort Listen port.
     */
    public void setListenPort(int listenPort) {
        listenPortField_.setText(listenPort + "");
    }
    

    /**
     * Returns the text area for incoming data.
     * 
     * @return JTextArea
     */
    public JTextArea getIncomingTextArea() {
        return incomingArea_;
    }


    /**
     * Returns host to forward traffic to.
     * 
     * @return String
     */
    public String getRemoteHost() {
        return remoteHostField_.getText().trim();
    }

    
    /**
     * Sets the value of the remote host.
     * 
     * @param remoteHost Remote host.
     */
    public void setRemoteHost(String remoteHost) {
        remoteHostField_.setText(remoteHost);
    }
    
    
    /**
     * Returns port to forward traffic to.
     * 
     * @return int
     */
    public int getRemotePort() {
        return Integer.parseInt(remotePortField_.getText().trim());
    }


    /**
     * Sets the value of the remote port.
     * 
     * @param remotePort Remote port.
     */
    public void setRemotePort(int remotePort) {
        remotePortField_.setText(remotePort + "");
    }

    
    /**
     * Returns the text area for incoming data.
     * 
     * @return JTextArea
     */
    public JTextArea getOutgoingTextArea() {
        return outgoingArea_;
    }


    /**
     * Sets the status bar.
     * 
     * @param statusBar Statusbar
     */
    public void setStatusBar(IStatusBar statusBar) {
        statusBar_ = statusBar;
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView() {
        setLayout(new BorderLayout());

        // Center
        JPanel outputPane = new JPanel(new BorderLayout());

            // Center
            incomingArea_ = new JSmartTextArea(true, false);
            incomingArea_.setFont(FontUtil.getPreferredMonoFont());
            incomingArea_.setRows(40);
            incomingArea_.setColumns(80);

            outgoingArea_ = new JSmartTextArea(true, false);
            outgoingArea_.setFont(FontUtil.getPreferredMonoFont());
            outgoingArea_.setRows(40);
            outgoingArea_.setColumns(80);

            splitter_ =
                new JSmartSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT,
                    true,
                    
                    incomingHeader_ = new JHeaderPanel(
                        "Sent to Remote Host",
                        createHeaderToolBar(incomingArea_),
                        new JScrollPane(incomingArea_)),
                        
                    outgoingHeader_ = new JHeaderPanel(
                        "Received from Remote Host",
                        createHeaderToolBar(outgoingArea_),
                        new JScrollPane(outgoingArea_)));

            splitter_.setContinuousLayout(true);
            outputPane.add(BorderLayout.CENTER, splitter_);

        add(BorderLayout.CENTER, outputPane);

        // South
        JPanel actionPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        SmartAction startAction = new StartTunnelAction();
        
        buttonPanel.add(new JSmartButton(startAction));
        buttonPanel.add(new JSmartButton(new StopTunnelAction()));
        buttonPanel.add(new JSmartButton(new ClearAction()));
        buttonPanel.add(new JSmartButton(new DetachAction()));

        actionPanel.add(BorderLayout.CENTER, buttonPanel);
        add(BorderLayout.SOUTH, actionPanel);

        configFlipPane_ = new JFlipPane(JFlipPane.LEFT);

        configFlipPane_.addFlipper(
            ImageCache.getIcon(ImageCache.IMAGE_CONFIG),
            "Config",
            
            new JHeaderPanel(
                ImageCache.getIcon(ImageCache.IMAGE_CONFIG),
                "Config",
                null,
                buildConfigView()));

        configFlipPane_.addFlipper(
            ImageCache.getIcon(ImageCache.IMAGE_BAR_CHART),
            "Tracker",
            
            new JHeaderPanel(
                ImageCache.getIcon(ImageCache.IMAGE_BAR_CHART),
                "Tracker",
                null,
                new JScrollPane(trackerView_ = buildTrackerView())));

        configFlipPane_.setExpanded(false);

        add(BorderLayout.WEST, configFlipPane_);

        // Done

        // Keep divider location in the middle if the window is resized
        addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                splitter_.setDividerLocation(0.5f);
            }
        });
    }


    /**
     * Configuration view for the tcp tunnel.
     * 
     * @return JPanel
     */
    protected JPanel buildConfigView() {
        FormLayout layout = new FormLayout("r:p:n, p, f:p:g", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(SmartComponentFactory.getInstance());
        builder.setDefaultDialogBorder();

        listenPortField_ = new JSmartTextField(10);
        builder.append("Local Tunnel Port", listenPortField_);
        builder.nextLine();
        
        remoteHostField_ = new JSmartTextField(10);
        builder.append("Remote Host", remoteHostField_);
        builder.nextLine();

        remotePortField_ = new JSmartTextField(10);
        builder.append("Remote Port", remotePortField_);
        builder.nextLine();

        capacityField_ = new JSmartTextField(10);
        builder.append("Max Capacity", capacityField_);
        builder.nextLine();

        return builder.getPanel();
    }
    
    protected JComponent buildTrackerView() {
        JPanel p = new JPanel(
            new VerticalLayout(0, VerticalLayout.BOTH, VerticalLayout.TOP));
        
        p.add(new JLabel("TrackerView"));
        return p;
    }

    
    /**
     * Creates the common toolbar that is used in the input and output text
     * areas.
     *
     * @param area Textarea.
     * @return JToolBar
     */
    protected JToolBar createHeaderToolBar(JSmartTextArea area) {
        JToolBar tb = JHeaderPanel.createToolBar();

        tb.add(JHeaderPanel.createToggleButton(
            new SupressBinaryAction(),
            area,
            TcpTunnel.PROP_SUPPRESS_BINARY));
        
        tb.add(JHeaderPanel.createToggleButton(
            new LineWrapAction(area),
            area,
            JSmartTextArea.PROP_LINEWRAP));

        tb.add(JHeaderPanel.createToggleButton(
            new AutoTailAction(area),
            area,
            JSmartTextArea.PROP_AUTOTAIL));

        tb.add(JHeaderPanel.createButton(
            new toolbox.util.ui.textarea.action.ClearAction(area)));

        return tb;
    }

    // -------------------------------------------------------------------------
    // IPreferenced Interface
    // -------------------------------------------------------------------------

    /*
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException {
        Element root =
            XOMUtil.getFirstChildElement(
                prefs,
                NODE_TCPTUNNEL_PLUGIN,
                new Element(NODE_TCPTUNNEL_PLUGIN));

        tunnel_.applyPrefs(root);
        
        setListenPort(tunnel_.getLocalPort());
        setRemoteHost(tunnel_.getRemoteHost());
        setRemotePort(tunnel_.getRemotePort());

        configFlipPane_.applyPrefs(root);
        splitter_.applyPrefs(root);

        incomingArea_.applyPrefs(XOMUtil.getFirstChildElement(
            root, NODE_INCOMING, new Element(NODE_INCOMING)));

        capacityField_.setText(incomingArea_.getCapacity() + "");

        outgoingArea_.applyPrefs(XOMUtil.getFirstChildElement(
            root, NODE_OUTGOING, new Element(NODE_OUTGOING)));

        incomingArea_.setPruningFactor(50);
        outgoingArea_.setPruningFactor(50);
    }


    /*
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException {
        Element root = new Element(NODE_TCPTUNNEL_PLUGIN);
        tunnel_.savePrefs(root);
        configFlipPane_.savePrefs(root);
        splitter_.savePrefs(root);
        
        Element incoming = new Element(NODE_INCOMING);
        incomingArea_.setCapacity(Integer.parseInt(capacityField_.getText()));
        incomingArea_.savePrefs(incoming);
        root.appendChild(incoming);

        Element outgoing = new Element(NODE_OUTGOING);
        outgoingArea_.setCapacity(Integer.parseInt(capacityField_.getText()));
        outgoingArea_.savePrefs(outgoing);
        root.appendChild(outgoing);

        XOMUtil.insertOrReplace(prefs, root);
    }

    // -------------------------------------------------------------------------
    // ClearAction
    // -------------------------------------------------------------------------

    /**
     * Clears the contents of the input and output text areas.
     */
    class ClearAction extends AbstractAction {
        
        ClearAction() {
            super("Clear", ImageCache.getIcon(ImageCache.IMAGE_CLEAR));
            putValue(SHORT_DESCRIPTION, "Clears both textareas");
        }


        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            incomingArea_.setText("");
            outgoingArea_.setText("");
        }
    }

    class DetachAction extends AbstractAction {

        public DetachAction() {
            super("Detach" /*, ImageCache.getIcon(ImageCache.IMAGE_STOP) */);
        }
        
        public void actionPerformed(ActionEvent e) {
            putValue(NAME, incomingDetachable.isDetached() ? "Detach" : "Attach");
            incomingDetachable.setDetached(!incomingDetachable.isDetached());
            outgoingDetachable.setDetached(!outgoingDetachable.isDetached());
        }
    }
    

    // -------------------------------------------------------------------------
    // StartTunnelAction
    // -------------------------------------------------------------------------

    /**
     * Starts the tunnel.
     */
    class StartTunnelAction extends SmartAction implements TcpTunnelListener {

        // ---------------------------------------------------------------------
        // Constructors
        // ---------------------------------------------------------------------

        /**
         * Creates a StartTunnelAction.
         */
        StartTunnelAction() {
            super("Start", true, false, null);
            putValue(SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_PLAY));
            putValue(SHORT_DESCRIPTION, "Starts the tunnel");
        }

        // ---------------------------------------------------------------------
        // TcpTunnelListener Interface
        // ---------------------------------------------------------------------

        /*
         * @see toolbox.tunnel.TcpTunnelListener#statusChanged(toolbox.tunnel.TcpTunnel, java.lang.String)
         */
        public void statusChanged(TcpTunnel tunnel, String status) {
            logger_.debug("tunnel status changed: " + status);
            statusBar_.setInfo(status);
        }


        /*
         * @see toolbox.tunnel.TcpTunnelListener#bytesRead(toolbox.tunnel.TcpTunnel, int, int)
         */
        public void bytesRead(
            TcpTunnel tunnel,
            int connBytesRead,
            int totalBytesRead) {
            incomingHeader_.setTitle("Sent to Remote Host: "
                + connBytesRead
                + " conn  "
                + totalBytesRead
                + " total");
        }


        /*
         * @see toolbox.tunnel.TcpTunnelListener#bytesWritten(toolbox.tunnel.TcpTunnel, int, int)
         */
        public void bytesWritten(
            TcpTunnel tunnel,
            int connBytesWritten,
            int totalBytesWritten) {
            outgoingHeader_.setTitle("Received from Remote Host: "
                + connBytesWritten
                + " conn  "
                + totalBytesWritten
                + " total");
        }


        /*
         * @see toolbox.tunnel.TcpTunnelListener#tunnelStarted(toolbox.tunnel.TcpTunnel)
         */
        public void tunnelStarted(TcpTunnel tunnel) {
            logger_.debug("tunnel started");
            statusBar_.setInfo("Tunnel started");
        }

        
        /*
         * @see toolbox.tunnel.TcpTunnelListener#newConnection(toolbox.util.io.MonitoredOutputStream, toolbox.util.io.MonitoredOutputStream)
         */
        public void newConnection(
            MonitoredOutputStream incomingSink, 
            MonitoredOutputStream outgoingSink) {
            
            logger_.debug("newConnection!");
            
            ConnectionTrackerView tracker = 
                new ConnectionTrackerView(incomingSink, outgoingSink);
            
            trackerView_.add(tracker);
        }
        
        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            if (StringUtils.isBlank(getRemoteHost()))
                throw new IllegalArgumentException(
                    "Please specify the remote hostname");

            tunnel_.setLocalPort(getListenPort());
            tunnel_.setRemoteHost(getRemoteHost());
            tunnel_.setRemotePort(getRemotePort());

            incomingDetachable = new DetachableOutputStream(
                new JTextAreaOutputStream(outgoingArea_), false);
            
            tunnel_.setIncomingSink(new BufferedOutputStream(incomingDetachable));
                 
            outgoingDetachable = new DetachableOutputStream(
                new JTextAreaOutputStream(incomingArea_), false);

            tunnel_.setOutgoingSink(new BufferedOutputStream(outgoingDetachable));

            tunnel_.addTcpTunnelListener(StartTunnelAction.this);
            tunnel_.start();
        }
    }

    // -------------------------------------------------------------------------
    // StopTunnelAction
    // -------------------------------------------------------------------------

    /**
     * Stops the tunnel.
     */
    class StopTunnelAction extends AbstractAction {

        /**
         * Creates a StopTunnelAction.
         */
        StopTunnelAction() {
            super("Stop", ImageCache.getIcon(ImageCache.IMAGE_STOP));
            putValue(SHORT_DESCRIPTION, "Stops the tunnel");
        }


        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            tunnel_.stop();
        }
    }

    // -------------------------------------------------------------------------
    // SupressBinaryAction
    // -------------------------------------------------------------------------

    /**
     * Toggles suppressing of binary data from showing up in the output.
     */
    public class SupressBinaryAction extends AbstractAction {

        public SupressBinaryAction() {
            super(null, ImageCache.getIcon(ImageCache.IMAGE_FUNNEL));
            putValue(SHORT_DESCRIPTION, "Supresses Binary Data");
        }


        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            tunnel_.setSuppressBinary(!tunnel_.isSuppressBinary());
        }
    }
}