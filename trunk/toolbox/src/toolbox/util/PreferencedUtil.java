package toolbox.util;

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
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction of this utility class.
     */
    private PreferencedUtil()
    {
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Reads properties from a javabean and inserts them into an XML node
     * as atttributes.
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
                Attribute attr = 
                    new Attribute(
                        propName, 
                        //PropertyUtils.getProperty(bean, propName).toString());
                        BeanUtils.getProperty(bean, propName).toString());
                
                node.addAttribute(attr);
            }
        }
    }

    
    /**
     * Reads attributes from an XML node and applies them to a javabean as
     * properties.
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
}