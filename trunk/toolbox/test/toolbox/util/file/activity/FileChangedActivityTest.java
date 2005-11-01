package toolbox.util.file.activity;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.file.IFileActivity;
import toolbox.util.file.activity.FileChangedActivity;

/**
 * Unit test for {@link toolbox.util.file.activity.FileChangedActivity}.
 */
public class FileChangedActivityTest extends TestCase {

    private static final Logger logger_ = 
        Logger.getLogger(FileChangedActivityTest.class);

    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------

    public static void main(String[] args) {
        TestRunner.run(FileChangedActivityTest.class);
    }

    // --------------------------------------------------------------------------
    // Unit Tests
    // --------------------------------------------------------------------------

    public void testGetAffectedFiles_ChangedFile() throws Exception {
        logger_.info("Running testGetAffectedFiles_DeleteFile ...");

        // Create a base line dir with two files
        File dir = FileUtil.createTempDir();

        try {
            String file1 = FileUtil.createTempFilename(dir);
            String file2 = FileUtil.createTempFilename(dir);

            FileUtil.setFileContents(file1, "file1", false);
            FileUtil.setFileContents(file2, "file2", false);

            // Get list of new files..should be zero on first run
            IFileActivity activity = new FileChangedActivity();
            List firstRun = activity.getAffectedFiles(dir);
            assertEquals("first run should be empty", 0, firstRun.size());

            // Append to the file
            FileUtil.setFileContents(file1, "appended text", true);

            // Run the activity again..should report 1 changed file
            List secondRun = activity.getAffectedFiles(dir);

            assertEquals(
                "second run should contain one file", 1, secondRun.size());

            logger_.info("Changed file activity: "
                + ArrayUtil.toString(secondRun.toArray()));

            // Run the activity again.. should report no new files
            List thirdRun = activity.getAffectedFiles(dir);
            assertEquals(ArrayUtil.toString(thirdRun.toArray()), 0, thirdRun.size());
        }
        finally {
            FileUtil.removeDir(dir);
        }
    }
}