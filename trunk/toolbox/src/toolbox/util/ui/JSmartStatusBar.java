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

/**
 * JSmartStatusBar
 */
public class JSmartStatusBar extends JPanel
{
    private JLabel statusLabel_;

    
    /**
     * Constructor for JSmartStatusBar.
     */
    public JSmartStatusBar()
    {
        buildView();
    }


    /**
     * Builds the view
     */
    protected void buildView()
    {
        statusLabel_ = new JLabel("", SwingConstants.LEFT);
        Font f = SwingUtil.getPreferredSerifFont();
        statusLabel_.setFont(f);
        FontMetrics fm = statusLabel_.getFontMetrics(f);
        Dimension d = new Dimension(100, fm.getHeight());
        statusLabel_.setPreferredSize(d);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));        
        add(BorderLayout.NORTH, statusLabel_);
    }


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
     * Gets the status
     * 
     * @return  Status
     */
    public String getStatus()
    {
        return statusLabel_.getText();
    }
}
