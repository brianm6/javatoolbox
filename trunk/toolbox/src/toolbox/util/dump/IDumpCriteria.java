package toolbox.util.dump;

import java.lang.reflect.Field;

/**
 * IDumper
 */
public interface IDumpCriteria
{
    public boolean includeClass(Class clazz);
    
    public boolean includeField(Field field);
    
    public boolean showInheritance();
    
    public String  formatClass(String className);
    
    public String  formatClass(Class clazz);
    
    public boolean sortFields();
}
