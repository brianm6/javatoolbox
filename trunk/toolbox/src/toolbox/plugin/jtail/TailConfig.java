package toolbox.jtail;

import java.awt.Font;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Category;
import toolbox.util.collections.ObjectMap;

import electric.xml.Attribute;
import electric.xml.Attributes;
import electric.xml.Element;

/**
 * Tail properties
 */
public class TailConfig
{
    /** Logger **/
    private static final Category logger_ =
        Category.getInstance(TailConfig.class);
    
    // Tail XML element
    public  static final String ELEMENT_TAIL     = "Tail";
    private static final String ATTR_FILE        = "file";
    private static final String ATTR_AUTOSCROLL  = "autoScroll";
    private static final String ATTR_LINENUMBERS = "showLineNumbers";
    
    // Font XML element
    private static final String ELEMENT_FONT     = "Font";
    private static final String ATTR_FAMILY      = "family";
    private static final String ATTR_STYLE       = "style";        
    private static final String ATTR_SIZE        = "size";

    
    private String  filename_;
    private boolean autoScroll_;
    private boolean showLineNumbers_;
    private Font    font_;

    /**
     * Default constructor
     */
    public TailConfig()
    {
    }


    /**
     * Creates TailConfig with given parameters
     * 
     * @param  file             File to tail
     * @param  autoScroll       Turn on autoscroll
     * @param  showLineNumbers  Shows line numbers in output
     */
    public TailConfig(String file, boolean autoScroll, boolean showLineNumbers,
        Font font)
    {
        setFilename(file);
        setAutoScroll(autoScroll);
        setShowLineNumbers(showLineNumbers);
        setFont(font);
    }


    /**
     * Returns the autoScroll.
     * 
     * @return boolean
     */
    public boolean isAutoScroll()
    {
        return autoScroll_;
    }


    /**
     * Returns the filename.
     * 
     * @return String
     */
    public String getFilename()
    {
        return filename_;
    }
    

    /**
     * Returns the showLineNumbers.
     * 
     * @return boolean
     */
    public boolean isShowLineNumbers()
    {
        return showLineNumbers_;
    }


    /**
     * Sets the autoScroll.
     * 
     * @param autoScroll The autoScroll to set
     */
    public void setAutoScroll(boolean autoScroll)
    {
        autoScroll_ = autoScroll;
    }


    /**
     * Sets the filename.
     * 
     * @param filename The filename to set
     */
    public void setFilename(String filename)
    {
        filename_ = filename;
    }


    /**
     * Sets the showLineNumbers.
     * 
     * @param showLineNumbers The showLineNumbers to set
     */
    public void setShowLineNumbers(boolean showLineNumbers)
    {
        showLineNumbers_ = showLineNumbers;
    }
    
    
    /**
     * Returns the font.
     * 
     * @return Font
     */
    public Font getFont()
    {
        return font_;
    }


    /**
     * Sets the font.
     * 
     * @param font The font to set
     */
    public void setFont(Font font)
    {
        font_ = font;
    }

    
    /**
     * Unmarshals an XML element representing a TailProp object
     * 
     * @param   tail  Element representing a tailprops
     * @return  Fully populated TailProp
     */
    public static TailConfig unmarshal(Element tail) throws IOException 
    {
        
        // DEBUG
        
        Attributes attribs = tail.getAttributeObjects();
        
        while (attribs.hasMoreElements())
        {
            Attribute attrib = attribs.next();
            
            logger_.debug("[unmars] " + attrib.getName() + " : " + 
                attrib.getValue());
        }
        
        // REAL 
        
        TailConfig props = new TailConfig();
        
        // Handle tail element
        props.setFilename(tail.getAttribute(ATTR_FILE));
        props.setAutoScroll(
            new Boolean(tail.getAttribute(ATTR_AUTOSCROLL)).booleanValue());
        props.setShowLineNumbers(
            new Boolean(tail.getAttribute(ATTR_LINENUMBERS)).booleanValue());
        
        // Handle font element    
        Element fontNode = tail.getElement(ELEMENT_FONT);
        String  family   = fontNode.getAttribute(ATTR_FAMILY);
        int     style    = Integer.parseInt(fontNode.getAttribute(ATTR_STYLE));
        int     size     = Integer.parseInt(fontNode.getAttribute(ATTR_SIZE));
        props.setFont(new Font(family, style, size));
            
        return props;
    }


    /**
     * Marshals from Java object representation to XML representation
     * 
     * @return  Tail XML node
     */
    public Element marshal()  throws IOException 
    {
        // Tail element
        Element tail = new Element(ELEMENT_TAIL);
        tail.setAttribute(ATTR_FILE, getFilename());
        tail.setAttribute(ATTR_AUTOSCROLL, 
            new Boolean(isAutoScroll()).toString());
        tail.setAttribute(ATTR_LINENUMBERS, 
            new Boolean(isShowLineNumbers()).toString());
        
        // Font element    
        Element font = new Element(ELEMENT_FONT);
        font.setAttribute(ATTR_FAMILY, getFont().getFamily());
        font.setAttribute(ATTR_STYLE, getFont().getStyle() + "");
        font.setAttribute(ATTR_SIZE, getFont().getSize() + "");            
        
        // Make font child of tail
        tail.addElement(font);
        
        return tail;
    }
    
    
    /**
     * @return String representation
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer("\n");
        
        try
        {
            ObjectMap map = new ObjectMap(this);
            
            Iterator i = map.keySet().iterator();
            
            while(i.hasNext())
            {
                Object key = i.next();
                Object value = map.get(key);
                sb.append(key + " = " + value + "\n");        
            }
        }
        catch (IntrospectionException e)
        {
        }
        
        return sb.toString();
    }
}
