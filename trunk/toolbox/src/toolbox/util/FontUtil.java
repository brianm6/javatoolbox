package toolbox.util;

import java.awt.Font;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * Font Utilities
 */
public final class FontUtil
{
    /**
     * Serializes a font to its XML representation.
     * 
     * @param   f  Font to serialze
     * @return  Element that captures the fonts characteristics.
     */
    public static Element toElement(Font f)
    {
        Element font = new Element("Font");
        font.addAttribute(new Attribute("size", f.getSize()+""));
        font.addAttribute(new Attribute("style", f.getStyle()+""));
        font.addAttribute(new Attribute("family", f.getFamily()));
        font.addAttribute(new Attribute("fontName", f.getFontName()));
        font.addAttribute(new Attribute("name", f.getName()));
        
        return font;
    }
    
    /**
     * Hydrates a font from its XML representation.
     * 
     * @param   e  Element containing the font specification
     * @return  Font
     */
    public static Font toFont(Element e)
    {
        String name = e.getAttributeValue("name");
        int size = Integer.parseInt(e.getAttributeValue("size"));
        int style = Integer.parseInt(e.getAttributeValue("style"));
        return new Font(name, style, size);
    }
}
