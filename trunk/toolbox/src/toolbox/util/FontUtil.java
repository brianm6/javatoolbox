package toolbox.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

/**
 * Font Utilities.
 */
public final class FontUtil
{
    private static final Logger logger_ = Logger.getLogger(FontUtil.class);
    
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
     * @param f Font to serialze
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
     * @param e Element containing the font specification
     * @return Font
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
            monoFont_ = getPreferredFont(preferredMono_);
        
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
