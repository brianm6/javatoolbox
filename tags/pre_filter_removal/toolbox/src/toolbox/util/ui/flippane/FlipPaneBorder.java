package toolbox.util.ui.flippane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Custom border for JFlipPane that emulates the look of a JSplitPane divider.
 */
public class FlipPaneBorder implements Border
{
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Position of the flippane. BOTTOM|RIGHT|TOP|LEFT
     */
    private String position_;
    
    /**
     * Insets of the border.
     */
    private Insets insets_;
    
    /**
     * Border color 1.
     */
    private Color color1_;
    
    /**
     * Border color 2.
     */
    private Color color2_;
    
    /**
     * Border color 3.
     */
    private Color color3_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FlipPaneBorder for the given position.
     * 
     * @param position JFlipPane.[BOTTOM|RIGHT|TOP|LEFT]
     */
    FlipPaneBorder(String position)
    {
        position_ = position;
        insets_ = new Insets(
            position.equals(JFlipPane.BOTTOM) ? JFlipPane.SPLITTER_WIDTH : 0,
            position.equals(JFlipPane.RIGHT)  ? JFlipPane.SPLITTER_WIDTH : 0,
            position.equals(JFlipPane.TOP)    ? JFlipPane.SPLITTER_WIDTH : 0,
            position.equals(JFlipPane.LEFT)   ? JFlipPane.SPLITTER_WIDTH : 0);
    } 

    //--------------------------------------------------------------------------
    // javax.swing.border.Border Interface
    //--------------------------------------------------------------------------
    
    /**
     * Paint custom border to look like a split pane divider.
     * 
     * @see javax.swing.border.Border#paintBorder(java.awt.Component,
     *      java.awt.Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, 
        int width, int height)
    {
        updateColors();

        if (color1_ == null || color2_ == null || color3_ == null)
            return;

        if (position_.equals(JFlipPane.BOTTOM))
            paintHorizBorder(g, x, y, width);
        else if (position_.equals(JFlipPane.RIGHT))
            paintVertBorder(g, x, y, height);
        else if (position_.equals(JFlipPane.TOP))
            paintHorizBorder(g, x, y + height - JFlipPane.SPLITTER_WIDTH, 
                width);
        else if (position_.equals(JFlipPane.LEFT))
            paintVertBorder(g, x + width - JFlipPane.SPLITTER_WIDTH, y, height);
    } 

    
    /**
     * Return custom insets.
     * 
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    public Insets getBorderInsets(Component c)
    {
        return insets_;
    } 
    
    
    /**
     * Border is never opaque.
     * 
     * @see javax.swing.border.Border#isBorderOpaque()
     */
    public boolean isBorderOpaque()
    {
        return false;
    } 
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Paints horizontal border.
     * 
     * @param g Graphics context.
     * @param x X coord.
     * @param y Y coord.
     * @param width Width.
     */
    private void paintHorizBorder(Graphics g, int x, int y, int width)
    {
        g.setColor(color3_);
        g.fillRect(x, y, width, JFlipPane.SPLITTER_WIDTH);

        for (int i = 0; i < width / 4 - 1; i++)
        {
            g.setColor(color1_);
            g.drawLine(x + i * 4 + 2, y + 3, x + i * 4 + 2, y + 3);
            g.setColor(color2_);
            g.drawLine(x + i * 4 + 3, y + 4, x + i * 4 + 3, y + 4);
            g.setColor(color1_);
            g.drawLine(x + i * 4 + 4, y + 5, x + i * 4 + 4, y + 5);
            g.setColor(color2_);
            g.drawLine(x + i * 4 + 5, y + 6, x + i * 4 + 5, y + 6);
        }
    } 
    
    
    /**
     * Paints vertical border.
     * 
     * @param g Graphics context.
     * @param x X coord.
     * @param y Y cord.
     * @param height Height.
     */
    private void paintVertBorder(Graphics g, int x, int y, int height)
    { 
        g.setColor(color3_);
        g.fillRect(x, y, JFlipPane.SPLITTER_WIDTH, height);

        for (int i = 0; i < height / 4 - 1; i++)
        {
            g.setColor(color1_);
            g.drawLine(x + 3, y + i * 4 + 2, x + 3, y + i * 4 + 2);
            g.setColor(color2_);
            g.drawLine(x + 4, y + i * 4 + 3, x + 4, y + i * 4 + 3);
            g.setColor(color1_);
            g.drawLine(x + 5, y + i * 4 + 4, x + 5, y + i * 4 + 4);
            g.setColor(color2_);
            g.drawLine(x + 6, y + i * 4 + 5, x + 6, y + i * 4 + 5);
        }
    } 


    /**
     * Updates colors to match bumps on the currently installed look and feel.
     */
    private void updateColors()
    {
        // Try to use the currently installed LAF's colors for the given keys...
        color1_ = UIManager.getColor("controlHighlight");
        color2_ = UIManager.getColor("controlDkShadow");
        color3_ = UIManager.getColor("control");
    } 
}