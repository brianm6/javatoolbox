package toolbox.util.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;

/**
 * Simple memory monitor component lifted from JEdit with minor modifications
 */
public class JMemoryMonitor extends JComponent
{
    private static final Logger logger_ = 
        Logger.getLogger(JMemoryMonitor.class);
        
    private static final String TEST_STRING = "999/999Mb";
    
    private LineMetrics lineMetrics_;
    private Color progressForeground_;
    private Color progressBackground_;
    private Timer timer_;
    private Font labelFont_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Creates a JMemoryMonitor
     */
    public JMemoryMonitor()
    {
        // Reset the colors/fonts/etc whenever the look and feel changes
        UIManager.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                setDefaults();
            }
        });
        
        setDefaults();
    }

    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
    protected void setDefaults()
    {
        labelFont_ = UIManager.getFont("Label.font");
        
        setDoubleBuffered(true);
        setForeground(UIManager.getColor("Label.foreground"));
        setBackground(UIManager.getColor("Label.background"));
        setFont(labelFont_);
        
        FontRenderContext frc = new FontRenderContext(null,false,false);
        lineMetrics_ = labelFont_.getLineMetrics(TEST_STRING, frc);
        
        progressBackground_ = UIManager.getColor("ScrollBar.thumb");
        progressForeground_ = UIManager.getColor("ScrollBar.thumbHighlight");
    }

    //--------------------------------------------------------------------------
    // Overrides javax.swing.JComponent
    //--------------------------------------------------------------------------

    /**
     * @see java.awt.Component#addNotify()
     */
    public void addNotify()
    {
        super.addNotify();
        timer_ = new Timer(2000, new RefreshAction());
        timer_.start();
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /**
     * @see java.awt.Component#removeNotify()
     */
    public void removeNotify()
    {
        timer_.stop();
        ToolTipManager.sharedInstance().unregisterComponent(this);
        super.removeNotify();
    }
    
    /**
     * @see javax.swing.JComponent#getToolTipText()
     */
    public String getToolTipText()
    {
        Runtime runtime = Runtime.getRuntime();
        int freeMemory  = (int)(runtime.freeMemory() / 1024);
        int totalMemory = (int)(runtime.totalMemory() / 1024);
        int usedMemory  = (totalMemory - freeMemory);
        return usedMemory + "M of " + totalMemory + "M";
    }

    /**
     * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
     */
    public Point getToolTipLocation(MouseEvent event)
    {
        return new Point(event.getX(), -20);
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g)
    {
        SwingUtil.makeAntiAliased(g, true);
        Insets insets = new Insets(0,0,0,0);

        //
        // Calc memory stats
        //
        
        Runtime runtime = Runtime.getRuntime();
        int freeMemory = (int)(runtime.freeMemory() / 1024);
        int totalMemory = (int)(runtime.totalMemory() / 1024);
        int usedMemory = (totalMemory - freeMemory);

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom - 1;
        float fraction = ((float)usedMemory) / totalMemory;

        g.setColor(progressBackground_);

        //
        // Fill gradient
        //
        
        Graphics2D g2d = (Graphics2D) g;
        
        GradientPaint redtowhite =
            new GradientPaint(
                insets.left, 
                insets.top,
                progressBackground_,  
                width,
                insets.top,
                progressForeground_); 
                
        g2d.setPaint(redtowhite);
        
        g2d.fill(
            new RoundRectangle2D.Double(
                insets.left, 
                insets.top, 
                (int)(width * fraction), 
                height, 
                1, 
                1));

        //
        // Draw text
        //
        
        String str = (usedMemory / 1024) + "M of " + (totalMemory / 1024) + "M";
        FontRenderContext frc = new FontRenderContext(null,false,false);
        Rectangle2D bounds = g.getFont().getStringBounds(str,frc);
        Graphics g2 = g.create();
        
        g2.setClip(insets.left,
                   insets.top,
                   (int)(width * fraction),
                   height);

        g2.setColor(Color.black);

        g2.drawString(str,
                      insets.left + (int)(width - bounds.getWidth()) / 2,
                      (int)(insets.top + lineMetrics_.getAscent()));

        g2.dispose();
        g2 = g.create();

        g2.setClip(insets.left + (int)(width * fraction),
                   insets.top,
                   getWidth() - insets.left - (int)(width*fraction),
                   height);

        g2.setColor(getForeground());

        g2.drawString(str,
                      insets.left + (int)(width - bounds.getWidth()) / 2,
                     (int)(insets.top + lineMetrics_.getAscent()));

        g2.dispose();
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------

    /** 
     * Refreshes are triggered by the Timer
     */
    class RefreshAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent evt)
        {
            repaint();
        }
    }
}