package toolbox.util.ui.font;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

/**
 * Component for displaying a "phrase" (a brief, one or two word String) 
 * using a particular font & a particular color.
 */
public class PhraseCanvas extends Canvas
{
    protected Color     color_;        
    protected String    phrase_;
    protected Font      font_;
    protected boolean   antialiasOn_;
            
    /**
     * Constructs a new PhraseCanvas with the supplied phrase, font, and 
     * color.
     * 
     * @param   phrase  Phrase to be displayed in this PhraseCanvas
     * @param   font    Font to use when rendering the phrase
     * @param   color   Color to use when rendering the phrase
     */
    public PhraseCanvas(String phrase, Font font, Color color)
    {
        phrase_ = phrase;
        font_ = font;
        color_ = color;
        antialiasOn_ = true;
    }


    /** 
     * @see java.awt.Canvas#paint(java.awt.Graphics) 
     */
    public void paint(Graphics g)
    {
        // Workaround for bug in Font.createGlyphVector(), in review by
        // Sun with review id 108400.
        Font dummyFont =
            new Font(
                font_.getFamily(),
                font_.getStyle(),
                font_.getSize() + 1);
                
        dummyFont.createGlyphVector(
            new FontRenderContext(null, antialiasOn_, false),
            phrase_);

        GlyphVector glyphVector =
            font_.createGlyphVector(
                new FontRenderContext(null, antialiasOn_, false),
                phrase_);
                
        // Use precedent set by applications like MS Word to place
        // glyph vector in the canvas:
        //
        // 1. If the total width of the glyph vector is less than the
        //    width of the canvas, the glyph vector will be horizontally 
        //    centered in the canvas; else the glyph vector will be 
        //    left-aligned
        //
        // 2. If the total height of the glyph vector is less than the 
        //    height of the canvas, the glyph vector will be vertically 
        //    centered in the canvas; else the glyph vector will be 
        //    bottom-aligned
        
        Rectangle2D logicalBounds = glyphVector.getLogicalBounds();
        double x;
        
        if (logicalBounds.getWidth() < this.getWidth())
            x = (this.getWidth() / 2) - (logicalBounds.getWidth() / 2);
        else
            x = 0;
        
        double y;
        
        if (logicalBounds.getHeight() < this.getHeight())
            y = (this.getHeight() / 2) + (logicalBounds.getHeight() / 2);
        else
            y = this.getHeight();

        g.setColor(color_);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawGlyphVector(glyphVector, (float) x, (float) y);
    }


    /** 
     * Returns the phrase to be rendered by this PhraseCanvas.
     * 
     * @return phrase to be rendered by this PhraseCanvas 
     */
     public String getPhrase()
     {
         return phrase_;
     }
 
        
    /** 
     * Sets the phrase to be rendered by this PhraseCanvas.
     * 
     * @param   phrase  New phrase to be rendered by this PhraseCanvas;
     *                  this new value will be rendered the next time 
     *                  {@link #paint(java.awt.Graphics)} is called 
     */
    public void setPhrase(String phrase)
    {
        phrase_ = phrase;
    }
    
    
    /** 
     * Returns the font to use when rendering the phrase.
     * 
     * @return font to use when rendering the phrase 
     */ 
    public Font getFont()
    {
        return font_;
    }
    
    
    /**
     * Sets the font to use when rendering the phrase.
     * 
     * @param   font    New font to use when rendering the phrase;
     *                  this new value will be used to render the phrase 
     *                  the next time {@link #paint(java.awt.Graphics)} 
     *                  is called 
     */
    public void setFont(Font font)
    {
        font_ = font;
    }


    /**
     * Returns the color to use when rendering the phrase.
     * 
     * @return color to use when rendering the phrase 
     */
    public Color getColor()
    {
        return color_;
    }
    
    
    /** 
     * Sets the color to use when rendering the phrase.
     * 
     * @param   color   New color to use when rendering the phrase;
     *                  this new value will be used to render the phrase 
     *                  the next time {@link #paint(java.awt.Graphics)} 
     *                  is called 
     */
    public void setColor(Color color)
    {
        color_ = color;
    }


    /** 
     * Returns true iff anti-aliasing is used when rendering the phrase.
     * 
     * @return whether or not anti-aliasing is used when rendering the 
     *         phrase 
     */
    public boolean isAntialiasOn()
    {
        return antialiasOn_;
    }
    
    
    /** 
     * Turn anti-aliasing on or off.
     * 
     * @param antialiasOn  Whether or not to use anti-aliasing when 
     *                     rendering the phrase this new value will be used 
     *                     to render the phrase the next time 
     *                     {@link #paint(java.awt.Graphics)} is called 
     */
    public void setAntialiasOn(boolean antialiasOn)
    {
        antialiasOn_ = antialiasOn;
    }
}
