
package toolbox.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Javabean Utilities.
 */
public class BeanUtil
{
    /**
     * Dumps the properties names and values of a bean into a string.
     * <p>
     * <b>Example:</b>
     * <pre class="snippet">
     * System.out.println(BeanUtil.toString(new Socket()));
     * </pre>
     * 
     * <b>Output:</b>
     * <pre class="snippet">
     * OOBInline = false
     * bound = false
     * channel = [empty]
     * class = class java.net.Socket
     * closed = false
     * connected = false
     * inetAddress = [empty]
     * inputShutdown = false
     * inputStream = 
     * </pre>
     * 
     * @param bean JavaBean to be introspected.
     * @return String dump of the property names and values.
     */
    public static String toString(Object bean)
    {
        StringBuffer sb = new StringBuffer();
        
        if (bean == null)
            return sb.toString();
        
        try
        {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            
            if (props == null)
                return sb.toString();
            
            for (int i = 0; i < props.length; i++)
            {
                Method readMethod = props[i].getReadMethod();
                
                if (readMethod != null)
                {
                    sb.append(props[i].getName());
                    sb.append(" = ");
                    Object obj = readMethod.invoke(bean, null);
                    
                    if (obj != null)
                        sb.append(obj.toString());
                    else
                        sb.append("[empty]");
                    
                    sb.append("\n");
                }
            }
        }
        catch (Exception e)
        {
            ; // ignore
        }
        
        return sb.toString();
    }
    
    /**
     * For all javabean properties of type clazz on a given javabean, sets the 
     * value of those javabean properties to the passed in value.
     * <p>
     * Example:
     * <p> 
     * Assume you have a javabean with 100 {@link Integer} attributes with getters
     * and setters. How do you set all the attributes to the value 2 without
     * calling <code>setAttrX(new Integer(2))</code> a 100 times (assuming you 
     * don't care about the performance overhead of using refletion)?
     * <p>
     * <pre>
     * setAllValuesForType(myBean, Integer.class, new Integer(2));
     * </pre>
     * 
     * @param javabean Javabean to set values on.
     * @param clazz Type of the javabean properties to set.
     * @param value The value to set each property to.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void setAllValuesForType(Object javabean, Class clazz, Object value) 
      throws 
        IllegalArgumentException, 
        IllegalAccessException, 
        InvocationTargetException {
      
      PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(javabean);

      for (int i = 0; i < descriptors.length; i++) {
        PropertyDescriptor pd = descriptors[i];
        if (pd.getPropertyType() == clazz) {
          Method m = pd.getWriteMethod();
          if (m != null)
            pd.getWriteMethod().invoke(javabean, new Object[] {value});
        }
      }
    }    
}