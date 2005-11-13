package toolbox.util.dirmon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.dirmon.recognizer.FileChangedRecognizer;
import toolbox.util.dirmon.recognizer.FileCreatedRecognizer;
import toolbox.util.dirmon.recognizer.FileDeletedRecognizer;
import edu.emory.mathcs.util.concurrent.ArrayBlockingQueue;
import edu.emory.mathcs.util.concurrent.BlockingQueue;

/**
 * Unit test for {@link toolbox.util.dirmon.DirectoryMonitor}.
 */
public class DirectoryMonitorTest extends TestCase {

    private static final Logger logger_ = 
        Logger.getLogger(DirectoryMonitorTest.class);

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args) {
        TestRunner.run(DirectoryMonitorTest.class);
    }

    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------

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

            // Mock Recognizer
            IFileActivityRecognizer recognizer = new IFileActivityRecognizer() {

                public List getRecognizedEvents(
                    DirSnapshot before, DirSnapshot after){
                    return new ArrayList();
                }
            };

            // Mock Listener
            IDirectoryMonitorListener listener = 
                new IDirectoryMonitorListener() {

                public void directoryActivity(
                    DirectoryMonitorEvent directoryMonitorEvent) 
                    throws Exception{
                    
                    logger_.info(
                        "File activity reported: " 
                        + directoryMonitorEvent);
                }
            };

            dm.addDirectoryMonitorListener(listener);
            dm.addRecognizer(recognizer);
            dm.setDelay(200);
            
            dm.start(); ThreadUtil.sleep(1000);
            dm.suspend(); ThreadUtil.sleep(1000);
            dm.resume(); ThreadUtil.sleep(1000);
            dm.stop();
            
            dm.removeRecognizer(recognizer);
            dm.removeDirectoryMonitorListener(listener);
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
        
        File mockDir = FileUtil.createTempDir();
        //File sub1 = FileUtil.createTempDir(root);
        //File sub11 = FileUtil.createTempDir(sub1);
        //File sub2 = FileUtil.createTempDir(root);
        
        //File root = new File("c:\\tmp\\crap");
        
        //File root = new File("M:\\x1700_vacany_10_dynamic\\staffplanning\\vacancy\\dev\\Ophelia\\src");
        
        try {
            DirectoryMonitor dm = new DirectoryMonitor(mockDir, true);
            
            dm.addRecognizer(new FileCreatedRecognizer(dm));
            dm.addRecognizer(new FileDeletedRecognizer(dm));
            dm.addRecognizer(new FileChangedRecognizer(dm));
            dm.setDelay(1000);
            
            final BlockingQueue eventQueue = new ArrayBlockingQueue(3);
            
            dm.addDirectoryMonitorListener(new IDirectoryMonitorListener() {
                
                public void directoryActivity(
                    DirectoryMonitorEvent event) 
                    throws Exception{
                    
                    switch (event.getEventType()) {
                        
                        case DirectoryMonitorEvent.TYPE_CREATED:
                            eventQueue.offer(event);
                            break;
                            
                        case DirectoryMonitorEvent.TYPE_CHANGED:
                            eventQueue.offer(event);
                            break;
                            
                        case DirectoryMonitorEvent.TYPE_DELETED:
                            eventQueue.offer(event);
                            break;
                            
                        default:
                            fail("Event not recognized.");
                    }
                }
            });
            
            dm.start();
            
            // Give the monitor time to startup...
            ThreadUtil.sleep(3000);
            
            String mockFilename = FileUtil.createTempFilename(mockDir);
            File mockFile = new File(mockFilename);
            
            // Test file created
            // =================================================================            
            FileUtils.writeStringToFile(mockFile, "testing", "utf-8");
            DirectoryMonitorEvent e = (DirectoryMonitorEvent) eventQueue.take();
            assertEquals(DirectoryMonitorEvent.TYPE_CREATED, e.getEventType());
            
            assertEquals(
                mockFile.getAbsolutePath(), 
                e.getAfterSnapshot().getAbsolutePath());
            
            logger_.debug("SUCCESS: Notified of file creation");
            
            // Test file changed
            // =================================================================
            FileUtils.touch(mockFile);
            DirectoryMonitorEvent e2 = (DirectoryMonitorEvent) eventQueue.take();
            assertEquals(DirectoryMonitorEvent.TYPE_CHANGED, e2.getEventType());
            
            assertEquals(
                mockFile.getAbsolutePath(), 
                e2.getAfterSnapshot().getAbsolutePath());
            
            logger_.debug("SUCCESS: Notified of file changed");
            
            // Test file deleted            
            // =================================================================            
            mockFile.delete();
            DirectoryMonitorEvent e3 = (DirectoryMonitorEvent) eventQueue.take();
            assertEquals(DirectoryMonitorEvent.TYPE_DELETED, e3.getEventType());
            
            assertEquals(
                mockFile.getAbsolutePath(), 
                e3.getBeforeSnapshot().getAbsolutePath());
            
            logger_.debug("SUCCESS: Notified of file deletion");
            
            dm.stop();
        }
        finally {
            FileUtils.cleanDirectory(mockDir);
        }
    }
}