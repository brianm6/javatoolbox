package toolbox.util.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;
import toolbox.util.StringUtil;

/**
 * Unit test for ClassUtil
 */
public class ClassUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ClassUtilTest.class);
    
    /**
     * Entrypoint
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(ClassUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for ClassUtilTest.
     * 
     * @param arg0  Name
     */
    public ClassUtilTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests getClassesInPackage() for a package in a file system directory
     */
    public void testGetClassesInPackageDirectory()
    {
        logger_.info("Running testGetClassesInPackageDirectory...");
        
        String[] classes = ClassUtil.getClassesInPackage("toolbox.util");
        logger_.info("\n" + ArrayUtil.toString(classes, true));
        assertTrue(StringUtil.class.getName() + " not found in package", 
            ArrayUtil.contains(classes, StringUtil.class.getName()));
    }
    
    /**
     * Tests getClassesInPackage() for a package in a jar file
     */
    public void testGetClassesInPackageArchive()
    {
        logger_.info("Running testGetClassesInPackageArchive...");
        
        String[] classes = ClassUtil.getClassesInPackage("junit.textui");
        logger_.info("\n"+ArrayUtil.toString(classes, true));
        assertTrue(TestRunner.class.getName() + " not found in package", 
            ArrayUtil.contains(classes, TestRunner.class.getName()));
    }
    
    /**
     * Tests getClassesInPackage() for a package in the boot classpath
     */
    public void testGetClassesInPackageBoot()
    {
        logger_.info("Running testGetClassesInPackageBoot...");
        
        String[] classes = ClassUtil.getClassesInPackage("java.lang");
        logger_.info("\n" + ArrayUtil.toString(classes, true));
        assertTrue(String.class.getName() + " not found in package", 
            ArrayUtil.contains(classes, String.class.getName()));
    }
    
    /**
     * Tests getPackagesInClasspath() 
     */
    public void testGetPackagesInClasspath()
    {   
        logger_.info("Running testGetPackagesInClasspath...");
        
        String[] packages = ClassUtil.getPackagesInClasspath();
        logger_.info("\n" + ArrayUtil.toString(packages, true));
        assertTrue(ArrayUtil.contains(packages, "java.lang"));
        assertTrue(ArrayUtil.contains(packages, "toolbox.util"));
        assertTrue(ArrayUtil.contains(packages, "junit.textui"));
    }
    
    /**
     * Tests getClasspath()
     */
    public void testGetClasspath()
    {
        logger_.info("Running testGetClasspath...");
        
        String classpath = ClassUtil.getClasspath();
        assertNotNull(classpath);
        String[] paths = StringUtil.tokenize(classpath, File.pathSeparator);
        logger_.info("\n" + ArrayUtil.toString(paths, true));
    }
    
    /**
     * Tests isArchive()
     */
    public void testIsArchive()
    {
        logger_.info("Running testIsArchive...");
        
        // Positive tests
        assertTrue(ClassUtil.isArchive("a.jar"));
        assertTrue(ClassUtil.isArchive("b.zip"));
        assertTrue(ClassUtil.isArchive("AFD.JAR"));
        assertTrue(ClassUtil.isArchive("ASDF.ZIP")); 
        assertTrue(ClassUtil.isArchive("a.b.jar"));
        assertTrue(ClassUtil.isArchive("C.D.ZIP"));
                
        // Negative tests        
        assertTrue(!ClassUtil.isArchive(""));
        assertTrue(!ClassUtil.isArchive(" "));
        assertTrue(!ClassUtil.isArchive("jar"));
        assertTrue(!ClassUtil.isArchive("ZIP"));        
        assertTrue(!ClassUtil.isArchive("df.jarx"));
        assertTrue(!ClassUtil.isArchive("df.zipo"));
    }
     
    /**
     * Tests isClassFile()
     */ 
    public void testIsClassFile()
    {
        logger_.info("Running testIsClassFile...");
        
        // Positive tests
        assertTrue(ClassUtil.isClassFile("a.class"));
        assertTrue(ClassUtil.isClassFile("A.CLASS"));
        assertTrue(ClassUtil.isClassFile("asdfasf.class"));
        assertTrue(ClassUtil.isClassFile("abc$123.class")); 
        assertTrue(ClassUtil.isClassFile("abc_123.CLASS"));
        
        // Negative tests        
        assertTrue(!ClassUtil.isArchive(".class"));
        assertTrue(!ClassUtil.isArchive("class "));
        assertTrue(!ClassUtil.isArchive(".class."));
        assertTrue(!ClassUtil.isArchive("a.b.class.x"));        
        assertTrue(!ClassUtil.isArchive("X.CLASS.X"));
    }
    
    /**
     * Tests packageToPath()
     */
    public void testPackageToPath()
    {
        logger_.info("Running testPackageToPath...");
        
        assertEquals("a", ClassUtil.packageToPath("a"));

        assertEquals("a" + File.separatorChar + "b" , 
            ClassUtil.packageToPath("a.b"));

                
        assertEquals("a" + File.separatorChar + 
                     "b" + File.separatorChar +
                     "c" + File.separatorChar + 
                     "d", ClassUtil.packageToPath("a.b.c.d"));
    }
}