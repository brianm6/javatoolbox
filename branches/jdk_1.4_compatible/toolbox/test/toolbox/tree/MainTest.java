package toolbox.tree;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.tree.Main}.
 */
public class MainTest extends TestCase{

    private static final Logger logger_ = Logger.getLogger(MainTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args){
        TestRunner.run(MainTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests printing the help/usage information.
     */
    public void testPrintUsage() throws Exception {
        logger_.info("Running testPrintUsage...");
        
        // Send in an invalid flag so usage information is shown
        Main.main(new String[] {"-xyz"});
    }
    
    
    /**
     * Tests execution via main().
     */
//    public void xxxtestMain() throws Exception {
//        // TODO: Fix me to use options when upgrading to cli2
//        
//        logger_.info("Running testMain...");
//        
//        Tree.main(new String[] {"-os", FileUtil.getTempDir().getAbsolutePath()});
//    }
}
