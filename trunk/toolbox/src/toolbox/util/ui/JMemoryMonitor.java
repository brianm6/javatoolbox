package toolbox.util.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

/**
 * Simple memory monitor component lifted from JEdit
 */
public class JMemoryMonitor extends JComponent
{
    private static final String TEST_STRING = "999/999Mb";
    
    private LineMetrics lm_;
    private Color progressForeground_;
    private Color progressBackground_;
    private Timer timer_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Creates a JMemoryMonitor
     */
    public JMemoryMonitor()
    {
        setDoubleBuffered(true);
        setForeground(UIManager.getColor("Label.foreground"));
        setBackground(UIManager.getColor("Label.background"));
        setFont(UIManager.getFont("Label.font"));
        
        FontRenderContext frc = new FontRenderContext(null,false,false);
        lm_ = UIManager.getFont("Label.font").getLineMetrics(TEST_STRING, frc);
        progressForeground_ = UIManager.getColor("Label.background");
        progressBackground_ = UIManager.getColor("Button.darkShadow");
    }

    //--------------------------------------------------------------------------
    // Overrides javax.swing.JComponent
    //--------------------------------------------------------------------------
    
    public void addNotify()
    {
        super.addNotify();
        timer_ = new Timer(2000, new RefreshAction());
        timer_.start();
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    public void removeNotify()
    {
        timer_.stop();
        ToolTipManager.sharedInstance().unregisterComponent(this);
        super.removeNotify();
    }
    
    public String getToolTipText()
    {
        Runtime runtime = Runtime.getRuntime();
        int freeMemory  = (int)(runtime.freeMemory() / 1024);
        int totalMemory = (int)(runtime.totalMemory() / 1024);
        int usedMemory  = (totalMemory - freeMemory);
        return usedMemory + "/" + totalMemory + "MB";
    }

    public Point getToolTipLocation(MouseEvent event)
    {
        return new Point(event.getX(), -20);
    }

    public void paintComponent(Graphics g)
    {
        // TODO: Move so its aware of switching LAFs
        setFont(UIManager.getFont("Label.font"));
        
        Insets insets = new Insets(0,0,0,0);
        //MemoryStatus.this.getBorder().getBorderInsets(this);

        Runtime runtime = Runtime.getRuntime();
        int freeMemory = (int)(runtime.freeMemory() / 1024);
        int totalMemory = (int)(runtime.totalMemory() / 1024);
        int usedMemory = (totalMemory - freeMemory);

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom - 1;

        float fraction = ((float)usedMemory) / totalMemory;

        g.setColor(progressBackground_);

        g.fillRect(insets.left,
                   insets.top,
                   (int)(width * fraction),
                   height);

        String str = (usedMemory / 1024) + "/" + (totalMemory / 1024) + "Mb";
        FontRenderContext frc = new FontRenderContext(null,false,false);
        Rectangle2D bounds = g.getFont().getStringBounds(str,frc);
        Graphics g2 = g.create();
        
        g2.setClip(insets.left,
                   insets.top,
                   (int)(width * fraction),
                   height);

        g2.setColor(progressForeground_);

        g2.drawString(str,
                      insets.left + (int)(width - bounds.getWidth()) / 2,
                      (int)(insets.top + lm_.getAscent()));

        g2.dispose();
        g2 = g.create();

        g2.setClip(insets.left + (int)(width * fraction),
                   insets.top,
                   getWidth() - insets.left - (int)(width*fraction),
                   height);

        g2.setColor(getForeground());

        g2.drawString(str,
                      insets.left + (int)(width - bounds.getWidth()) / 2,
                     (int)(insets.top + lm_.getAscent()));

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