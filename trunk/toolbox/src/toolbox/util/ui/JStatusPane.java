package toolbox.util.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import toolbox.util.SwingUtil;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * JStatusPane is a simple component to show a text message as status.
 * 
 * <pre>
 * TODO: Rename to JStatusBar
 * TODO: Add RMB accessible status history
 * TODO: Add knight right like busy meter
 * TODO: Add log4j like prioity with visual cues (TBD)
 * </pre>
 */
public class JStatusPane extends JPanel implements IStatusBar
{
    private JLabel statusLabel_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default construtor
     */
    public JStatusPane()
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
        statusLabel_ = new JLabel("", SwingConstants.LEFT);
        Font f = SwingUtil.getPreferredSerifFont();
        statusLabel_.setFont(f);
        FontMetrics fm = statusLabel_.getFontMetrics(f);
        Dimension d = new Dimension(100, (int)(fm.getHeight() * 1.1));
        statusLabel_.setPreferredSize(d);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));        
        add(BorderLayout.NORTH, statusLabel_);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets the status
     *
     * @param  status  Status text
     */
    public void setStatus(String status)
    {
        statusLabel_.setText(status);
    }    
    
    /**
     * @return Current status
     */
    public String getStatus()
    {
        return statusLabel_.getText();
    }
}