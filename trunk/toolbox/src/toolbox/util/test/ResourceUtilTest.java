package toolbox.util.test;

import java.awt.Image;
import java.awt.MediaTracker;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JPanel;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.StreamUtil;

/**
 * Unit test for ResourceUtilTest.
 */
public class ResourceUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ResourceUtilTest.class);
    
    //--------------------------------------------------------------------------
    // Constants 
    //--------------------------------------------------------------------------
    
    /**
     * String embedded in FILE_TEXT used to verify correctness.
     */
    private static final String MATCH_STRING = "ResourceUtil";
    
    /**
     * URL to test getResource() via HTTP. 
     */
    private static final String TEST_URL = "http://www.yahoo.com/index.html";
    
    /**
     * Text file to load as a resource.
     */
    private static final String FILE_TEXT = 
        "/toolbox/util/test/ResourceUtilTest_Text.txt";
        
    /**
     * Binary file to load as a resource.
     */
    private static final String FILE_BINARY =
        "/toolbox/util/test/ResourceUtilTest_Binary.dat";
        
    /**
     * Image file to load as a resource.
     */
    private static final String FILE_IMAGE = 
        "/toolbox/util/test/ResourceUtilTest_Image.gif";
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(ResourceUtilTest.class);    
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests : getResource()
    //--------------------------------------------------------------------------
    
    /**
     * Tests getResource() on a text file in the classpath.
     * 
     * @throws Exception on error.
     */
    public void testGetResource_FileInClasspath() throws Exception
    {
        logger_.info("Running testGetResource_FileInClasspath...");
        
        InputStream is = ResourceUtil.getResource(FILE_TEXT);
        assertNotNull("stream is null", is);        
        String contents = StreamUtil.asString(is);
        logger_.info("Resource: " + contents);
        assertTrue("string match failure", contents.indexOf(MATCH_STRING) >= 0);
    }
    
    
    /**
     * Tests getResource() on a text file in the system temp directory by 
     * using an absolute file path.
     * 
     * @throws Exception on error.
     */
    public void testGetResource_FileAbsolute() throws Exception
    {
        logger_.info("Running testGetResource_FileAbsolute...");

        // Create a file in the tmp dir
        File tmpFile = null;
        
        try
        {
            tmpFile = FileUtil.createTempFile();
            
            String contents = 
                getClass().getName() + ":testGetResource_FileAbsolute";
            
            FileUtil.setFileContents(tmpFile, contents, false);
            String absolutePath = tmpFile.getAbsolutePath();
            logger_.debug("Test file's absolute path: " + absolutePath);
            
            InputStream is = ResourceUtil.getResource(absolutePath);
            assertNotNull("stream is null", is);        
            
            String newContents = StreamUtil.asString(is);
            logger_.info("Contents: " + newContents);
            assertEquals("File contents don't match", contents, newContents);
        }
        finally
        {
            FileUtil.delete(tmpFile);
        }
    }
    
    
    /**
     * Tests getResource() on a HTTP URL.
     * 
     * @throws Exception on error.
     */
    public void testGetResource_FileOverHTTP() throws Exception
    {
        logger_.info("Running testGetResource_FileOverHTTP...");
        
        InputStream is =  ResourceUtil.getResource(TEST_URL);
        assertNotNull("stream is null", is);
        String contents = StreamUtil.asString(is);
        logger_.info("Resource length: " + contents.length());
        assertTrue(contents.length() > 0);
    }

    
    /**
     * Tests getResource() failure.
     * 
     * @throws Exception on error.
     */
    public void testGetResource_Failure() throws Exception
    {
        logger_.info("Running testGetResource_Failure...");
        
        // Non-existant file
        try
        {
            ResourceUtil.getResource("bogus_file.txt");
            fail("getResource() should have failed on a non-existant file.");
        }
        catch (IOException ioe)
        {
            logger_.debug("Failure message: " + 
                ioe.getClass().getName() + ":" + ioe.getMessage());
        }
        
        // Non-existant HTTP url resource
        try
        {
            ResourceUtil.getResource("http://www.yahoo.com/crap.html");
            fail("getResource() should fail on a non-existant HTTP file.");
        }
        catch (IOException ioe)
        {
            logger_.debug("Failure message: " + 
                ioe.getClass().getName() + ":" + ioe.getMessage());
        }
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests : Other
    //--------------------------------------------------------------------------
    
    /**
     * Tests getResourceAsBytes() on a text file.
     * 
     * @throws Exception on error.
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
     * Tests getResourceAsTempFile() on a text file.
     * 
     * @throws Exception on error.
     */
    public void testGetResourceAsTempFile() throws Exception
    {
        logger_.info("Running testGetResourceAsTempFile...");
        
        File tempFile = null;
        
        try
        {
            tempFile = ResourceUtil.getResourceAsTempFile(FILE_TEXT);
        
            assertTrue("temp file does not exist", tempFile.exists());
            
            String contents = new String(
                FileUtil.getFileContents(tempFile.getCanonicalPath()));
            
            logger_.info("Resource: " + contents);
            
            assertTrue(
                "string match failure", contents.indexOf(MATCH_STRING) >= 0);
        }
        finally
        {
            FileUtil.delete(tempFile);
        }
    }

    
    /**
     * Tests getResourceAsIcon() on a GIF file.
     * 
     * @throws Exception on error.
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
     * Tests getResourceAsImage() on a GIF file.
     * 
     * @throws Exception on error.
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
     * Tests the exportToClass() method.
     * 
     * @throws Exception on error.
     */
    public void testExportToClass() throws Exception
    {
        logger_.info("Running testExportToClass...");
        
        //String treeOpen  = "images" + File.separator + "tree_open.gif";
        //
        //String filename = "TreeOpenGIF.java";
        //    
        //String javaSrc = ResourceUtil.exportToClass(
        //    treeOpen, 
        //    "toolbox.util.ui", 
        //    "TreeOpenGIF",
        //    FileUtil.getTempDir());
        //
        //logger_.info("Wrote TreeOpenGIF.java to " + 
        //    FileUtil.getTempDir().getAbsolutePath() + " \n" + javaSrc);
        //    
        //String compareSrc = FileUtil.getFileContents(
        //    FileUtil.getTempDir().getAbsolutePath() + 
        //    File.separator +
        //    filename);
        //    
        //assertEquals("files don't match" , javaSrc, compareSrc);
    }
}