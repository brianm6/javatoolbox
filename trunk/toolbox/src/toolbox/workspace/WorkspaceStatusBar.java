package toolbox.util.ui.plugin;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import toolbox.util.ui.statusbar.JStatusBar;

/**
 * Specialization of the default JStatusBar with pre-assembled components.
 * This includes a status text, progress bar for long running operations, and
 * a display for memory usage statistics.
 */
public class WorkspaceStatusBar extends JStatusBar implements IStatusBar
{
    private JProgressBar progressBar_;
    private JLabel       status_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
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
        status_ = new JLabel("Howdy pardner!");
        
        addStatusComponent(status_, RELATIVE, 1);
        addStatusComponent(progressBar_, FIXED, 100);    
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
        progressBar_.setIndeterminate(false);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setBusy(java.lang.String)
     */
    public void setBusy(String status)
    {
        setStatus(status);
        
        // Repaint interval.
        UIManager.put("ProgressBar.repaintInterval", new Integer(25));
        
        // Cycle time.
        UIManager.put("ProgressBar.cycleTime", new Integer(1500));
        
        
        progressBar_.setIndeterminate(true);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setError(java.lang.String)
     */
    public void setError(String status)
    {
        setStatus(status);
        progressBar_.setIndeterminate(false);

    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setInfo(java.lang.String)
     */
    public void setInfo(String status)
    {
        setStatus(status);
        progressBar_.setIndeterminate(false);

    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setWarning(java.lang.String)
     */
    public void setWarning(String status)
    {
        setStatus(status);
        progressBar_.setIndeterminate(false);

    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#getStatus()
     */
    public String getStatus()
    {
        return status_.getText();
    }
    
    
}
