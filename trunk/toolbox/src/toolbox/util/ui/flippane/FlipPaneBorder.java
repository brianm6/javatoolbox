package toolbox.util.ui.flippane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Custom border for JFlipPane
 */
public class FlipPaneBorder implements Border
{
    private String position_;
    private Insets insets_;
    private Color color1_;
    private Color color2_;
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
            position.equals(JFlipPane.BOTTOM)?JFlipPane.SPLITTER_WIDTH : 0,
            position.equals(JFlipPane.RIGHT) ?JFlipPane.SPLITTER_WIDTH : 0,
            position.equals(JFlipPane.TOP)   ?JFlipPane.SPLITTER_WIDTH : 0,
            position.equals(JFlipPane.LEFT)  ?JFlipPane.SPLITTER_WIDTH : 0);
    } 

    //--------------------------------------------------------------------------
    // javax.swing.border.Border Interface
    //--------------------------------------------------------------------------
    
    /**
     * Paints the border.
     * 
     * @param c Component to paint
     * @param g Graphics device
     * @param x X coord
     * @param y Y coord
     * @param width Width
     * @param height Height
     */
    public void paintBorder(Component c, Graphics g, int x, int y, 
        int width, int height)
    {
        updateColors();

        if (color1_ == null || color2_ == null || color3_ == null)
            return;

        if (position_.equals(JFlipPane.BOTTOM))
            paintHorizBorder(g,x,y,width);
        else if (position_.equals(JFlipPane.RIGHT))
            paintVertBorder(g,x,y,height);
        else if (position_.equals(JFlipPane.TOP))
            paintHorizBorder(g,x,y + height - JFlipPane.SPLITTER_WIDTH,
                width);
        else if (position_.equals(JFlipPane.LEFT))
            paintVertBorder(g,x + width - JFlipPane.SPLITTER_WIDTH,y,
                height);
    } 


    /**
     * Retrieves border insets.
     * 
     * @param c Component
     * @return Border insets
     */
    public Insets getBorderInsets(Component c)
    {
        return insets_;
    } 
    
    
    /**
     * @return True if border is opaque, false otherwise.
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
     */
    private void paintHorizBorder(Graphics g, int x, int y, int width)
    {
        g.setColor(color3_);
        g.fillRect(x, y, width, JFlipPane.SPLITTER_WIDTH);

        for(int i = 0; i < width / 4 - 1; i++)
        {
            g.setColor(color1_);
            g.drawLine(x + i * 4 + 2,y + 3, x + i * 4 + 2,y + 3);
            g.setColor(color2_);
            g.drawLine(x + i * 4 + 3,y + 4, x + i * 4 + 3,y + 4);
            g.setColor(color1_);
            g.drawLine(x + i * 4 + 4,y + 5, x + i * 4 + 4,y + 5);
            g.setColor(color2_);
            g.drawLine(x + i * 4 + 5,y + 6, x + i * 4 + 5,y + 6);
        }
    } 
    
    
    /**
     * Paints vertical border.
     */
    private void paintVertBorder(Graphics g, int x, int y, int height)
    {
        g.setColor(color3_);
        g.fillRect(x, y, JFlipPane.SPLITTER_WIDTH, height);

        for(int i = 0; i < height / 4 - 1; i++)
        {
            g.setColor(color1_);
            g.drawLine(x + 3,y + i * 4 + 2, x + 3,y + i * 4 + 2);
            g.setColor(color2_);
            g.drawLine(x + 4,y + i * 4 + 3, x + 4,y + i * 4 + 3);
            g.setColor(color1_);
            g.drawLine(x + 5,y + i * 4 + 4, x + 5,y + i * 4 + 4);
            g.setColor(color2_);
            g.drawLine(x + 6,y + i * 4 + 5, x + 6,y + i * 4 + 5);
        }
    } 


    /**
     * Updates colors.
     */
    private void updateColors()
    {
        if(UIManager.getLookAndFeel() instanceof MetalLookAndFeel)
        {
            color1_ = MetalLookAndFeel.getControlHighlight();
            color2_ = MetalLookAndFeel.getControlDarkShadow();
            color3_ = MetalLookAndFeel.getControl();
        }
        else
        {
            color1_ = color2_ = color3_ = null;
        }
    } 
}