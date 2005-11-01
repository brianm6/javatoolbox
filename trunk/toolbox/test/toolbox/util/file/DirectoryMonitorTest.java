package toolbox.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.file.activity.FileChangedActivity;
import toolbox.util.file.activity.FileCreatedActivity;
import toolbox.util.file.activity.FileDeletedActivity;

/**
 * Unit test for {@link toolbox.util.file.DirectoryMonitor}.
 */
public class DirectoryMonitorTest extends TestCase {

    private static final Logger logger_ = 
        Logger.getLogger(DirectoryMonitorTest.class);

    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------

    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args) {
        TestRunner.run(DirectoryMonitorTest.class);
    }

    // --------------------------------------------------------------------------
    // Unit Tests
    // --------------------------------------------------------------------------

    /**
     * Tests a full lifecycle of the DirectoryMonitor.
     * 
     * @throws Exception on I/O error.
     */
    public void testDirectoryMonitor() throws Exception {
        logger_.info("Running testDirectoryMonitor...");

        File dir = FileUtil.createTempDir();

        try {
            DirectoryMonitor dm = new DirectoryMonitor(dir);

            // Dummy activity
            IFileActivity activity = new IFileActivity() {

                public List getAffectedFiles(File dir) {
                    return new ArrayList();
                }
            };

            // Dummy listener
            IDirectoryListener listener = new IDirectoryListener() {

                public void fileActivity(
                    IFileActivity activity, 
                    List affectedFiles) throws Exception {
                    
                    logger_.info("File activity reported: "
                        + ArrayUtil.toString(affectedFiles.toArray()));
                }
            };

            dm.addDirectoryListener(listener);
            dm.addFileActivity(activity);
            dm.setDelay(100);
            dm.start();

            ThreadUtil.sleep(1000);

            dm.stop();
            dm.removeFileActivity(activity);
            dm.removeDirectoryListener(listener);
        }
        finally {
            FileUtil.removeDir(dir);
        }
    }


    /**
     * Tests failure of an attempt to start an already running directory
     * monitor.
     * 
     * @throws Exception on I/O error.
     */
    public void testDirectoryMonitorFalseStart() throws Exception {
        logger_.info("Running testDirectoryMonitorFalseStart...");

        File dir = FileUtil.createTempDir();

        DirectoryMonitor dm = new DirectoryMonitor(dir);
        dm.setDelay(250);

        dm.start();

        try {
            dm.start();
            fail("Expected failure on attempt to start twice");
        }
        catch (IllegalStateException ise) {
            // Success
            logger_.debug("SUCCESS: start twice failed.");
        }
        catch (Exception e) {
            fail("Expected IllegalStateException");
        }
        finally {
            dm.stop();
            FileUtil.removeDir(dir);
        }
    }
    
    
    /**
     * @throws Exception
     */
    public void testDirectoryMonitorWithSubDirs() throws Exception {
        logger_.info("Running testDirectoryMonitorWithSubDirs...");
        
        //File root = FileUtil.createTempDir();
        //File sub1 = FileUtil.createTempDir(root);
        //File sub11 = FileUtil.createTempDir(sub1);
        //File sub2 = FileUtil.createTempDir(root);
        
        File root = new File("c:\\tmp\\crap");
        
        //File root = new File("M:\\x1700_vacany_10_dynamic\\staffplanning\\vacancy\\dev\\Ophelia\\src");
        
        try {
            DirectoryMonitor dm = new DirectoryMonitor(root, true);
            
            dm.addFileActivity(new FileCreatedActivity());
            dm.addFileActivity(new FileDeletedActivity());
            dm.addFileActivity(new FileChangedActivity());
            dm.setDelay(1000);
            dm.addDirectoryListener(new IDirectoryListener() {
            
                public void fileActivity(
                    IFileActivity activity, 
                    List affectedFiles) 
                    throws Exception {

                    String msg = null;
                    
                    if (activity instanceof FileCreatedActivity) {
                        msg = "File created = ";
                    }
                    else if (activity instanceof FileDeletedActivity) { 
                        msg = "File deleted = ";
                    }
                    else if (activity instanceof FileChangedActivity) {
                        msg = "File changed = ";
                    }
                    
                    logger_.info(
                        msg   
                        + ArrayUtil.toString(affectedFiles.toArray()));
                }
            });
            
            dm.start();
            
            ThreadUtil.sleep(999999999);
            
            //dm.stop();
        }
        finally {
            //FileUtils.cleanDirectory(root);
        }
    }
}