package toolbox.util.dump;

import java.lang.reflect.Field;

/**
 * IDumpFormatter defines the configurable dump and formatting options 
 * exposed by the object dumper.
 */
public interface IDumpFormatter
{
    /**
     * Should the given class be included in the dump?
     * 
     * @param 	clazz	 Class to test for inclusion
     * @return 	boolean  True if class should be included, false otherwise
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
     * @return True if the inheritance tree should be shown for each object
     * 		   traversed.
     */
    public boolean showInheritance();
    
    /**
     * Formats the presentation of a classes' name
     * 
     * @param 	className  Class name to format
     * @return 	Formatted class name
     */
    public String  formatClass(String className);
    
    /**
     * Formats the presentation of a classes' name
     * 
     * @param 	clazz  Class name to format
     * @return 	Formatted classname
     */
    public String  formatClass(Class clazz);
    
    /**
	 * @return	True if the fields in a class should be sorted alphabetically,
     *          false otherwise.
     */
    public boolean sortFields();
}
