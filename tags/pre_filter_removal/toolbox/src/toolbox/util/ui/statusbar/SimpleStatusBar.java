package toolbox.util.ui.statusbar;

import toolbox.util.ui.JSmartLabel;

/**
 * A pre-built no-frills status bar with the expected get/setStatus() methods.
 * 
 * @see JStatusBar
 */
public class SimpleStatusBar extends JStatusBar
{
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /** 
     * Label for displaying status text. 
     */
    private JSmartLabel status_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SimpleStatusBar.
     */
    public SimpleStatusBar()
    {
        buildView();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the status text.
     * 
     * @param status Status text.
     */
    public void setStatus(String status)
    {
        status_.setText(status);
    }
    
    
    /**
     * Returns the status text.
     * 
     * @return String
     */
    public String getStatus()
    {
        return status_.getText();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        status_ = new JSmartLabel();
        addStatusComponent(status_, RELATIVE, 1);
    }
}