package toolbox.util.typecast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import toolbox.util.collections.LRUMap;

/**
 * A class used for seaching finding other classes based on the 
 * a FROM class, a TO class, and prefix.
 * 
 * The packages and class names of the FROM and TO classes are combined
 * to search for a corresponding class with the provided prefix.
 * 
 * As an example:
 * <pre>
 * // Define Transposition of app and view
 * ClassFinder finder = new ClassFinder( "app", "view" );
 *
 * // Find a Class which is a RequestReader for a CustomList with the prefix UE
 * Class aClass = finder.findClass( com.xyz.app.testing.CustomList.class,
 *                           com.xyz.common.view.RequestReader.class, "UE" );
 *
 * // The finder will look for the following classes
 * // NOTE:  looking in FROM class packages ( app -> view )
 * // 	com.xyz.view.testing.UECustomListRequestReader
 * // 	com.xyz.view.testing.UEAbstractListRequestReader
 * // 	com.xyz.view.testing.UEAbstractCollectionRequestReader
 * // 	com.xyz.view.testing.UEObjectRequestReader
 * // NOTE: looking in TO classes package
 * // 	com.xyz.common.view.UECustomListRequestReader
 * // 	com.xyz.common.view.UEAbstractListRequestReader
 * // 	com.xyz.common.view.UEAbstractCollectionRequestReader
 * // 	com.xyz.common.view.UEObjectRequestReader
 * 
 * // NOTE: look again with no prefix
 * 
 * </pre>
 *
 * If a class in not found then ClassNotFoundException will be thrown
 * within the <tt>findClass()</tt>.
 */
public class ClassFinder
{
    protected Set additionalSearchPackages = new HashSet(10);
    /** Create an LRUMap with a max size of 10000 and a timelimit of 60 minutes */
    protected Map cache =
        Collections.synchronizedMap(new LRUMap(1000, 60 * 60000));
    protected String search, replace;

    public ClassFinder()
    {
    }

    public ClassFinder(String search, String replace)
    {
        this.search = search;
        this.replace = replace;
    }

    // API

    public void addSearchPackage(String pckgName)
    {
        additionalSearchPackages.add(pckgName);
    }

    public void addSearchPackage(Class aClassInPackage)
    {
        additionalSearchPackages.add(getPackageName(aClassInPackage));
    }

    public Class findClass(Class fromClass, Class toClass)
        throws ClassNotFoundException
    {
        return findClass(fromClass, toClass, null);
    }

    public Class findClass(Class fromClass, Class toClass, String prefix)
        throws ClassNotFoundException
    {

        return findClass(
            fromClass,
            toClass,
            prefix,
            Thread.currentThread().getContextClassLoader());
    }

    public Class findClass(
        Class fromClass,
        Class toClass,
        String prefix,
        ClassLoader loader)
        throws ClassNotFoundException
    {

        // Check Cache
        Class fClass = checkCache(fromClass, toClass, prefix, loader);

        if (fClass != null)
            return fClass;

        String toClassName = getClassName(toClass);
        List fromClassNames = getClassNames(fromClass);
        List allPackages = getAllPackages(fromClass, toClass);
        List combinations =
            getCombinations(allPackages, fromClassNames, toClassName, prefix);

        for (Iterator i = combinations.iterator(); i.hasNext();)
        {
            try
            {
                String className = i.next().toString();

                //System.err.println( className );
                Class clazz = loader.loadClass(className);
                if (toClass.isAssignableFrom(clazz))
                {
                    putCache(
                        fromClass,
                        toClass,
                        prefix,
                        loader,
                        clazz.getName());
                    return clazz;
                }
            }
            catch (ClassNotFoundException ignore)
            {
            }
        }

        throw new ClassNotFoundException(
            "Unable to find '"
                + toClass.getName()
                + "' for '"
                + fromClass.getName()
                + "'");
    }

    // HELPER METHODS

    protected Class checkCache(
        Class fromClass,
        Class toClass,
        String prefix,
        ClassLoader loader)
        throws ClassNotFoundException
    {

        String className =
            (String) cache.get(getCacheKey(fromClass, toClass, prefix, loader));

        if (className != null)
            return loader.loadClass(className);

        return null;
    }

    protected Object getCacheKey(
        Class fromClass,
        Class toClass,
        String prefix,
        ClassLoader loader)
    {
        return fromClass.getName()
            + toClass.getName()
            + (prefix == null ? "" : prefix)
            + System.identityHashCode(loader);
    }

    protected void putCache(
        Class fromClass,
        Class toClass,
        String prefix,
        ClassLoader loader,
        String className)
    {

        cache.put(getCacheKey(fromClass, toClass, prefix, loader), className);
    }

    /**
     * Combine all of the formClass packages and their heirarchy
     * along with the toClass heirarchy
     */
    protected List getAllPackages(Class fromClass, Class toClass)
    {
        List allPackages = getPackageNames(getClassHierarchy(fromClass));
        List toPackages = getPackageNames(getClassHierarchy(toClass));

        toPackages.removeAll(allPackages);
        allPackages.addAll(toPackages);
        allPackages.addAll(this.additionalSearchPackages);

        return allPackages;
    }

    protected static List getClassHierarchy(Class aClass)
    {
        List classes = new ArrayList(20);

        classes.add(aClass);

        while ((aClass = aClass.getSuperclass()) != null)
            classes.add(aClass);

        return classes;
    }

    protected List getCombinations(
        List packages,
        List fromClassNames,
        String toClassName,
        String prefix)
    {

        List classNames =
            new ArrayList(packages.size() * fromClassNames.size());

        for (Iterator i = packages.iterator(); i.hasNext();)
        {
            String pckg = i.next().toString();

            for (Iterator j = fromClassNames.iterator(); j.hasNext();)
            {
                StringBuffer sb = new StringBuffer();

                sb.append(pckg);
                sb.append('.');

                if (prefix != null)
                    sb.append(prefix);

                sb.append(j.next());
                sb.append(toClassName);
                transpose(sb);
                classNames.add(sb);
            }
        }

        return classNames;
    }

    protected List getPackageNames(List classes)
    {

        List pckgs = new ArrayList(classes.size());

        for (Iterator i = classes.iterator(); i.hasNext();)
        {
            String pckgName = getPackageName((Class) i.next());

            if (!pckgs.contains(pckgName) && !pckgName.startsWith("java"))
                pckgs.add(pckgName);
        }

        return pckgs;
    }

    protected void transpose(StringBuffer name)
    {
        if (search == null || replace == null)
            return;

        int index = name.toString().indexOf(search);

        if (index >= 0)
            name.replace(index, index + search.length(), replace);
    }

    // STATIC HELPER METHODS

    public static String getPackageName(Class aClass)
    {
        Package pckg = aClass.getPackage();
        return pckg == null ? removePackage(aClass.getName()) : pckg.getName();
    }

    public static String capitalize(String name)
    {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public static String getClassName(Class aClass)
    {
        String name = aClass.getName();
        int index = name.lastIndexOf('.');

        return index > 0 ? name.substring(index + 1) : name;
    }

    protected static String removePackage(String name)
    {
        int index = name.lastIndexOf('.');

        return index > 0 ? name.substring(0, index) : name;
    }

    protected static List getClassNames(Class aClass)
    {
        List classes = getClassHierarchy(aClass);
        List names = new ArrayList(classes.size());

        for (Iterator i = classes.iterator(); i.hasNext();)
        {
            String name = getClassName((Class) i.next());

            if (!names.contains(name))
                names.add(name);
        }

        return names;
    }

}