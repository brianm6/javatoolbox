package toolbox.util.dirmon.recognizer;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.dirmon.DirSnapshot;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.IFileActivityRecognizer;
import toolbox.util.dirmon.event.FileEvent;

/**
 * Unit test for {@link FileDeletedRecognizer}.
 */
public class FileDeletedRecognizerTest extends TestCase {

    private static final Logger logger_ = 
        Logger.getLogger(FileDeletedRecognizerTest.class);

    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------

    public static void main(String[] args) {
        TestRunner.run(FileDeletedRecognizerTest.class);
    }

    // --------------------------------------------------------------------------
    // Unit Tests
    // --------------------------------------------------------------------------

    public void testGetRecognizedEvents_NewFilesAdded() throws Exception {
        logger_.info("Running testGetRecognizedEvents_NewFilesAdded ...");

        // Create a base line dir with two files
        File dir = FileUtil.createTempDir();
        DirSnapshot snapshot1 = new DirSnapshot(dir);
        DirectoryMonitor dm = new DirectoryMonitor(dir);

        try {
            File file1 = FileUtil.createTempFile(dir);
            File file2 = FileUtil.createTempFile(dir);
            FileUtils.writeStringToFile(file1, "file1", null);
            FileUtils.writeStringToFile(file2, "file2", null);
            DirSnapshot snapshot2 = new DirSnapshot(dir);
            
            IFileActivityRecognizer recognizer = new FileDeletedRecognizer(dm);
            List firstRun = recognizer.getRecognizedEvents(snapshot1, snapshot2);
            assertEquals("No deleted files should have been recognized", 0, firstRun.size());
        }
        finally {
            FileUtils.forceDelete(dir);
        }
    }

    
    public void testGetRecognizedEvents_NoChange() throws Exception {
        logger_.info("Running testGetRecognizedEvents_NoChange ...");

        File dir = FileUtil.createTempDir();
        DirectoryMonitor dm = new DirectoryMonitor(dir);

        try {
            File file1 = FileUtil.createTempFile(dir);
            File file2 = FileUtil.createTempFile(dir);
            FileUtils.writeStringToFile(file1, "file1", null);
            FileUtils.writeStringToFile(file2, "file2", null);
            DirSnapshot snapshot1 = new DirSnapshot(dir);
            DirSnapshot snapshot2 = new DirSnapshot(dir);
            
            IFileActivityRecognizer recognizer = new FileDeletedRecognizer(dm);
            List firstRun = recognizer.getRecognizedEvents(snapshot1, snapshot2);
            assertEquals("No deleted files should have been recognized", 0, firstRun.size());
        }
        finally {
            FileUtils.forceDelete(dir);
        }
    }
    
    
    public void testGetRecognizedEvents_SingleFileDeleted() throws Exception {
        logger_.info("Running testGetRecognizedEvents_SingleFileDeleted ...");

        File dir = FileUtil.createTempDir();
        DirectoryMonitor dm = new DirectoryMonitor(dir);
        IFileActivityRecognizer recognizer = new FileDeletedRecognizer(dm);

        try {
            File file1 = FileUtil.createTempFile(dir);
            File file2 = FileUtil.createTempFile(dir);
            FileUtils.writeStringToFile(file1, "file1", null);
            FileUtils.writeStringToFile(file2, "file2", null);
            String expectedPath = file2.getAbsolutePath();
            
            DirSnapshot snapshot1 = new DirSnapshot(dir);
            FileUtils.forceDelete(file2);
            DirSnapshot snapshot2 = new DirSnapshot(dir);
            
            List events = recognizer.getRecognizedEvents(snapshot1, snapshot2);
            assertEquals(1, events.size());
            
            FileEvent e = (FileEvent) events.get(0);
            assertEquals(FileEvent.TYPE_FILE_DELETED, e.getEventType());
            assertEquals(dm, e.getSource());
            assertNull(e.getAfterSnapshot());
            assertNotNull(e.getBeforeSnapshot());
            assertEquals(dm, e.getDirectoryMonitor());
            assertEquals(expectedPath, e.getBeforeSnapshot().getAbsolutePath());
        }
        finally {
            FileUtils.forceDelete(dir);
        }
    }
    
    
//    public void testGetRecognizedEvents_SingleFileDeleted() throws Exception {
//        logger_.info("Running testGetRecognizedEvents_SingleFileDeleted ...");
//
//        File dir = FileUtil.createTempDir();
//        DirectoryMonitor dm = new DirectoryMonitor(dir);
//
//        try {
//            File file1 = FileUtil.createTempFile(dir);
//            File file2 = FileUtil.createTempFile(dir);
//            FileUtils.writeStringToFile(file1, "file1", null);
//            FileUtils.writeStringToFile(file2, "file2", null);
//            DirSnapshot snapshot1 = new DirSnapshot(dir);
//            FileUtils.forceDelete(file2);
//            DirSnapshot snapshot2 = new DirSnapshot(dir);
//            
//            IFileActivityRecognizer recognizer = new FileDeletedRecognizer(dm);
//            List firstRun = recognizer.getRecognizedEvents(snapshot1, snapshot2);
//            assertEquals(1, firstRun.size());
////
////            // Delete a file from the directory
////            FileUtil.delete(file1);
////
////            // Run the activity again..should report 1 deleted file
////            List secondRun = activity.getAffectedFiles(dir);
////
////            assertEquals(
////                "second run should contain one file", 1, secondRun.size());
////
////            logger_.debug("Deleted file activity: "
////                + ArrayUtil.toString(secondRun.toArray()));
////
////            // Run the activity again.. should report no new files
////            List thirdRun = activity.getAffectedFiles(dir);
////            assertEquals(0, thirdRun.size());
//        }
//        finally {
//            FileUtils.forceDelete(dir);
//        }
//    }
    
    
//    public void testFileEquality() {
//        logger_.info("Running testFileEquality ...");
//        
//        
//        File a = new File("c:\\nresults.txt");
//        File b = new File("c:\\nresults.txt");
//        File c = new File("c:\\bin\\..\\nresults.txt");
//        
//        List x = new ArrayList();
//        
//        x.add(a);
//        x.remove(b);
//        x.add(b);
//        x.remove(a);
//        x.add(a);
//        x.add(a);
//        
//        logger_.error("Size = " + x.size());
//    }
    
    
}