package toolbox.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.io.filter.ExtensionFilter;

/**
 * Class related utility methods that complements
 * {@link org.apache.commons.lang.ClassUtils}.
 * 
 * @see org.apache.commons.lang.ClassUtils
 */
public final class ClassUtil
{
    private static final Logger logger_ = Logger.getLogger(ClassUtil.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction of this static singleton.
     */
    private ClassUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Retrieves the names of all classes in a given package. Doesn't work under
     * WebStart.
     * 
     * @param packageName Name of package to search.
     * @return Array of fully qualified class names in the package. Empty array
     *         if no classes are found.
     */
    public static String[] getClassesInPackage(String packageName)
    {
        // Collect results here
        Set collector = new HashSet();
        
        String packageForward  = packageName.replace('.', '/');
        String packageBackward = packageName.replace('.', '\\');
        String packageDir      = packageName.replace('.', File.separatorChar);

        // Tokenize the classpath and iterate over
        String[] classpathEntries = getClassPathElements();
       
        // logger_.info("Classpath: " + 
        //      ArrayUtil.toString(classpathEntries, false));
        
        for (int i = 0; i < classpathEntries.length; i++)
        {
            String pathElement = classpathEntries[i];
        
            // Identify archives    
            if (isArchive(pathElement))
            {
                ZipFile zipFile = null;
                
                try
                {
                    zipFile = new ZipFile(pathElement);

                    Enumeration zipFiles = zipFile.entries();
                    
                    while (zipFiles.hasMoreElements())
                    {
                        ZipEntry zipEntry = (ZipEntry) zipFiles.nextElement();
                        
                        String filename = zipEntry.getName();
                        
                        if ((filename.startsWith(packageBackward) || 
                             filename.startsWith(packageForward)) &&
                            (isClassFile(filename)))
                        {
                            // Replace slashes and trunc .class
                            filename = pathToPackage(filename);
                            filename = FileUtil.dropExtension(filename);
                                    
                            // Add to collector    
                            //logger_.info("Added " + filename + "from zip " + 
                            //      pathElement);    
                            collector.add(filename);
                        }
                    }
                }
                catch (IOException ex)
                {
                    logger_.warn(
                        "Problem with archive " + pathElement + 
                        " : " + ex.getMessage());
                }
            }
            else
            {
                pathElement = FileUtil.trailWithSeparator(pathElement);
                
                // Build expected dir based on pathElement and package directory
                File file = new File(pathElement + packageDir);
                
                // If directory exists
                if (file.exists() && file.isDirectory())
                {
                    // Filter out only the class files
                    File[] classnames = 
                        file.listFiles(new ExtensionFilter(".class"));
                    
                    for (int j = 0; j < classnames.length; j++)
                    {
                        // Replace slashes and trunc .class 
                        String classname = 
                            packageName + "." + classnames[j].getName();
                            
                        classname = pathToPackage(classname);
                        classname = FileUtil.dropExtension(classname);
                        
                        //logger_.info(
                        //  "Added " + classname + "from path " + pathElement);
                        
                        collector.add(classname);
                    }
                }
            }
            

        }
        return (String[]) collector.toArray(new String[0]);
    }

    
    /**
     * Returns a list of all known packages in the classpath that contain
     * class files.
     * 
     * @return Array of fully qualified package names.
     */    
    public static String[] getPackagesInClasspath()
    {
        // Collect results here
        Set collector = new HashSet();

        // Tokenize the classpath and iterate over
        String[] classpathEntries = 
            StringUtil.tokenize(getClasspath(),  File.pathSeparator);
        
        for (int i = 0; i < classpathEntries.length; i++)
        {
            String pathElement = classpathEntries[i];
        
            // Identify archives    
            if (isArchive(pathElement))
            {
                ZipFile zipFile = null;
                
                try
                {
                    zipFile = new ZipFile(pathElement);
                
                    Enumeration zipFiles = zipFile.entries();
                    
                    while (zipFiles.hasMoreElements())
                    {
                        ZipEntry zipEntry  = (ZipEntry) zipFiles.nextElement();
                        String   entryName = zipEntry.getName();
                        
                        // Find classfiles and then tag parent dir as a package
                        if (!zipEntry.isDirectory() && isClassFile(entryName))
                        {
                            String packageName = 
                                FileUtil.dropExtension(entryName);
                                
                            packageName = pathToPackage(packageName);
                            
                            // Chop off the filename to get just the package
                            int dot = packageName.lastIndexOf(".");
                            
                            if (dot != -1)
                                packageName = packageName.substring(0, dot);
                            
                            // Add to set, don't worry about dupes   
                            collector.add(packageName);
                        }
                    }
                }
                catch (IOException ioe)
                {
                    logger_.info(
                        "Problem with archive " + pathElement + 
                        " : " + ioe.getMessage());
                }
            }
            else
            {
                // Normalize path
                pathElement = FileUtil.matchPlatformSeparator(pathElement);
                pathElement = FileUtil.trailWithSeparator(pathElement);
                
                // Get all class files       
                List classFiles = FileUtil.findFiles(
                    pathElement, new ExtensionFilter(".class"));
                       
                // Roundup parent directories and tag as package names
                for (Iterator c = classFiles.iterator(); c.hasNext();)
                {
                    String filepath = (String) c.next();

                    // Prune pathelement from the front + 1 for separator
                    filepath = filepath.substring(pathElement.length());
                    
                    int sep = filepath.lastIndexOf(File.separator);
                    
                    // Prune file name
                    if (sep != -1)
                        filepath = filepath.substring(0, sep);
                    
                    // Turn into package
                    String packageName = pathToPackage(filepath);
                    
                    collector.add(packageName);
                }    
            }                    
        }
    
        List sorted = new ArrayList(collector);
        Collections.sort(sorted);
        
        return (String[]) sorted.toArray(new String[0]);
    }
    
    
    /**
     * Converts a file path into a package name.
     * 
     * @param path File path.
     * @return Fully qualified package name.
     */
    public static String pathToPackage(String path)
    {
        String packageName = 
            StringUtils.strip(path.replace('/', '.').replace('\\', '.'), ".");
            
        return packageName;
    }
    
    
    /**
     * Converts a package name into a file path.
     * 
     * @param packageName Package name.
     * @return File path.
     */
    public static String packageToPath(String packageName)
    {
        return packageName.replace('.', File.separatorChar);        
    }
    
    
    /**
     * Determines if a files name indicates a java archive. This includes zip
     * and jar file types.
     * 
     * @param filename File to examine.
     * @return True if the name is a valid java archive, false otherwise.
     */
    public static boolean isArchive(String filename)
    {
        String f = filename.toLowerCase().trim();
        
        return 
            (f.endsWith(".zip") || 
             f.endsWith(".jar") || 
             f.endsWith(".ear"));        
    }

    
    /**
     * Returns true if the filename indicates a java class file.
     * 
     * @param filename File to examine.
     * @return True if a class file, false otherwise.
     */
    public static boolean isClassFile(String filename)
    {
        return filename.trim().toLowerCase().endsWith(".class");    
    }

    
    /**
     * Returns the complete system classpath separated by pathSeparator.
     * 
     * @return String
     */
    public static String getClasspath()
    {
        String bootpath  = System.getProperty("sun.boot.class.path");
        String classpath = System.getProperty("java.class.path");
        
        if (bootpath != null)
            classpath = bootpath + File.pathSeparator + classpath;
        
        return classpath;           
    }

    
    /**
     * Returns an array of all elements on the current classpath.
     * 
     * @return String[]
     */
    public static String[] getClassPathElements()
    {
        return StringUtils.split(getClasspath(), File.pathSeparator);
    }
    
    
    /**
     * Returns array of Class object matching the types for the passed in 
     * params.
     * 
     * @param params Array of objects. 
     * @return Array of Class objects.
     */    
    public static Class[] getMatchingClasses(Object[] params)
    {
        Class[] ca = new Class[0];
            
        if (params != null && params.length > 0)
        {
            ca = new Class[params.length];
            
            for (int i = 0; i < params.length; i++)
                ca[i] = params[i].getClass();
        }
            
        return ca;
    }
    
    
    /**
     * Given a Class object, attempts to find its .class location [returns null
     * if no such definition can be found]. Use for testing/debugging only.
     *
     * @param cls Class to find.
     * @return URL that points to the class definition or null if not found.
     */
    public static URL getClassLocation(Class cls)
    {
        URL result = null;
        String clsAsResource = cls.getName().replace('.', '/').concat(".class");
        ProtectionDomain pd = cls.getProtectionDomain();
        CodeSource cs = pd.getCodeSource();
        
        // 'cs' can be null depending on the classloader behavior:
        if (cs != null)
            result = cs.getLocation();

        if (result != null)
        {
            // Convert a code source location into a full class file 
            // location for some common cases:
            
            if ("file".equals(result.getProtocol()))
            {
                try
                {
                    if (result.toExternalForm().endsWith(".jar") || 
                        result.toExternalForm().endsWith(".zip"))
                    {    
                        result = new URL(
                            "jar:".concat(result.toExternalForm()).
                                concat("!/").concat(clsAsResource));
                    }
                    else if (new File(result.getFile()).isDirectory())
                    {
                        result = new URL(result, clsAsResource);
                    }
                }
                catch (MalformedURLException ignore)
                {
                    ; // Ignore
                }
            }
        }

        if (result == null)
        {
            // Try to find 'cls' definition as a resource; this is not
            // documented to be legal, but Sun's implementations seem to allow 
            // this:
            
            ClassLoader clsLoader = cls.getClassLoader();

            result = clsLoader != null 
                        ? clsLoader.getResource(clsAsResource)
                        : ClassLoader.getSystemResource(clsAsResource);
        }

        return result;
    }
}