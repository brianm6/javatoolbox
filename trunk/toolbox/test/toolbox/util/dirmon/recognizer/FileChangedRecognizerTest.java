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

/**
 * Unit test for {@link FileChangedRecognizer}.
 */
public class FileChangedRecognizerTest extends TestCase {

    private static final Logger logger_ = 
        Logger.getLogger(FileChangedRecognizerTest.class);

    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------

    public static void main(String[] args) {
        TestRunner.run(FileChangedRecognizerTest.class);
    }

    // --------------------------------------------------------------------------
    // Unit Tests
    // --------------------------------------------------------------------------

    public void testGetRecognizedEvents() throws Exception {
        logger_.info("Running testGetRecognizedEvents...");

        // Create a base line dir with two files
        File dir = FileUtil.createTempDir();

        try {
            String file1 = FileUtil.createTempFilename(dir);
            String file2 = FileUtil.createTempFilename(dir);

            FileUtil.setFileContents(file1, "file1", false);
            FileUtil.setFileContents(file2, "file2", false);

            // Get list of new files..should be zero on first run
            DirectoryMonitor dm = new DirectoryMonitor(FileUtil.getTempDir());
            IFileActivityRecognizer recognizer = new FileChangedRecognizer(dm);
            DirSnapshot a = new DirSnapshot(dir);
            DirSnapshot b = new DirSnapshot(dir);
            List firstRun = recognizer.getRecognizedEvents(a, b);
            assertEquals(0, firstRun.size());

            // Append to the file
            FileUtil.setFileContents(file1, "appended text", true);
            b = new DirSnapshot(dir);
            List secondRun = recognizer.getRecognizedEvents(a, b);

            assertEquals(
                "second run should contain one file", 
                1, secondRun.size());

            logger_.debug("Changed file activity: " + secondRun.get(0));

            // Run the activity again.. should report no new files
            DirSnapshot c = new DirSnapshot(dir);
            List thirdRun = recognizer.getRecognizedEvents(b, c);
            assertEquals(0, thirdRun.size());
        }
        finally {
            FileUtils.deleteDirectory(dir);
        }
    }
}