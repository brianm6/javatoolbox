package toolbox.util.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import toolbox.util.SwingUtil;
import toolbox.workspace.IStatusBar;

/**
 * JStatusBar is a simple component to show a text message as status.
 */
public class JStatusBar extends JPanel implements IStatusBar
{
    // TODO: Replace with toolbox.util.ui.statusbar.JStatusBar
    
    private JSmartLabel statusLabel_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default construtor
     */
    public JStatusBar()
    {
        buildView();
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Builds the view
     */
    protected void buildView()
    {
        statusLabel_ = new JSmartLabel("", SwingConstants.LEFT);
        Font f = SwingUtil.getPreferredSerifFont();
        
        // TODO: remove once getPreferredSerifFont() gets size from LAF
        f = f.deriveFont( (float) (f.getSize() - 1));
        
        statusLabel_.setFont(f);
        FontMetrics fm = statusLabel_.getFontMetrics(f);
        Dimension d = new Dimension(100, (int)(fm.getHeight() * 1.1));
        statusLabel_.setPreferredSize(d);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));        
        add(BorderLayout.NORTH, statusLabel_);
    }

    //--------------------------------------------------------------------------
    // IstatusBar Interface
    //--------------------------------------------------------------------------

    /**
     * Sets the status
     *
     * @param  status  Status text
     */
    public void setStatus(String status)
    {
        setStatus(-1, status);
    }    
    
    /**
     * @return Current status
     */
    public String getStatus()
    {
        return statusLabel_.getText();
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setBusy(boolean)
     */    
    public void setBusy(boolean busy)
    {
        //setStatus(IStatusBar.BUSY, status);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setError(java.lang.String)
     */
    public void setError(String status)
    {
        setStatus(IStatusBar.ERROR, status);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setInfo(java.lang.String)
     */
    public void setInfo(String status)
    {
        setStatus(IStatusBar.INFO, status);
    }

    /**
     * @see toolbox.util.ui.plugin.IStatusBar#setWarning(java.lang.String)
     */
    public void setWarning(String status)
    {
        setStatus(IStatusBar.WARNING, status);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    public void setStatus(int state, String status)
    {
        switch (state)
        {
            case -1 : break;
            case IStatusBar.BUSY   : statusLabel_.setIcon(ImageCache.getIcon("")); break;
            case IStatusBar.ERROR  : statusLabel_.setIcon(ImageCache.getIcon("")); break;
            case IStatusBar.INFO   : statusLabel_.setIcon(ImageCache.getIcon("")); break;
            case IStatusBar.WARNING: statusLabel_.setIcon(ImageCache.getIcon("")); break;
            
            default: throw new IllegalArgumentException("Invalid status bar state: " + state);
        }
        
        statusLabel_.setText(status);
    }
}