package toolbox.jtail;

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
    
    // XML Constants
    private static final String ELEMENT_TAIL     = "Tail";
    private static final String ATTR_FILE        = "file";
    private static final String ATTR_AUTOSCROLL  = "autoScroll";
    private static final String ATTR_LINENUMBERS = "showLineNumbers";
    
    private String  filename_;
    private boolean autoScroll_;
    private boolean showLineNumbers_;

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
    public TailConfig(String file, boolean autoScroll, boolean showLineNumbers)
    {
        setFilename(file);
        setAutoScroll(autoScroll);
        setShowLineNumbers(showLineNumbers);
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
     * Unmarshals an XML element representing a TailProp object
     * 
     * @param   tail  Element representing a tailprop
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
        props.setFilename(tail.getAttribute(ATTR_FILE));
        
        props.setAutoScroll(
            new Boolean(tail.getAttribute(ATTR_AUTOSCROLL)).booleanValue());
            
        props.setShowLineNumbers(
            new Boolean(tail.getAttribute(ATTR_LINENUMBERS)).booleanValue());
            
        return props;
    }


    /**
     * Writes the state of this object to the writer in XML
     * 
     * @param  writer   Destination for XML
     */
    public Element marshal()  throws IOException 
    {
        Element tail = new Element(ELEMENT_TAIL);
        tail.setAttribute(ATTR_FILE, getFilename());
        
        tail.setAttribute(ATTR_AUTOSCROLL, 
            new Boolean(isAutoScroll()).toString());
            
        tail.setAttribute(ATTR_LINENUMBERS, 
            new Boolean(isShowLineNumbers()).toString());
            
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
