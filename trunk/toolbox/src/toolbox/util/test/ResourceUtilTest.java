package toolbox.util.test;

import java.awt.Image;
import java.awt.MediaTracker;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JPanel;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ResourceUtil;
import toolbox.util.StreamUtil;

/**
 * Unit test for ResourceUtilTest
 */
public class ResourceUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ResourceUtilTest.class);

    private static final String MATCH_STRING = "ResourceUtil";
    
    private static final String TEST_URL = "http://www.yahoo.com/index.html";
    
    private static final String FILE_TEXT = 
        "/toolbox/util/test/ResourceUtilTest_Text.txt";
        
    private static final String FILE_BINARY =
        "/toolbox/util/test/ResourceUtilTest_Binary.dat";
        
    private static final String FILE_IMAGE = 
        "/toolbox/util/test/ResourceUtilTest_Image.gif";
        
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
    
    /**
     * Tests getResource() on a text file
     * 
     * @throws Exception on error
     */
    public void testGetResourceByFile() throws Exception
    {
        logger_.info("Running testGetResource...");
        
        InputStream is = ResourceUtil.getResource(FILE_TEXT);
        assertNotNull("stream is null", is);        
        String contents = StreamUtil.asString(is);
        logger_.info("Resource: " + contents);
        assertTrue("string match failure", contents.indexOf(MATCH_STRING) >= 0);
    }
    
    /**
     * Tests getResourceAsBytes() on a text file
     * 
     * @throws Exception on error
     */
    public void testGetResourceAsBytes() throws Exception
    {
        logger_.info("Running testGetResourceAsBytes...");
        
        byte[] data = ResourceUtil.getResourceAsBytes(FILE_TEXT);
        assertNotNull("data is null", data);
        String contents = new String(data);
        logger_.info("Resource: " + contents);
        assertTrue("string match failure", contents.indexOf(MATCH_STRING) >= 0);
    }

    /**
     * Tests getResourceAsIcon() on a GIF file
     * 
     * @throws Exception on error
     */
    public void testGetResourceAsIcon() throws Exception
    {
        logger_.info("Running testGetResourceAsIcon...");
        
        Icon icon = ResourceUtil.getResourceAsIcon(FILE_IMAGE);
        assertNotNull("icon is null", icon);
        assertTrue(icon.getIconHeight() > 0);
        assertTrue(icon.getIconWidth() > 0);
    }

    /**
     * Tests getResourceAsImage() on a GIF file
     * 
     * @throws Exception on error
     */
    public void testGetResourceAsImage() throws Exception
    {
        logger_.info("Running testGetResourceAsImage...");
        
        Image image = ResourceUtil.getResourceAsImage(FILE_IMAGE);
        assertNotNull("icon is null", image);
        
        MediaTracker tracker = new MediaTracker(new JPanel());
        tracker.addImage(image, 0);
        tracker.waitForAll(); 
        
        assertTrue(image.getHeight(null) > 0);
        assertTrue(image.getWidth(null) > 0);
    }
    
    /**
     * Tests getResource() on a HTTP URL
     * 
     * @throws Exception on error
     */
    public void testGetResourceByURL() throws Exception
    {
        logger_.info("Running testGetResourceByURL...");
        
        InputStream is =  ResourceUtil.getResource(TEST_URL);
        assertNotNull("stream is null", is);
        String contents = StreamUtil.asString(is);
        logger_.info("Resource length: " + contents.length());
    }
}