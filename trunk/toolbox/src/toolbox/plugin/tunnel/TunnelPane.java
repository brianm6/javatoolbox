package toolbox.plugin.tunnel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.tunnel.TcpTunnel;
import toolbox.tunnel.TcpTunnelListener;
import toolbox.util.FontUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.layout.ParagraphLayout;
import toolbox.util.ui.textarea.action.AutoScrollAction;
import toolbox.util.ui.textarea.action.LineWrapAction;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.IStatusBar;

/**
 * User interface for TCPTunnel.
 * 
 * @see toolbox.tunnel.TcpTunnel
 */
public class TunnelPane extends JPanel implements IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(TunnelPane.class);

    //--------------------------------------------------------------------------
    // XML Constants
    //--------------------------------------------------------------------------

    private static final String NODE_TCPTUNNEL_PLUGIN = "TCPTunnelPlugin";
    private static final String   ATTR_REMOTE_PORT    =   "remoteport";
    private static final String   ATTR_REMOTE_HOST    =   "remotehost";
    private static final String   ATTR_LOCAL_PORT     =   "localport";
    private static final String   NODE_INCOMING       =   "Incoming";
    private static final String   NODE_OUTGOING       =   "Outgoing";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Textarea that incoming data from the tunnel is dumped to.
     */
    private JSmartTextArea incomingArea_;

    /**
     * Textarea that outgoing data to the tunnel is dumped to.
     */
    private JSmartTextArea outgoingArea_;

    /**
     * Refernce to the workspace status bar.
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
     * @param listenPort Port to listen on.
     * @param remoteHost Host to tunnel to.
     * @param remotePort Port to tunnel to.
     */
    public TunnelPane(int listenPort, String remoteHost, int remotePort)
    {
        buildView();

        listenPortField_.setText(listenPort + "");
        remoteHostField_.setText(remoteHost);
        remotePortField_.setText(remotePort + "");
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
        return incomingArea_;
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
        return outgoingArea_;
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
     * Constructs the user interface.
     */
    protected void buildView()
    {
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

        configFlipPane_.addFlipper(
            ImageCache.getIcon(ImageCache.IMAGE_CONFIG),
            "Config",
            new JHeaderPanel(
                ImageCache.getIcon(ImageCache.IMAGE_CONFIG),
                "Config",
                null,
                configPanel));

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


    /**
     * Creates the common toolbar that is used in the input and output text
     * areas.
     *
     * @param area Textarea.
     * @return JToolBar
     */
    protected JToolBar createHeaderToolBar(JSmartTextArea area)
    {
        JToolBar tb = JHeaderPanel.createToolBar();

        tb.add(JHeaderPanel.createToggleButton(
            ImageCache.getIcon(ImageCache.IMAGE_LINEWRAP),
            "Wrap Lines",
            new LineWrapAction(area),
            area,
            "lineWrap"));

        tb.add(JHeaderPanel.createToggleButton(
            ImageCache.getIcon(ImageCache.IMAGE_LOCK),
            "Scroll Lock",
            new AutoScrollAction(area),
            area,
            "autoscroll"));

        tb.add(JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_CLEAR),
            "Clear",
            new ClearAction()));

        // TODO: link to property change
        tb.add(JHeaderPanel.createToggleButton(
            ImageCache.getIcon(ImageCache.IMAGE_FUNNEL),
            "Supress Binary Data",
            new SupressBinaryAction()));

        return tb;
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root =
            XOMUtil.getFirstChildElement(
                prefs,
                NODE_TCPTUNNEL_PLUGIN,
                new Element(NODE_TCPTUNNEL_PLUGIN));

        remotePortField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_REMOTE_PORT, ""));

        remoteHostField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_REMOTE_HOST, ""));

        listenPortField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_LOCAL_PORT, ""));

        configFlipPane_.applyPrefs(root);
        splitter_.applyPrefs(root);

        incomingArea_.applyPrefs(XOMUtil.getFirstChildElement(
            root, NODE_INCOMING, new Element(NODE_INCOMING)));

        capacityField_.setText(incomingArea_.getCapacity() + "");

        outgoingArea_.applyPrefs(XOMUtil.getFirstChildElement(
            root, NODE_OUTGOING, new Element(NODE_OUTGOING)));

        incomingArea_.setPruneFactor(50);
        outgoingArea_.setPruneFactor(50);
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
        incomingArea_.setCapacity(
            Integer.parseInt(capacityField_.getText()));
        incomingArea_.savePrefs(incoming);
        root.appendChild(incoming);

        Element outgoing = new Element(NODE_OUTGOING);
        outgoingArea_.setCapacity(
            Integer.parseInt(capacityField_.getText()));
        outgoingArea_.savePrefs(outgoing);
        root.appendChild(outgoing);

        XOMUtil.insertOrReplace(prefs, root);
    }

    //--------------------------------------------------------------------------
    // ClearAction
    //--------------------------------------------------------------------------

    /**
     * Clears the contents of the input and output text areas.
     */
    class ClearAction extends AbstractAction
    {
        /**
         * Creates a ClearAction.
         */
        ClearAction()
        {
            super("Clear", ImageCache.getIcon(ImageCache.IMAGE_CLEAR));
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            incomingArea_.setText("");
            outgoingArea_.setText("");
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
        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------

        /**
         * Creates a StartTunnelAction.
         */
        StartTunnelAction()
        {
            super("Start", true, false, null);

            putValue(AbstractAction.SMALL_ICON,
                ImageCache.getIcon(ImageCache.IMAGE_PLAY));
        }

        //----------------------------------------------------------------------
        // TcpTunnelListener Interface
        //----------------------------------------------------------------------

        /**
         * @see toolbox.tunnel.TcpTunnelListener#statusChanged(
         *      toolbox.tunnel.TcpTunnel, java.lang.String)
         */
        public void statusChanged(TcpTunnel tunnel, String status)
        {
            statusBar_.setInfo(status);
        }


        /**
         * @see toolbox.tunnel.TcpTunnelListener#bytesRead(
         *      toolbox.tunnel.TcpTunnel, int, int)
         */
        public void bytesRead(
            TcpTunnel tunnel,
            int connBytesRead,
            int totalBytesRead)
        {
            incomingHeader_.setTitle(
                "Sent to Remote Host: " + connBytesRead + " conn  " +
                totalBytesRead + " total");
        }


        /**
         * @see toolbox.tunnel.TcpTunnelListener#bytesWritten(
         *      toolbox.tunnel.TcpTunnel, int, int)
         */
        public void bytesWritten(
            TcpTunnel tunnel,
            int connBytesWritten,
            int totalBytesWritten)
        {
            outgoingHeader_.setTitle(
                "Received from Remote Host: " + connBytesWritten + " conn  " +
                totalBytesWritten + " total");
        }


        /**
         * @see toolbox.tunnel.TcpTunnelListener#tunnelStarted(
         *      toolbox.tunnel.TcpTunnel)
         */
        public void tunnelStarted(TcpTunnel tunnel)
        {
            statusBar_.setInfo("Tunnel started");
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            if (StringUtils.isEmpty(getRemoteHost()))
                throw new IllegalArgumentException(
                    "Please specify the remote hostname");

            tunnel_ =
                new TcpTunnel(
                    getListenPort(),
                    getRemoteHost(),
                    getRemotePort());

            tunnel_.setIncomingSink(new BufferedOutputStream(//System.out));
                new JTextAreaOutputStream(outgoingArea_), 20480));

            tunnel_.setOutgoingSink(new BufferedOutputStream(//System.err));
                new JTextAreaOutputStream(incomingArea_), 20480));

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
        /**
         * Creates a StopTunnelAction.
         */
        StopTunnelAction()
        {
            super("Stop", ImageCache.getIcon(ImageCache.IMAGE_STOP));
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            tunnel_.stop();
        }
    }

    //--------------------------------------------------------------------------
    // SupressBinaryAction
    //--------------------------------------------------------------------------

    /**
     * Toggles suppressing of binary data from showing up in the output.
     */
    public class SupressBinaryAction extends AbstractAction
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            tunnel_.setSupressBinary(!tunnel_.isSupressBinary());
        }
    }
}