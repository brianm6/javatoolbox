package toolbox.util.dump;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import toolbox.util.ClassUtil;
import toolbox.util.StringUtil;

/**
 * Basic implementation of the {@link DumpFormatter} interface
 */
public class BasicDumpFormatter implements DumpFormatter
{
    /** 
     * Classes that are excluded from the object dump 
     */
    private List excludedClasses_;

    /** 
     * Fields that are excluded from the object dump 
     */
    private List excludedFields_;

    /** 
     * Controls stripping of the package when printing out a classes' name 
     */
    private boolean stripPackage_;

    /** 
     * Flag to show inheritance tree for each object that is traversed
     */
    private boolean showInheritance_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Default constructor
     * 
     * @throws  RESyntaxException on regular expression error
     */
    public BasicDumpFormatter() throws RESyntaxException
    {
        excludedClasses_ = new ArrayList();
        excludedFields_  = new ArrayList();

        excludeClass(String.class);
        excludeFields("^this");
        
        setStripPackage(true);
        setShowInheritance(false);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Excludes a class from the object dump
     *
     * @param   clazz  Class to exclude
     * @throws  RESyntaxException on regular expression error 
     */
    public void excludeClass(Class clazz) throws RESyntaxException
    {
        excludedClasses_.add(new RE("^" + clazz.getName() + "$"));
    }

    /**
     * Excludes one or more classes matching a regular expression from the
     * object dump.
     *
     * @param   classFilter  Regular expression representing classes to exclude
     * @throws  RESyntaxException on regular expression error 
     */
    public void excludeClasses(String classFilter) throws RESyntaxException
    {
        excludedClasses_.add(new RE(classFilter));
    }

    /**
     * Excludes one or more fields matching a regular expression from the
     * object dump
     *
     * @param   fieldFilter  Regular expression represeting fields names to
     *                       exclude
     * @throws  RESyntaxException on regular expression error 
     */
    public void excludeFields(String fieldFilter) throws RESyntaxException
    {
        excludedFields_.add(new RE(fieldFilter));
    }

    /**
     * Allows stripping of the package name when printing out a classes 
     * fully qualified name.
     * 
     * @param  b  If true, package will be choped from a class name, otherwise 
     *            the FQN will be used.
     */
    public void setStripPackage(boolean b)
    {
        stripPackage_ = b;
    }
    
    /**
     * Flag to set the display of the class inheritance hierarchy
     * 
     * @param  b  If true, show inheritance hierarchy, otherwise just print
     *            the name of the current class in the hierarchy.
     */
    public void setShowInheritance(boolean b)
    {
        showInheritance_ = b;
    }
    
    //--------------------------------------------------------------------------
    // DumpFormatter Interface
    //--------------------------------------------------------------------------

    public boolean shouldInclude(Class clazz)
    {
        String name = clazz.getName();
        
        for (Iterator i = excludedClasses_.iterator(); i.hasNext(); )
        {
            RE regExp = (RE) i.next();
            
            if (regExp.match(name))
                return false;
        }
        
        return true;
    }

    public boolean shouldInclude(Field field)
    {
        String name = field.getName();

        // Skip static fields
        if (Modifier.isStatic(field.getModifiers()))
            return false;

        for (Iterator i = excludedFields_.iterator(); i.hasNext(); )
        {
            // Skip fields that match the regular expression
            RE regExp = (RE) i.next();
            if (regExp.match(name))
                return false;
        }

        return true;
    }
    
    public String formatClassName(Class clazz)
    {
        return formatClassName(clazz.getName());
    }

    public String formatClassName(String className)
    {
        return (stripPackage_ ? ClassUtil.stripPackage(className) : className);
    }

    public boolean showInheritance()
    {
        return showInheritance_;
    }
    
    public boolean sortFields()
    {
        return true;
    }
    
    public String formatFieldName(String fieldName)
    {
        //
        // Strip leading and trailing underscores
        //
        
        return StringUtil.trim(fieldName, '_');
    }

}