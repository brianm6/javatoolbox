package toolbox.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.text.StyleContext;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

/**
 * Font Utilities.
 */
public final class FontUtil
{
    private static final Logger logger_ = Logger.getLogger(FontUtil.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Preferred monospaced font.
     */
    private static Font monoFont_;
    
    /** 
     * Preferred serif font. 
     */
    private static Font serifFont_;

    /**
     * Pick list for mono fonts. First one has the highest priority.
     */
    private static String[] preferredMono_ = new String[] 
    {
        "Lucida Sans Typewriter",
        "Lucida Console",
        "Monospaced",
        "Mono",
        "mono",
        "Courier",
        "Dialog"
    };
    
    /**
     * Pick list for the serif fonts. First one has the highest priority.
     */
    private static String[] preferredSerif_ = new String[]
    {
        "Tahoma",
        "Verdana",
        "Trebuchet MS",
        "Arial",
        "serif",
        "Dialog"
    };
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Serializes a font to its XML representation.
     * 
     * @param f Font to serialze.
     * @return Element that captures the fonts characteristics.
     */
    public static Element toElement(Font f)
    {
        Element font = new Element("Font");
        font.addAttribute(new Attribute("size", f.getSize() + ""));
        font.addAttribute(new Attribute("style", f.getStyle() + ""));
        font.addAttribute(new Attribute("family", f.getFamily()));
        font.addAttribute(new Attribute("fontName", f.getFontName()));
        font.addAttribute(new Attribute("name", f.getName()));
        
        return font;
    }
    
    
    /**
     * Hydrates a font from its XML representation.
     * 
     * @param e Element containing the font specification.
     * @return Font.
     */
    public static Font toFont(Element e)
    {
        String name = e.getAttributeValue("name");
        int size = Integer.parseInt(e.getAttributeValue("size"));
        int style = Integer.parseInt(e.getAttributeValue("style"));
        return new Font(name, style, size);
    }
    
    
    /**
     * Simple way to apply the bold style to an existing component.
     * 
     * @param c Component to bold.
     */
    public static void setBold(JComponent c)
    {
        c.setFont(c.getFont().deriveFont(Font.BOLD));
    }


    /**
     * Returns the preferred monospaced font available on the system.
     * 
     * @return Font
     */
    public static synchronized Font getPreferredMonoFont()
    {
        if (monoFont_ == null)
        {
            monoFont_ = getPreferredFont(preferredMono_);
            
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            
            // Make font smaller if desktop width beyound 1280. Just look
            // better or the font ends up being too large.
            
            if (d.getWidth() >= 1280)
                monoFont_ = shrink(monoFont_, 1);
        }
        
        return monoFont_;               
    }


    /**
     * Returns the preferred variable text font available on the system.
     * 
     * @return Font
     */
    public static synchronized Font getPreferredSerifFont()
    {
        if (serifFont_ == null)
            serifFont_ = getPreferredFont(preferredSerif_);
        
        return serifFont_;               
    }

    
    /**
     * Increases a fonts size by the given number of units.
     * 
     * @param font Font to base increased size on.
     * @param units The number of units to increase the size.
     * @return Font with the increased size.
     */
    public static Font grow(final Font font, int units)
    {
        Font f = font.deriveFont((float) (font.getSize() + units));
        return f;
    }

    
    /**
     * Decreases a fonts size by the given number of units.
     * 
     * @param font Font to base decreased size on.
     * @param units The number of units to decrease the size.
     * @return Font with the decreased size.
     */
    public static Font shrink(final Font font, int units)
    {
        return grow(font, -units);
    }
    
    
    /**
     * Returns a new font with the given size based on the passed in font.
     * 
     * @param font Original font.
     * @param size Desired size.
     * @return Font
     */
    public static Font setSize(final Font font, int size) 
    {
        return grow(font, size - font.getSize());
    }
    
    
    /**
     * Returns true if the given font is a monospaced font, false otherwise.
     * This method is not as exhaustive in its criteria as it could be.
     * 
     * @param font Font
     * @return boolean
     */
    public static boolean isMonospaced(final Font font)
    {
        FontMetrics fm = 
            StyleContext.getDefaultStyleContext().getFontMetrics(font);
        
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = lower.toUpperCase();
        
        for (int i = 0, n = lower.length(); i < n; i++)
        {
            int widthLower = fm.charWidth(lower.charAt(i));
            int widthUpper = fm.charWidth(upper.charAt(i));
        
            if (widthLower != widthUpper)
                return false;
        }
        
        return true;
    }
    
    //--------------------------------------------------------------------------
    // Private 
    //--------------------------------------------------------------------------
    
    /**
     * Returns the first available font from the given list of preferred fonts.
     * 
     * @param preferred List of preferred fonts to choose from.
     * @return Font
     */
    private static Font getPreferredFont(String[] preferred)
    {
        GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();

        String[] fontNames = ge.getAvailableFontFamilyNames();
        String fontName = null;

        for (int i = 0; i < preferred.length; i++)
        {
            if (ArrayUtil.contains(fontNames, preferred[i]))
            {
                fontName = preferred[i];
                break;
            }
        }

        Map fontAttribs = new HashMap();

        if (fontName != null)
        {

            fontAttribs = new HashMap();
            fontAttribs.put(TextAttribute.FAMILY, fontName);
            fontAttribs.put(TextAttribute.FONT, fontName);
            fontAttribs.put(TextAttribute.SIZE, new Float(12));
        }

        return new Font(fontAttribs);
    }
}