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
     * @see toolbox.workspace.IStatusBar#setStatus(java.lang.String)
     */
    public void setStatus(String status)
    {
        status_.setText(status);
    }
    
    
    /**
     * @see toolbox.workspace.IStatusBar#getStatus()
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