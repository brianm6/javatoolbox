package toolbox.util.dump;

import java.lang.reflect.Field;

/**
 * DumpFormatter defines the configurable dump and formatting options 
 * recognized by {@link Dumper}.
 */
public interface DumpFormatter
{
    /**
     * Should the given class be included in the dump?
     * 
     * @param   clazz    Class to test for inclusion
     * @return  boolean  True if class should be included, false otherwise
     */
    public boolean shouldInclude(Class clazz);

    /**
     * Should the given field be included in the dump?
     * 
     * @param   field    Field to test for inclusion
     * @return  boolean  True if field should be included, false otherwise
     */
    public boolean shouldInclude(Field field);
    
    /**
     * Returns true if the inheritance tree should be shown for each object
     * traversed, false otherwise.
     * 
     * @return Inheritance flag
     */
    public boolean showInheritance();
    
    /**
     * Formats the presentation of a classes' name
     * 
     * @param   className  Class name to format
     * @return  Formatted class name
     */
    public String  formatClassName(String className);
    
    /**
     * Formats the presentation of a classes' name
     * 
     * @param   clazz  Class name to format
     * @return  Formatted classname
     */
    public String  formatClassName(Class clazz);
    
    /**
     * Returns true if the fields in a class should be sorted alphabetically,
     * false otherwise.
     * 
     * @return Sort fields flag
     */
    public boolean sortFields();
    
    /**
     * Formats a field name.
     * 
     * @return  Formatted field name
     */
    public String formatFieldName(String fieldName);
}