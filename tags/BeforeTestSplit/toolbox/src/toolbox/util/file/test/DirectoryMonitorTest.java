package toolbox.util.file.test;

import java.io.File;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.file.DirectoryMonitor;
import toolbox.util.file.IDirectoryListener;
import toolbox.util.file.IFileActivity;

/**
 * Unit test for DirectoryMonitor.
 */
public class DirectoryMonitorTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(DirectoryMonitorTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(DirectoryMonitorTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests a full lifecycle of the DirectoryMonitor. 
     * 
     * @throws Exception on IO error
     */
    public void testDirectoryMonitor() throws Exception
    {
        logger_.info("Running testDirectoryMonitor...");
        
        File dir = FileUtil.createTempDir();
        
        try
        {
            DirectoryMonitor dm = new DirectoryMonitor(dir);
            dm.setDelay(500);
            
            // Dummy activity
            IFileActivity activity = new IFileActivity()
            {
                public File[] getFiles(File dir)
                {
                    return new File[0];
                }
            };
            
            // Dummy listener
            IDirectoryListener listener = new IDirectoryListener()
            {
                public void fileActivity(IFileActivity activity, File[] files)
                    throws Exception
                {
                    logger_.info("File activity reported: " + 
                        ArrayUtil.toString(files));
                }
            };
            
            dm.addDirectoryListener(listener);
            dm.addFileActivity(activity);
            dm.start();
            
            ThreadUtil.sleep(1000);
            
            dm.stop();
            dm.removeFileActivity(activity);
            dm.removeDirectoryListener(listener);
        }
        finally
        {
            FileUtil.removeDir(dir);
        }
    }
    
    
    /**
     * Tests failure of an attempt to start an already running directory 
     * monitor. 
     * 
     * @throws Exception on I/O error
     */
    public void testDirectoryMonitorFalseStart() throws Exception
    {
        logger_.info("Running testDirectoryMonitorFalseStart...");
        
        File dir = FileUtil.createTempDir();
        
        DirectoryMonitor dm = new DirectoryMonitor(dir);
        dm.setDelay(500);
        
        dm.start();
        
        try
        {
            dm.start();
            fail("Expected failure on attempt to start twice");
        }
        catch (IllegalStateException ise)
        {
            ; // Success
        }
        catch (Exception e)
        {
            fail("Expected IllegalStateException");
        }
        finally
        {
            dm.stop();
            FileUtil.removeDir(dir);
        }
    }
}