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

import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * Font Utilities.
 */
public final class FontUtil implements IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(FontUtil.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    static final String NODE_FONTUTIL = "FontUtil";
    static final String NODE_DEFAULT_MONO = "DefaultMono";
    static final String NODE_DEFAULT_SERIF = "DefaultSerif";

    static final String NODE_FONT = "Font";
    static final String ATTR_SIZE = "size";
    static final String ATTR_STYLE = "style";
    static final String ATTR_FAMILY = "family";
    static final String ATTR_FONTNAME = "fontName";
    static final String ATTR_NAME = "name";

    //--------------------------------------------------------------------------
    // Static
    //--------------------------------------------------------------------------

    /**
     * Singleton instance.
     */
    private static FontUtil instance_;

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
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Preferred monospaced font.
     */
    private Font monoFont_;
    
    /** 
     * Preferred serif font. 
     */
    private Font serifFont_;
    
    //--------------------------------------------------------------------------
    // Static Private
    //--------------------------------------------------------------------------
    
    /**
     * Returns the singleton instanec of FontUtil.
     * 
     * @return FontUtil.
     */
    public static FontUtil getInstance()
    {
        if (instance_ == null)
            instance_ = new FontUtil();
        
        return instance_;
    }

    //--------------------------------------------------------------------------
    // Preferred Fonts
    //--------------------------------------------------------------------------
    
    /**
     * Returns the preferred monospaced font available on the system.
     * 
     * @return Font
     */
    public static synchronized Font getPreferredMonoFont()
    {
        FontUtil instance = getInstance();
        
        if (instance.monoFont_ == null)
        {
            instance.monoFont_ = getPreferredFont(preferredMono_);
            
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            
            // Make font smaller if desktop width beyound 1280. Just look
            // better or the font ends up being too large.
            
            if (d.getWidth() >= 1280)
                instance.monoFont_ = shrink(instance.monoFont_, 1);
        }
        
        return instance.monoFont_;               
    }


    /**
     * Returns the preferred variable text font available on the system.
     * 
     * @return Font
     */
    public static synchronized Font getPreferredSerifFont()
    {
        FontUtil instance = getInstance();
        
        if (instance.serifFont_ == null)
            instance.serifFont_ = getPreferredFont(preferredSerif_);
        
        return instance_.serifFont_;               
    }

    
    /**
     * Sets the preferred serif font.
     * 
     * @param f Serif font.
     */
    public static void setPreferredSerifFont(Font f)
    {
        getInstance().serifFont_ = f;
    }
    
    
    /**
     * Sets the preferred monospaced font.
     * 
     * @param f Monospaced font.
     */
    public static void setPreferredMonoFont(Font f)
    {
        getInstance().monoFont_ = f;
    }
    
    //--------------------------------------------------------------------------
    // XML <--> Font
    //--------------------------------------------------------------------------
    
    /**
     * Serializes a font to its XML representation.
     * 
     * @param f Font to serialze.
     * @return Element that captures the fonts characteristics.
     */
    public static Element toElement(Font f)
    {
        Element font = new Element(NODE_FONT);
        font.addAttribute(new Attribute(ATTR_SIZE, f.getSize() + ""));
        font.addAttribute(new Attribute(ATTR_STYLE, f.getStyle() + ""));
        font.addAttribute(new Attribute(ATTR_FAMILY, f.getFamily()));
        font.addAttribute(new Attribute(ATTR_FONTNAME, f.getFontName()));
        font.addAttribute(new Attribute(ATTR_NAME, f.getName()));
        
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
        String name = e.getAttributeValue(ATTR_NAME);
        int size = Integer.parseInt(e.getAttributeValue(ATTR_SIZE));
        int style = Integer.parseInt(e.getAttributeValue(ATTR_STYLE));
        return new Font(name, style, size);
    }
    
    //--------------------------------------------------------------------------
    // Decorate Font
    //--------------------------------------------------------------------------
    
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
    
    //--------------------------------------------------------------------------
    // Query Font
    //--------------------------------------------------------------------------
    
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
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = PreferencedUtil.getElement(prefs, NODE_FONTUTIL);
        
        monoFont_ = FontUtil.toFont(
            PreferencedUtil.getElement(
                PreferencedUtil.getElement(root, NODE_DEFAULT_MONO),
                NODE_FONT));
        
        serifFont_ = FontUtil.toFont(
            PreferencedUtil.getElement(
                PreferencedUtil.getElement(root, NODE_DEFAULT_SERIF),
                NODE_FONT));
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_FONTUTIL);

        Element mono = new Element(NODE_DEFAULT_MONO);
        mono.appendChild(FontUtil.toElement(monoFont_));
        XOMUtil.insertOrReplace(root, mono);

        Element serif = new Element(NODE_DEFAULT_SERIF);
        serif.appendChild(FontUtil.toElement(serifFont_));
        XOMUtil.insertOrReplace(root, serif);
        
        XOMUtil.insertOrReplace(prefs, root);
    }
}