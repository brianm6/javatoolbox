package toolbox.util.ui.flippane;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;

/**
 * FlipIcon
 */
public class FlipIcon implements Icon
{
    static final int NONE = 0;
    static final int CW = 1;
    static final int CCW = 2;

    private int rotate_;
    private Font font_;
    private GlyphVector glyphs_;
    private float width_;
    private float height_;
    private float ascent_;
    private RenderingHints renderHints_;

    /**
     * Creates a FlipIcon
     * 
     * @param   rotate  [NONE|CW|CCW]
     * @param   font    Font to use for rendering
     * @param   text    Text of icon
     */
    public FlipIcon(int rotate, Font font, String text)
    {
        rotate_ = rotate;
        font_ = font;

        FontRenderContext fontRenderContext = 
            new FontRenderContext(null,true,true);
            
        glyphs_ = font.createGlyphVector(fontRenderContext,text);
        width_ = (int)glyphs_.getLogicalBounds().getWidth() + 4;
        //height = (int)glyphs.getLogicalBounds().getHeight();

        LineMetrics lineMetrics = 
            font.getLineMetrics(text,fontRenderContext);
            
        ascent_ = lineMetrics.getAscent();
        height_ = (int)lineMetrics.getHeight();

        renderHints_ = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
            
        renderHints_.put(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            
        renderHints_.put(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
    } 

    /**
     * @return Icon width
     */
    public int getIconWidth()
    {
        return (int)(rotate_ == FlipIcon.CW || 
                     rotate_ == FlipIcon.CCW ? height_ : width_);
    } 

    /**
     * @return  Icon height
     */
    public int getIconHeight()
    {
        return (int)(rotate_ == FlipIcon.CW ||
                     rotate_ == FlipIcon.CCW ? width_ : height_);
    } 

    /**
     * Renders the icon on the graphics
     * 
     * @param  c  Component
     * @param  g  Graphics
     * @param  x  X coord
     * @param  y  y coord
     */
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setFont(font_);
        AffineTransform oldTransform = g2d.getTransform();
        RenderingHints oldHints = g2d.getRenderingHints();

        g2d.setRenderingHints(renderHints_);
        g2d.setColor(c.getForeground());

        
        if (rotate_ == FlipIcon.NONE)
        {
            // No rotation
            g2d.drawGlyphVector(glyphs_,x + 2,y + ascent_);
        } 
        else if (rotate_ == FlipIcon.CW)
        {
            // Clockwise rotation
            AffineTransform trans = new AffineTransform();
            trans.concatenate(oldTransform);
            trans.translate(x, y + 2);
            trans.rotate(Math.PI / 2, height_ / 2, width_ / 2);
            g2d.setTransform(trans);
            g2d.drawGlyphVector(glyphs_,(height_ - width_) / 2,
                (width_ - height_) / 2 + ascent_);
        } 
        else if (rotate_ == FlipIcon.CCW)
        {
            // Counterclockwise rotation
            AffineTransform trans = new AffineTransform();
            trans.concatenate(oldTransform);
            trans.translate(x,y - 2);
            trans.rotate(Math.PI * 3 / 2, height_ / 2, width_ / 2);
            g2d.setTransform(trans);
            g2d.drawGlyphVector(glyphs_,(height_ - width_) / 2,
                (width_ - height_) / 2 + ascent_);
        } 

        g2d.setTransform(oldTransform);
        g2d.setRenderingHints(oldHints);
    } 
}