package toolbox.util.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.Category;

import toolbox.util.FileUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.StringUtil;

/**
 * Unit test for ResourceUtilTest
 */
public class ResourceUtilTest extends TestCase
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(ResourceUtilTest.class);
        
    
    /**
     * Entrypoint
     * 
     * @param  args Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(ResourceUtilTest.class);    
    }
    
    
    /**
     * Constructor for ResourceUtilTest
     * 
     * @param  arg0  Name
     */
    public ResourceUtilTest(String arg0)
    {
        super(arg0);
    }
    
    
    /**
     * Tests the exportToClass() method
     * 
     * @throws Exception on error
     */
    public void testExportToClass() throws Exception
    {
        String treeOpen  = "images" + File.separator + "tree_open.gif";
    
        String filename = "TreeOpenGIF.java";
            
        String javaSrc = ResourceUtil.exportToClass(
            treeOpen, 
            "toolbox.util.ui", 
            "TreeOpenGIF",
            FileUtil.getTempDir());
    
        logger_.info("Wrote TreeOpenGIF.java to " + 
            FileUtil.getTempDir().getAbsolutePath() + " \n" + javaSrc);
            
        String compareSrc = FileUtil.getFileContents(
            FileUtil.getTempDir().getAbsolutePath() + 
            File.separator +
            filename);
            
        assertEquals("files don't match" , javaSrc, compareSrc);
    }
}