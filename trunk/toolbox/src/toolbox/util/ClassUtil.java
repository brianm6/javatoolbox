package toolbox.util;

import java.io.File;
import java.io.FileFilter;
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
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.io.filter.RegexFileFilter;

/**
 * Class related utility methods that complements
 * {@link org.apache.commons.lang.ClassUtils}.
 */
public final class ClassUtil
{
    private static final Logger logger_ = Logger.getLogger(ClassUtil.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Filter for class files.
     */
    private static final IOFileFilter FILTER_CLASS = new RegexFileFilter(".class$", false);
    
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
                            filename = FilenameUtils.removeExtension(filename);
                                    
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
                        file.listFiles((FileFilter) FILTER_CLASS);
                    
                    for (int j = 0; j < classnames.length; j++)
                    {
                        // Replace slashes and trunc .class 
                        String classname = 
                            packageName + "." + classnames[j].getName();
                            
                        classname = pathToPackage(classname);
                        classname = FilenameUtils.removeExtension(classname);
                        
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
                                FilenameUtils.removeExtension(entryName);
                                
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
                    logger_.info("Problem with archive " + pathElement + " : " + ioe.getMessage());
                }
            }
            else
            {
                // Normalize path
                pathElement = FilenameUtils.separatorsToSystem(pathElement);
                pathElement = FileUtil.trailWithSeparator(pathElement);
                
                // Get all class files       
                List classFiles = FileUtil.find(pathElement, FILTER_CLASS);
                       
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
     * <p>
     * <b>Example</b>
     * <pre class="snippet">
     *   packageToPath("a.b")         => a/b         (path separator is '/')
     *   packageToPath("com.foo.bar") => com\foo\bar (path separaror is '\')
     * </pre>
     * 
     * @param packageName Package name.
     * @return String
     */
    public static String packageToPath(String packageName)
    {
        return packageName.replace('.', File.separatorChar);        
    }
    
    
    /**
     * Determines if a file's name indicates that is a java archive. This 
     * includes files with the following extensions. Returns true if the name 
     * is a valid java archive, false otherwise.
     * <ul>
     *   <li>zip
     *   <li>jar
     *   <li>ear
     *   <li>war
     * </ul>
     * 
     * @param filename File to examine.
     * @return boolean 
     */
    public static boolean isArchive(String filename)
    {
        String f = filename.toLowerCase().trim();
        
        return 
            (f.endsWith(".zip") || 
             f.endsWith(".jar") || 
             f.endsWith(".ear") ||
             f.endsWith(".war"));        
    }

    
    /**
     * Returns true if the filename indicates a java class file. The file is
     * not checked for content.
     * 
     * @param filename File to examine.
     * @return True if the name indicates a class file, false otherwise.
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
        Class[] classes = new Class[0];
            
        if (!ArrayUtil.isNullOrEmpty(params))
        {
            classes = new Class[params.length];
            
            for (int i = 0; i < params.length; i++)
                classes[i] = params[i].getClass();
        }
            
        return classes;
    }
    
    
    /**
     * Given a Class object, attempts to find its .class location [returns null
     * if no such definition can be found]. Use for testing/debugging only.
     *
     * @param clazz Class to find.
     * @return URL that points to the class definition or null if not found.
     */
    public static URL getClassLocation(Class clazz)
    {
        URL result = null;
        
        String clazzAsResource = 
            clazz.getName().replace('.', '/').concat(".class");
        
        ProtectionDomain pd = clazz.getProtectionDomain();
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
                    // Embedded in an archive
                    
                    if (isArchive(result.toExternalForm()))
                    {    
                        result = new URL(
                            "jar:".concat(result.toExternalForm()).
                                concat("!/").concat(clazzAsResource));
                    }
                    else if (new File(result.getFile()).isDirectory())
                    {
                        result = new URL(result, clazzAsResource);
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
            // Try to find 'clazz' definition as a resource; this is not
            // documented to be legal, but Sun's implementations seem to allow 
            // this:
            
            ClassLoader loader = clazz.getClassLoader();

            result = loader != null 
                        ? loader.getResource(clazzAsResource)
                        : ClassLoader.getSystemResource(clazzAsResource);
        }

        return result;
    }
    
    
    /**
     * Finds a file if it exists on the system's executable PATH. If there are
     * multiple occurrences in the path, the first one encountered in left
     * to right order in the path is returned. If the file is not found in the
     * path, a null is returned.
     * <p>
     * <b>Example</b>
     * <pre class="snippet">
     *   Assuming the PATH = c:\;c:\windows;c:\bin;
     *   and some.exe exists in c:\bin
     *   findInPath("some.exe") will return c:\bin\some.exe 
     * </pre>
     * @param file Executable file to find.
     * @return File
     */
    public static File findInPath(String file)
    {
        // TODO: Move to a more system related utility class
        
        File result = null;
        String path = null;
        
        if (Platform.isUnix())
        {
            Properties env = new Properties();
            
            try
            {
                env.load(Runtime.getRuntime().exec("env").getInputStream());
            }
            catch (IOException e)
            {
                logger_.error(e);
            }
            
            path = env.getProperty("PATH");
        }
        else
        {
            path = System.getProperty("java.library.path");
        }
        
        StringTokenizer st = 
            new StringTokenizer(
                path, 
                System.getProperty("path.separator"));

        while (st.hasMoreElements())
        {
            String pathElement = st.nextToken();
            String trying = FileUtil.trailWithSeparator(pathElement) + file;
            
            //logger_.debug("Trying " + trying);
            
            File f = new File(trying);
            if (f.exists())
            {
                result = f;
                break;
            }
        }

        return result;
    }

    
    /**
     * Returns true if the name of the given class indicates that it is an
     * inner class, false otherwise.
     * <p>
     * <b>Example</b>
     * <pre class="snippet">
     *   isInnerClass("Widget$Listener")     ==> true
     *   isInnerClass("Car$Engine$Cylinder") ==> true
     *   isInnerClass("ThingyBob$1")         ==> true
     *   isInnerClass("a$b")                 ==> true
     * </pre>
     * 
     * @param clazz Name of the class in FCQN or shortened form without the file
     *        extension.
     * @return boolean
     */
    public static boolean isInnerClass(String clazz)
    {
        int idx = clazz.lastIndexOf("$");
        
        return (clazz.length() > 2) &&     // a$b is shortest inner class 
               (idx > 0) &&                // separator can't be first char
               (idx < clazz.length() - 1); // separator can't be the last char
    }
    
    
    /**
     * Takes an array of arbitrary objects and returns an array containing the
     * <code>Class</code> type of each <code>Object<code>.
     * <p>
     * If the array of objects is null or the array is empty, an empty array of 
     * type <code>Class</code> is returned.
     * <p>
     * <b>Example</b>
     * <pre class="snippet">
     *   Object[] objs = new Object[] { "Hello", new ArrayList(), new Date()};
     *   Class[] types = ClassUtil.toClass(objs);
     *   for (int i = 0; i < types.length; System.out.println(types[i++]);
     * </pre>
     * <b>Output</b>
     * <pre class="snippet">
     *   java.lang.String
     *   java.util.ArrayList
     *   java.util.Date 
     * </pre>
     * 
     * @param objs Array of objects to retrieve the classes for.
     * @return Class[]
     */
    public static Class[] toClass(Object[] objs)
    {
        if (objs == null || objs.length == 0)
            return new Class[0];
        
        Class[] result = new Class[objs.length];
        
        for (int i = 0; i < objs.length; i++)
            result[i] = objs[i].getClass();
        
        return result;
    }
}