package toolbox.util.test;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.log4j.SmartLogger;
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

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(ClassUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests getClassesInPackage() for a package in a file system directory
     */
    public void testGetClassesInPackageDirectory()
    {
        assertTrue(true);
        
        // TODO: Revisit testcase 
        
        // Have to revisit because no classes files are hanging out in
        // a directory on the classpath when the test is run from outside
        // eclipse
        
        //logger_.info("Running testGetClassesInPackageDirectory...");
        
        //String[] classes = ClassUtil.getClassesInPackage("toolbox.util");
        //logger_.info("\n" + ArrayUtil.toString(classes, true));
        //assertTrue(StringUtil.class.getName() + " not found in package", 
        //    ArrayUtil.contains(classes, StringUtil.class.getName()));
    }
    
    /**
     * Tests getClassesInPackage() for a package in a jar file
     */
    public void testGetClassesInPackageArchive()
    {
        logger_.info("Running testGetClassesInPackageArchive...");
        
        String[] classes = ClassUtil.getClassesInPackage("junit.textui");
        //logger_.info("\n"+ArrayUtil.toString(classes, true));
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
        
        //logger_.info("\n" + ArrayUtil.toString(classes, true));
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
        SmartLogger.info(logger_, ArrayUtil.toString(packages, true));
        assertTrue("tgpic1", ArrayUtil.contains(packages, "java.lang"));
        assertTrue("tgpic3", ArrayUtil.contains(packages, "junit.textui"));
    }
    
    /**
     * Tests getClasspath()
     */
    public void testGetClasspath()
    {
        logger_.info("Running testGetClasspath...");
        
        String classpath = ClassUtil.getClasspath();
        assertNotNull("cp null", classpath);
        String[] paths = StringUtil.tokenize(classpath, File.pathSeparator);
        SmartLogger.info(logger_, ArrayUtil.toString(paths, true));
    }
    
    /**
     * Tests isArchive()
     */
    public void testIsArchive()
    {
        logger_.info("Running testIsArchive...");
        
        // Positive tests
        assertTrue("tar1", ClassUtil.isArchive("a.jar"));
        assertTrue("tar2", ClassUtil.isArchive("b.zip"));
        assertTrue("tar3", ClassUtil.isArchive("AFD.JAR"));
        assertTrue("tar4", ClassUtil.isArchive("ASDF.ZIP")); 
        assertTrue("tar5", ClassUtil.isArchive("a.b.jar"));
        assertTrue("tar6", ClassUtil.isArchive("C.D.ZIP"));
                
        // Negative tests        
        assertTrue("tar7", !ClassUtil.isArchive(""));
        assertTrue("tar8", !ClassUtil.isArchive(" "));
        assertTrue("tar9", !ClassUtil.isArchive("jar"));
        assertTrue("tar10", !ClassUtil.isArchive("ZIP"));        
        assertTrue("tar11", !ClassUtil.isArchive("df.jarx"));
        assertTrue("tar12", !ClassUtil.isArchive("df.zipo"));
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
    
    /**
     * Tests stripPackage()
     */
    public void testStripPackage()
    {
        logger_.info("Running testStripPackage...");
        
        assertEquals("Class names don't match", "ClassUtilTest", 
            ClassUtil.stripPackage(getClass().getName()));
            
        assertEquals("Class names don't match", "c",
            ClassUtil.stripPackage("a.b.c"));
            
        assertEquals("Class names don't match", "NoPackage",
            ClassUtil.stripPackage("NoPackage"));
    }
    
    /**
     * Tests stripClass()
     */
    public void testStripClass()
    {
        logger_.info("Running testStripClass...");
        
        assertEquals("Package names don't match", "java.io", 
            ClassUtil.stripClass("java.io.InputStream"));
            
        assertEquals("Package names don't match", "", 
            ClassUtil.stripClass("Widget"));
            
        assertEquals("Package names don't match", "", 
            ClassUtil.stripClass("")); 
    }
    
    /**
     * Tests getMatchingClasses() for null, zero, one, and > 1 as input
     */
    public void testGetMatchingClasses()
    {
        logger_.info("Running testGetMatchingClasses...");
        
        // Null
        assertEquals(0, ClassUtil.getMatchingClasses(null).length);
        
        // Zero
        assertEquals(0, ClassUtil.getMatchingClasses(new Object[0]).length);
        
        // One
        Class[] one = ClassUtil.getMatchingClasses(new Object[] { "whoopee" } );
        assertEquals(1, one.length);
        assertEquals(String.class, one[0]);
        
        // Many
        Object[] objs = new Object[] {"whoopee",new Integer(3),new ArrayList()};
        Class[] many = ClassUtil.getMatchingClasses(objs);
        assertEquals(3, many.length);
        assertEquals(String.class, many[0]);
        assertEquals(Integer.class, many[1]);
        assertEquals(ArrayList.class, many[2]);
    }
}