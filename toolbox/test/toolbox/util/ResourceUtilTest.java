package toolbox.util;

import java.awt.Image;
import java.awt.MediaTracker;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import javax.swing.Icon;
import javax.swing.JPanel;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.ResourceUtil}.
 */
public class ResourceUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(ResourceUtilTest.class);
    
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
        
    public static void main(String[] args)
    {
        TestRunner.run(ResourceUtilTest.class);    
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests : getResource()
    //--------------------------------------------------------------------------
    
    /**
     * Tests getResource() on a text file in the classpath.
     */
    public void testGetResourceFileInClasspath() throws Exception
    {
        logger_.info("Running testGetResourceFileInClasspath...");
        
        InputStream is = ResourceUtil.getResource(FILE_TEXT);
        assertNotNull("stream is null", is);        
        String contents = IOUtils.toString(is);
        logger_.debug("Resource: " + contents);
        assertTrue("string match failure", contents.indexOf(MATCH_STRING) >= 0);
    }
    
    
    /**
     * Tests getResource() on a text file in the system temp directory by 
     * using an absolute file path.
     */
    public void testGetResourceFileAbsolute() throws Exception
    {
        logger_.info("Running testGetResourceFileAbsolute...");

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
            
            String newContents = IOUtils.toString(is);
            logger_.debug("Contents: " + newContents);
            assertEquals("File contents don't match", contents, newContents);
        }
        finally
        {
            FileUtil.deleteQuietly(tmpFile);
        }
    }
    
    
    /**
     * Tests getResource() on a HTTP URL.
     */
    public void testGetResourceFileOverHTTP() throws Exception
    {
        logger_.info("Running testGetResourceFileOverHTTP...");
        
        try {
            InputStream is =  ResourceUtil.getResource(TEST_URL);
            assertNotNull("stream is null", is);
            String contents = IOUtils.toString(is);
            logger_.debug("Resource length: " + contents.length());
            assertTrue(contents.length() > 0);
        }
        catch (UnknownHostException uhe) {
            logger_.debug("Can't resolve host..probably not connected to the net. Skipping test...");
        }
    }

    
    /**
     * Tests getResource() failure.
     */
    public void testGetResourceFailure() throws Exception
    {
        logger_.info("Running testGetResourceFailure...");
        
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
     */
    public void testGetResourceAsBytes() throws Exception
    {
        logger_.info("Running testGetResourceAsBytes...");
        
        byte[] data = ResourceUtil.getResourceAsBytes(FILE_TEXT);
        assertNotNull("data is null", data);
        String contents = new String(data);
        logger_.debug("Resource: " + contents);
        assertTrue("string match failure", contents.indexOf(MATCH_STRING) >= 0);
    }


    /**
     * Tests getResourceAsString() on a text file.
     */
    public void testGetResourceAsString() throws Exception
    {
        logger_.info("Running testGetResourceAsString...");
        
        String data = ResourceUtil.getResourceAsString(FILE_TEXT);
        assertNotNull("data is null", data);
        String contents = new String(data);
        logger_.debug("Resource: " + contents);
        assertTrue("string match failure", contents.indexOf(MATCH_STRING) >= 0);
    }
    
    
    /**
     * Tests getResourceAsTempFile() on a text file.
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
            
            logger_.debug("Resource: " + contents);
            
            assertTrue(
                "string match failure", contents.indexOf(MATCH_STRING) >= 0);
        }
        finally
        {
            FileUtil.deleteQuietly(tempFile);
        }
    }

    
    /**
     * Tests getResourceAsIcon() on a GIF file.
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
     */
    public void testExportToClass() throws Exception
    {
        logger_.info("Running testExportToClass...");
        
        String classfile = RandomUtil.nextString(10);
        
        String javaSrc = 
            ResourceUtil.exportToClass(
              FILE_IMAGE,
              "resourceutil.test",
              classfile,
              FileUtil.getTempDir());

        logger_.debug("Wrote " + classfile + ".java to " + 
            FileUtil.getTempDir().getCanonicalPath() + "\n" + 
            StringUtil.banner(javaSrc.substring(0, 200) + "..."));
        
        FileUtil.deleteQuietly(new File(FileUtil.getTempDir(), classfile + ".java"));
    }
}