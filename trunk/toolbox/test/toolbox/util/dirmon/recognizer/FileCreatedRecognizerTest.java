package toolbox.util.dirmon.recognizer;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;

/**
 * Unit test for {@link FileCreatedRecognizer}.
 */
public class FileCreatedRecognizerTest extends TestCase {

    private static final Logger logger_ = 
        Logger.getLogger(FileCreatedRecognizerTest.class);

    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------

    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args) {

        TestRunner.run(FileCreatedRecognizerTest.class);
    }

    // --------------------------------------------------------------------------
    // Unit Tests
    // --------------------------------------------------------------------------

    /**
     * Tests getFiles().
     * 
     * @throws Exception on error.
     */
    public void testAffectedGetFiles() throws Exception {

        logger_.info("Running testGetFiles...");

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
//            FileCreatedActivity activity = new FileCreatedActivity();
//            List firstRun = activity.getAffectedFiles(dir);
//            assertEquals("first run should be empty", 0, firstRun.size());
//
//            // Add a file to the baseline dir
//            String file3 = FileUtil.createTempFilename(dir);
//            FileUtil.setFileContents(file3, "file3", false);
//
//            // Run the activity again..should report 1 new file
//            List secondRun = activity.getAffectedFiles(dir);
//
//            assertEquals("second run should contain one file", 1, secondRun
//                .size());
//
//            logger_.debug("New file activity: "
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