package toolbox.workspace;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JMemoryMonitor;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.statusbar.JStatusBar;

/**
 * Specialization of JStatusBar with pre-assembled components.
 * <br> 
 * This includes:
 * <ul>
 *   <li>Area to display arbitrary status text
 *   <li>Progress bar for long running operations
 *   <li>Memory usage bar
 *   <li>Quick-click icon to trigger garbage collection
 * </ul> 
 */
public class WorkspaceStatusBar extends JStatusBar implements IStatusBar
{
    /** 
     * Progress bar for indicating execution of an operation is in progress 
     */
    private JProgressBar progressBar_;
    
    /** 
     * Label for displaying status text 
     */
    private JSmartLabel status_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a WorkspaceStatusBar
     */
    public WorkspaceStatusBar()
    {
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        progressBar_ = new JProgressBar();
        status_ = new JSmartLabel("Howdy pardner!");

        JLabel gc = new JLabel(ImageCache.getIcon(ImageCache.IMAGE_TRASHCAN));
        
        gc.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                System.gc();
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
    
    //--------------------------------------------------------------------------
    // IStatusBar Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setStatus(java.lang.String)
     */
    public void setStatus(String status)
    {
        status_.setText(status);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setBusy(boolean)
     */
    public void setBusy(boolean busy)
    {
        progressBar_.setIndeterminate(busy);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setError(java.lang.String)
     */
    public void setError(String status)
    {
        setStatus(status);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setInfo(java.lang.String)
     */
    public void setInfo(String status)
    {
        setStatus(status);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setWarning(java.lang.String)
     */
    public void setWarning(String status)
    {
        setStatus(status);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#getStatus()
     */
    public String getStatus()
    {
        return status_.getText();
    }
}