package toolbox.util.ui.explorer.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.Platform;
import toolbox.util.ui.explorer.FileNode;

/**
 * Unit Test for FileNodeTest. 
 */
public class FileNodeTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(FileNodeTest.class);
    
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
        TestRunner.run(FileNodeTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the Object constructor.
     * 
     * @throws Exception on error. 
     */
    public void testConstructorElement() throws Exception
    {
        logger_.info("Running testConstructorObject...");
        
        FileNode node = new FileNode("file.xml");
        assertEquals("file.xml", node.getUserObject());
    }
    
    
    /**
     * Tests the equals() method.
     */
    public void testEquals()
    {
        logger_.info("Running testEquals...");
        
        // Equality to self
        FileNode info = new FileNode("abc");
        assertTrue(info.equals(info));
        
        // Equality by content
        FileNode replica = new FileNode("abc");
        assertTrue(info.equals(replica));
  
        // Equality by filename based on the platform
        FileNode upperCase = new FileNode("ABC");
        FileNode lowerCase = new FileNode("abc");
        
        if (Platform.isUnix())
        {
            // Should not be equal on unix
            assertTrue(!upperCase.equals(lowerCase));
        }
        else
        {
            // Should be equal most everywhere else
            assertTrue(upperCase.equals(lowerCase));
        }
        
        // Inequality by content
        FileNode different = new FileNode("xyz");
        assertTrue(!info.equals(different));
    }
}
