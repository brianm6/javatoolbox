package toolbox.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import toolbox.util.io.filter.ExtensionFilter;

/**
 * Class related utility methods
 */
public class ClassUtil
{
    private static final Logger logger_ = 
        Logger.getLogger(ClassUtil.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction
     */
    private ClassUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Retrieves the names of all classes in a given package
     *
     * @param  packageName  Name of package to search
     * @return Array of fully qualified class names in the package.
     *         Empty array if no classes are found.
     */
    public static String[] getClassesInPackage(String packageName)
    {
        // Collect results here
        Set collector = new HashSet();
        
        String packageForward  = packageName.replace('.', '/');
        String packageBackward = packageName.replace('.', '\\');
        String packageDir      = packageName.replace('.', File.separatorChar);

        // Tokenize the classpath and iterate over
        String[] classpathEntries = 
            StringUtil.tokenize(getClasspath(), File.pathSeparator);
       
        // logger_.info("Classpath: " + 
        //      ArrayUtil.toString(classpathEntries, false));
        
        for (int i=0; i<classpathEntries.length; i++)
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
                    logger_.info(
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
     * class files
     * 
     * @return  Array of fully qualified package names
     */    
    public static String[] getPackagesInClasspath()
    {
        // Collect results here
        Set collector = new HashSet();

        // Tokenize the classpath and iterate over
        String[] classpathEntries = 
            StringUtil.tokenize(getClasspath(),  File.pathSeparator);
        
        for (int i=0; i<classpathEntries.length; i++)
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
                List classFiles = FileUtil.findFilesRecursively(
                    pathElement, new ExtensionFilter(".class"));
                       
                // Roundup parent directories and tag as package names
                for (Iterator c = classFiles.iterator(); c.hasNext(); )
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
     * Converts a file path into a package name
     * 
     * @param  path  File path
     * @return Fully qualified package name
     */
    public static String pathToPackage(String path)
    {
        String packageName = path.replace('/', '.').replace('\\', '.');
        
        if (packageName.endsWith("."))
            packageName = packageName.substring(0, packageName.length() -1);
            
        return packageName;
    }
    
    /**
     * Converts a package name into a file path
     * 
     * @param  packageName  Package name
     * @return File path
     */
    public static String packageToPath(String packageName)
    {
        return packageName.replace('.', File.separatorChar);        
    }
    
    /**
     * Determines if a files name indicates a java archive. This includes
     * zip and jar file types.
     * 
     * @param   filename  File to examine
     * @return  True if the name is a valid java archive, false otherwise
     */
    public static boolean isArchive(String filename)
    {
        filename = filename.toLowerCase().trim();
        return (filename.endsWith(".zip") || filename.endsWith(".jar"));        
    }

    /**
     * Returns true if the filename indicates a java class file
     * 
     * @param  filename  File to examine
     * @return True if a class file, false otherwise
     */
    public static boolean isClassFile(String filename)
    {
        return filename.trim().toLowerCase().endsWith(".class");    
    }
    
    /**
     * @return  The complete system classpath
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
     * Strips package name from a fully qualified class name
     * 
     * @param   fqn  Fully qualified class name
     * @return  Name of class only
 	 */
    public static String stripPackage(String fqn)
    {
        int idx = fqn.lastIndexOf('.');
        
        if (idx >= 0)
            return fqn.substring(idx+1);
        else
            return fqn;
    }
}