package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for ResourceUtilTest
 */
public class ResourceUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ResourceUtilTest.class);

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
        TestRunner.run(ResourceUtilTest.class);    
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the exportToClass() method
     * 
     * @throws Exception on error
     */
    public void testExportToClass() throws Exception
    {
        logger_.info("Running testExportToClass...");
        
//        String treeOpen  = "images" + File.separator + "tree_open.gif";
//    
//        String filename = "TreeOpenGIF.java";
//            
//        String javaSrc = ResourceUtil.exportToClass(
//            treeOpen, 
//            "toolbox.util.ui", 
//            "TreeOpenGIF",
//            FileUtil.getTempDir());
//    
//        logger_.info("Wrote TreeOpenGIF.java to " + 
//            FileUtil.getTempDir().getAbsolutePath() + " \n" + javaSrc);
//            
//        String compareSrc = FileUtil.getFileContents(
//            FileUtil.getTempDir().getAbsolutePath() + 
//            File.separator +
//            filename);
//            
//        assertEquals("files don't match" , javaSrc, compareSrc);
    }
}