package toolbox.util.file.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.file.DirectoryMonitor;

/**
 * Unit test for DirectoryMonitor
 */
public class DirectoryMonitorTest extends TestCase
{
    /**
     * Entrypoint
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(DirectoryMonitorTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for DirectoryMonitorTest
     * 
     * @param  arg0  Name
     */
    public DirectoryMonitorTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the DirectoryMonitor 
     * 
     * @throws  Exception on IO error
     */
    public void testDirectoryMonitor() throws Exception
    {
        File dir = FileUtil.createTempDir();
        DirectoryMonitor dm = new DirectoryMonitor(dir);
        dm.start();
        ThreadUtil.sleep(1000);
        dm.stop();
    }
}