package toolbox.junit.test;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.junit.PackageTester;

/**
 * Unit test for PackageTester
 */
public class PackageTesterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(PackageTesterTest.class);
        
    private static final String testPackage_ = "toolbox.showpath.test";    
        
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(PackageTesterTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
        
    /**
     * Constructor for PackageTesterTest.
     * 
     * @param arg0 Test name
     */
    public PackageTesterTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests main()
     */
    public void testMain()
    {
        logger_.info("Running testMain...");
        
        PackageTester.main(new String[] { testPackage_ } );
    }
    
    /**
     * Tests PackageTester
     */
    public void testPackageTester()
    {
        logger_.info("Running testPackageTester...");
        
        PackageTester t = new PackageTester();
        t.addPackage(testPackage_);        
        assertEquals(1, t.getPackageCount());
        t.run();
    }
    
    /**
     * Tests constructor PackageTester(String)
     */
    public void testConstructor()
    {
        logger_.info("Running testConstructor...");
        
        PackageTester t = new PackageTester(testPackage_);
        assertEquals(1, t.getPackageCount());
        t.run();
    }
}