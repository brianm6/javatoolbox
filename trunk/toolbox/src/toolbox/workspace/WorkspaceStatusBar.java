package toolbox.workspace;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JMemoryMonitor;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.action.DisposeAction;
import toolbox.util.ui.list.JSmartList;
import toolbox.util.ui.statusbar.JStatusBar;

/**
 * <code>WorkspaceStatusBar</code> is an extension of
 * {@link toolbox.util.ui.statusbar.JStatusBar} with commonly used
 * pre-assembled components. This includes:
 * <ul>
 *   <li>Area to display arbitrary status text.
 *   <li>Progress bar for long running operations.
 *   <li>Memory usage bar.
 *   <li>Quick-click icon to trigger garbage collection.
 *   <li>Right click pops up a window with status history.
 * </ul>
 * The status text can optionally have an associated priority which is denoted
 * by an icon. The priorities are:
 * <ul>
 *   <li>INFO  - Informational status message
 *   <li>WARN  - Warning status message.
 *   <li>ERROR - Error status message.
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 *   WorkspaceStatusBar sb = new WorkspaceStatusBar();
 *   sb.setInfo("Logging in ..");
 *   sb.setBusy(true);
 *   // go login
 *   sb.setBusy(false);
 *   sb.setInfo("Login succeeded.");
 * </pre>
 */
public class WorkspaceStatusBar extends JStatusBar implements IStatusBar
{
    private static final Logger logger_ =
        Logger.getLogger(WorkspaceStatusBar.class);

    //--------------------------------------------------------------------------
    // Defaults
    //--------------------------------------------------------------------------

    /**
     * Default number of status history to retain.
     */
    private static final int MAX_HISTORY = 100;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * An indeterminate progress bar that is activated when the busy flag is
     * set.
     */
    private JProgressBar progressBar_;

    /**
     * Label for displaying status message.
     */
    private JSmartLabel status_;

    /**
     * Keeps a running history of the past x number of status messages.
     */
    private CircularFifoBuffer history_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a WorkspaceStatusBar.
     */
    public WorkspaceStatusBar()
    {
        history_ = new CircularFifoBuffer(MAX_HISTORY);
        buildView();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        progressBar_ = new JProgressBar();

        status_ = new JSmartLabel("Howdy pardner!");
        status_.addMouseListener(new StatusMouseListener());

        JLabel gc = new JSmartLabel(
            ImageCache.getIcon(ImageCache.IMAGE_TRASHCAN));

        gc.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                setInfo("Garbage collecting...");
                System.gc();
                setInfo("Garbage collecting...done.");
            }
        });

        addStatusComponent(status_, RELATIVE, 1);
        addStatusComponent(new JMemoryMonitor(), FIXED, 100);
        addStatusComponent(gc);
        addStatusComponent(progressBar_, FIXED, 100);

        // Repaint interval.
        UIManager.put("ProgressBar.repaintInterval", new Integer(100));

        // Cycle time.
        UIManager.put("ProgressBar.cycleTime", new Integer(1500));

        //UIManager.put("ProgressBar.cellLength", new Integer(75));
        //UIManager.put("ProgressBar.cellSpacing", new Integer(5));
    }


    /**
     * Sets the status text and icon.
     *
     * @param status Status text.
     * @param icon Icon that visually classifies the status.
     */
    protected void setStatus(String status, Icon icon)
    {
        //setStatus(status);
        status_.setText(status);
        status_.setIcon(icon);
        history_.add(status);
    }

    //--------------------------------------------------------------------------
    // IStatusBar Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IStatusBar#setStatus(java.lang.String)
     */
    public void setStatus(String status)
    {
        //status_.setText(status);
        setStatus(status, ImageCache.getIcon(ImageCache.IMAGE_INFO));
    }


    /**
     * @see toolbox.workspace.IStatusBar#setBusy(boolean)
     */
    public void setBusy(boolean busy)
    {
        progressBar_.setIndeterminate(busy);
    }


    /**
     * @see toolbox.workspace.IStatusBar#setError(java.lang.String)
     */
    public void setError(String status)
    {
        setStatus(status);
    }


    /**
     * @see toolbox.workspace.IStatusBar#setInfo(java.lang.String)
     */
    public void setInfo(String status)
    {
        setStatus(status, ImageCache.getIcon(ImageCache.IMAGE_INFO));
    }


    /**
     * @see toolbox.workspace.IStatusBar#setWarning(java.lang.String)
     */
    public void setWarning(String status)
    {
        setStatus(status, ImageCache.getIcon(ImageCache.IMAGE_WARNING));
    }


    /**
     * @see toolbox.workspace.IStatusBar#getStatus()
     */
    public String getStatus()
    {
        return status_.getText();
    }

    //--------------------------------------------------------------------------
    // StatusMouseListener
    //--------------------------------------------------------------------------

    /**
     * StatusMouseListener is responsible for activating the popup window with
     * status history on a RMB click on the status bar.
     */
    class StatusMouseListener extends MouseAdapter
    {
        /**
         * @see java.awt.event.MouseListener#mousePressed(
         *      java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e)
        {
            maybeShowPopup(e);
        }


        /**
         * @see java.awt.event.MouseListener#mouseReleased(
         *      java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e)
        {
            maybeShowPopup(e);
        }

        //----------------------------------------------------------------------
        //  Private
        //----------------------------------------------------------------------

        /**
         * Determines if the popupmenu should be made visible.
         *
         * @param e Mouse event.
         */
        private void maybeShowPopup(MouseEvent e)
        {
            if (e.isPopupTrigger())
            {
                Window w = new StatusHistoryWindow(
                    SwingUtil.getFrameAncestor(WorkspaceStatusBar.this));

                w.setVisible(true);
            }
        }
    }

    //--------------------------------------------------------------------------
    // StatusHistoryWindow
    //--------------------------------------------------------------------------

    /**
     * StatusHistoryWindow is responsible for displaying the last x number of
     * status messages in a listbox that hover directly above the status bar
     * in a Window.
     */
    public class StatusHistoryWindow extends Window
    {
        /**
         * Creates a StatusHistoryWindow.
         *
         * @param frame Parent frame.
         */
        public StatusHistoryWindow(Frame frame)
        {
            super(frame);

            int fh = frame.getHeight();
            int h = fh / 3;
            Point fl = frame.getLocationOnScreen();
            setSize(status_.getWidth(), h);
            Point s = new Point(fl.x, fl.y + fh - h - status_.getHeight());
            setLocation(s);
            setLayout(new BorderLayout());

            JHeaderPanel hp = new JHeaderPanel("Status History");
            JToolBar tb = JHeaderPanel.createToolBar();

            AbstractAction aa = new DisposeAction(this);

            aa.putValue(
                AbstractAction.SMALL_ICON,
                ImageCache.getIcon(ImageCache.IMAGE_CROSS));

            tb.add(JHeaderPanel.createButton(aa));

            hp.setToolBar(tb);

            JSmartList list = new JSmartList(history_.toArray());
            hp.setContent(new JScrollPane(list));

            list.scrollToBottom();

            add(hp, BorderLayout.CENTER);
        }
    }
}