package toolbox.util;

import java.awt.Component;
import java.awt.Rectangle;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Utility methods for classes implementing IPreferenced.
 * 
 * @see toolbox.workspace.IPreferenced
 * @see org.apache.commons.beanutils.Converter
 * @see org.apache.commons.beanutils.ConvertUtils
 */
public class PreferencedUtil
{
    //--------------------------------------------------------------------------
    // XML Constants
    //--------------------------------------------------------------------------
    
    /**
     * Attribute for the width of a component.
     */
    private static final String ATTR_WIDTH = "w";
    
    /**
     * Attribute for the height of a component.
     */
    private static final String ATTR_HEIGHT = "h";
    
    /**
     * Attribute for the x-axis location of a component.
     */
    private static final String ATTR_X = "x";
    
    /**
     * Attribute for the y-axis location of a component.
     */
    private static final String ATTR_Y = "y";
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction of this utility class.
     */
    private PreferencedUtil()
    {
    }
    
    //--------------------------------------------------------------------------
    // JavaBean Helpers
    //--------------------------------------------------------------------------
    
    /**
     * Reads properties from a javabean and adds them to an XML node as 
     * atttributes.
     * <p>
     * <b>Example implementation of IPreferenced.savePrefs(Element):</b>
     * <pre class="snippet">
     * public void savePrefs(Element prefs) throws Exception
     * {
     *     String[] props = {"width", "height", "weight"};
     *     Element root = new Element("root");
     *     PreferencedUtil.writePreferences(this, root, props);
     *     XOMUtil.insertOrReplace(prefs, root);
     * }
     * </pre>
     * 
     * @param bean Javabean to extract values from.
     * @param node XML node to add attributes to.
     * @param propNames Array of javabean property names/XML attribute names to
     *        to read/write.
     * @throws Exception on error.
     */
    public static void writePreferences(
        Object bean, 
        Element node, 
        String[] propNames)
        throws Exception
    {
        BeanInfo info = Introspector.getBeanInfo(bean.getClass());
        PropertyDescriptor[] ds = info.getPropertyDescriptors();
        
        for (int i = 0; i < ds.length; i++)
        {
            PropertyDescriptor descriptor = ds[i];
            String propName = descriptor.getName();
            
            if (ArrayUtil.contains(propNames, propName))
            {    
                // If the value is null, leave it out
                String value = BeanUtils.getProperty(bean, propName);

                if (value != null)
                {
                    Attribute attr = new Attribute(propName, value);
                    node.addAttribute(attr);
                }
            }
        }
    }

    
    /**
     * Reads attributes from an XML node and applies them to a javabean as
     * properties.
     * <p>
     * <b>Example implementation of IPreferenced.applyPrefs(Element)</b>
     * <pre class="snippet">
     * public void applyPrefs(Element prefs) throws Exception
     * {
     *     String[] props = {"width", "height", "weight"};
     * 
     *     Element root = XOMUtil.getFirstChildElement(
     *         prefs, "root", new Element("root"));
     * 
     *     PreferencedUtil.readPreferences(this, root, props);
     * }
     * </pre>
     * 
     * @param bean Javabean to apply the preferences to.
     * @param node XML node to read the attributes from.
     * @param propNames Array of javabean property names/XML attribute names to
     *        to read/write.
     * @throws Exception on error.
     */
    public static void readPreferences(
        Object bean, 
        Element node, 
        String[] propNames)
        throws Exception
    {
        BeanInfo info = Introspector.getBeanInfo(bean.getClass());
        PropertyDescriptor[] ds = info.getPropertyDescriptors();
        
        for (int i = 0; i < ds.length; i++)
        {
            PropertyDescriptor descriptor = ds[i];
            String propName = descriptor.getName();
            
            if (ArrayUtil.contains(propNames, propName))
            {    
                Attribute attr = node.getAttribute(propName);
                
                if (attr != null)
                    BeanUtils.setProperty(bean, propName, attr.getValue());
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Component Helpers
    //--------------------------------------------------------------------------
    
    /**
     * Convenience method to apply preferences to a Component. Supported 
     * properties include size and location.
     * 
     * @param node Node containing valid attributes for size and location.
     * @param c Component to apply the preferences to.
     */
    public static void applyPrefs(Element node, Component c)
    {
        applyPrefs(node, c, new Rectangle(0, 0, 800, 600));
    }

    
    /**
     * Convenience method to apply preferences to a Component. Supported 
     * properties include size and location.
     * 
     * @param node Node containing valid attributes for size and location.
     * @param c Component to apply the preferences to.
     * @param defaults If any of the attributes are not found, a default value
     *        is used from this default.
     */
    public static void applyPrefs(Element node, Component c, Rectangle defaults)
    {
        c.setBounds(
            XOMUtil.getIntegerAttribute(node, ATTR_X, defaults.x),
            XOMUtil.getIntegerAttribute(node, ATTR_Y, defaults.y),
            XOMUtil.getIntegerAttribute(node, ATTR_WIDTH, defaults.width),
            XOMUtil.getIntegerAttribute(node, ATTR_HEIGHT, defaults.height));
    }

    
    /**
     * Convenience method to save a Components preferences to node. Supported 
     * properties include size and location.
     * 
     * @param node Node to append attributes to.
     * @param c Component to read size and location from.
     */
    public static void savePrefs(Element node, Component c)
    {
        Rectangle r = c.getBounds();
        node.addAttribute(new Attribute(ATTR_X, r.x + ""));
        node.addAttribute(new Attribute(ATTR_Y, r.y + ""));
        node.addAttribute(new Attribute(ATTR_WIDTH, r.width + ""));
        node.addAttribute(new Attribute(ATTR_HEIGHT, r.height + ""));
    }

    /**
     * Convenience method to return the child element of a given node.
     * If the child element does not exist, it is created and returned.
     *
     * @param root Parent node.
     * @param name Child node's name.
     * @return Element
     */
    public static Element getElement(Element root, String name)
    {
        return 
            XOMUtil.getFirstChildElement(
                root, 
                name, 
                new  Element(name));
    }
}