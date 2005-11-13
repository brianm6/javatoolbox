package toolbox.util.dirmon.recognizer;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;

/**
 * Unit test for {@link toolbox.util.dirmon.trash.FileDeletedActivity}.
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

    public void testGetAffectedFiles_DeleteFile() throws Exception {
        logger_.info("Running testGetAffectedFiles_DeleteFile ...");

        // Create a base line dir with two files
        File dir = FileUtil.createTempDir();

//        try {
//            String file1 = FileUtil.createTempFilename(dir);
//            String file2 = FileUtil.createTempFilename(dir);
//
//            FileUtil.setFileContents(file1, "file1", false);
//            FileUtil.setFileContents(file2, "file2", false);
//
//            // Get list of new files..should be zero on first run
//            IFileActivity activity = new FileDeletedActivity();
//            List firstRun = activity.getAffectedFiles(dir);
//            assertEquals("first run should be empty", 0, firstRun.size());
//
//            // Delete a file from the directory
//            FileUtil.delete(file1);
//
//            // Run the activity again..should report 1 deleted file
//            List secondRun = activity.getAffectedFiles(dir);
//
//            assertEquals(
//                "second run should contain one file", 1, secondRun.size());
//
//            logger_.info("Deleted file activity: "
//                + ArrayUtil.toString(secondRun.toArray()));
//
//            // Run the activity again.. should report no new files
//            List thirdRun = activity.getAffectedFiles(dir);
//            assertEquals(0, thirdRun.size());
//        }
//        finally {
//            FileUtil.removeDir(dir);
//        }
    }
}