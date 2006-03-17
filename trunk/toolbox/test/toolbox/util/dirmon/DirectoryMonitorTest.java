package toolbox.util.dirmon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import edu.emory.mathcs.backport.java.util.concurrent.ArrayBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;
import toolbox.util.dirmon.recognizer.FileChangedRecognizer;
import toolbox.util.dirmon.recognizer.FileCreatedRecognizer;
import toolbox.util.dirmon.recognizer.FileDeletedRecognizer;

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
                    FileEvent directoryMonitorEvent) 
                    throws Exception{
                    
                    logger_.debug(
                        "File activity reported: " 
                        + directoryMonitorEvent);
                }
                
                public void statusChanged(StatusEvent statusEvent) 
                    throws Exception {
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
            FileUtils.deleteDirectory(dir);
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
            FileUtils.deleteDirectory(dir);
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
                    FileEvent event) 
                    throws Exception{
                    
                    switch (event.getEventType()) {
                        
                        case FileEvent.TYPE_FILE_CREATED:
                            eventQueue.offer(event);
                            break;
                            
                        case FileEvent.TYPE_FILE_CHANGED:
                            eventQueue.offer(event);
                            break;
                            
                        case FileEvent.TYPE_FILE_DELETED:
                            eventQueue.offer(event);
                            break;
                            
                        default:
                            fail("Event not recognized.");
                    }
                }
                
                public void statusChanged(StatusEvent statusEvent) 
                    throws Exception {
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
            FileEvent e = (FileEvent) eventQueue.take();
            assertEquals(FileEvent.TYPE_FILE_CREATED, e.getEventType());
            
            assertEquals(
                mockFile.getAbsolutePath(), 
                e.getAfterSnapshot().getAbsolutePath());
            
            logger_.debug("SUCCESS: Notified of file creation");
            
            // Test file changed
            // =================================================================
            FileUtils.touch(mockFile);
            FileEvent e2 = (FileEvent) eventQueue.take();
            assertEquals(FileEvent.TYPE_FILE_CHANGED, e2.getEventType());
            
            assertEquals(
                mockFile.getAbsolutePath(), 
                e2.getAfterSnapshot().getAbsolutePath());
            
            logger_.debug("SUCCESS: Notified of file changed");
            
            // Test file deleted            
            // =================================================================            
            mockFile.delete();
            FileEvent e3 = (FileEvent) eventQueue.take();
            assertEquals(FileEvent.TYPE_FILE_DELETED, e3.getEventType());
            
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