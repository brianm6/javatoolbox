
package toolbox.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * BeanUtil is responsible for ___.
 */
public class BeanUtil
{
    /**
     * dumps the properties names and values of a bean into a string
     * 
     * @param bean the JavaBean to be intropected
     * @return String a dump of the property names and values
     */
    public static String toString(Object bean)
    {
        StringBuffer sb = new StringBuffer();
        
        if (bean != null)
        {
            try
            {
                BeanInfo info = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] properties = info.getPropertyDescriptors();
                if (properties != null)
                {
                    for (int i = 0; i < properties.length; i++)
                    {
                        Method readMethod = properties[i].getReadMethod();
                        
                        if (readMethod != null)
                        {
                            sb.append(properties[i].getName());
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
            }
            catch (Exception e)
            {
                ; // ignore
            }
        }
        
        return sb.toString();
    }
}