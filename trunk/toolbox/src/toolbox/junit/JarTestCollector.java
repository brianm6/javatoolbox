package toolbox.junit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.runner.TestCollector;

/**
 * Test collector that will find JUnit tests embeded in jar/zip
 * files on the classpath
 */
public class JarTestCollector implements TestCollector
{

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /** 
     * Default constructor 
     */
    public JarTestCollector()
    {
    }

    //--------------------------------------------------------------------------
    //  TestCollector Interface
    //--------------------------------------------------------------------------
    
    /**
     * @return Enumeration of classnames identified as JUnit tests
     */
    public Enumeration collectTests()
    {
        String    classPath = System.getProperty("java.class.path");
        Vector    result    = new Vector();
        List      archives  = getArchives(classPath);
        collectFilesInJars(archives, result);
        return result.elements();
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Retrieves list of classpath path entries that are jars
     * 
     * @param   classPath   Classpath to search for archives
     * @return  List of classpath path entries that are jars
     */
    protected List getArchives(String classPath) 
    {
        List result = new ArrayList();
        
        StringTokenizer tokenizer = new StringTokenizer(classPath, 
            System.getProperty("path.separator"));
        
        while (tokenizer.hasMoreTokens()) 
        {   
            String entry = (String)tokenizer.nextToken();
            if (isArchive(entry))
                result.add(entry);
        }
            
        return result;
    }


    /**
     * Determines whether a given file is a java archive or not
     * 
     * @param   s   absolute name of the java archive
     * @return      true if a valid archive, false otherwise
     */
    protected boolean isArchive(String s) 
    { 
        s = s.toUpperCase();
        if (s.endsWith(".JAR") || s.endsWith(".ZIP"))
            return true;
        else
            return false;
    }
    
    
    /**
     * Collects test classes in a jar/zip file
     * 
     * @param  archives   List of archive files
     * @param  result     Running list of test files that where found
     */
    protected void collectFilesInJars(List archives, List result)
    {
        for(Iterator i=archives.iterator(); i.hasNext(); )
        {
            String archive = (String)i.next();    
            System.out.println("Archive=" + archive);
            
            try
            {
                findInArchive(archive, result);
            }
            catch(IOException e)
            {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Finds all classes in a given jar file and tests for criteria matching
     * a JUnit test case
     * 
     * @param   jarName     The name of the jar file to search
     * @param   result      Running list of test files that were found
     * @throws  IOException on error
     */
    protected void findInArchive(String jarName, List result) 
        throws IOException
    { 
        ZipFile zf = null;

        try 
        { 
            zf = new ZipFile(jarName);
        }
        catch (Exception e) 
        { 
            System.err.println("Error: Could not locate " + jarName + ".");
            e.printStackTrace();
            return;
        }
        
        for (Enumeration e = zf.entries(); e.hasMoreElements();) 
        { 
            ZipEntry ze = (ZipEntry) e.nextElement();

            if (!ze.isDirectory() &&  ze.getName().endsWith("Test.class")) 
            { 
                String classname = ze.getName().replace('/', '.');
                
                classname = classname.substring(0, 
                    classname.length() - ".class".length());

                result.add(classname);                
            }
        }
        zf.close();
    }
}