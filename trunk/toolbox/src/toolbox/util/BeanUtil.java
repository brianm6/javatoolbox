
package toolbox.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Javabean Utilities.
 */
public class BeanUtil
{
    /**
     * Dumps the properties names and values of a bean into a string.
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
}