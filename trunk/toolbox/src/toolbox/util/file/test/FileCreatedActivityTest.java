package toolbox.util.file.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.file.FileCreatedActivity;

/**
 * Unit test for FileCreatedActivity
 */
public class FileCreatedActivityTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(FileCreatedActivityTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FileCreatedActivityTest.class);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests getFiles()
     * 
     * @throws  Exception on error
     */
    public void testGetFiles() throws Exception
    {
        logger_.info("Running testGetFiles...");
        
        // Create a base line dir with two files
        File dir = FileUtil.createTempDir();
        
        try
        {
            String file1 = FileUtil.generateTempFilename(dir);            
            String file2 = FileUtil.generateTempFilename(dir);
            
            FileUtil.setFileContents(file1, "file1", false);
            FileUtil.setFileContents(file2, "file2", false);
            
            // Get list of new files..should be zero on first run
            FileCreatedActivity activity = new FileCreatedActivity();
            File[] firstRun = activity.getFiles(dir);
            assertEquals("first run should be empty", 0, firstRun.length);
            
            // Add a file to the baseline dir
            String file3 = FileUtil.generateTempFilename(dir);
            FileUtil.setFileContents(file3, "file3", false);
            
            // Run the activity again..should report 1  new file
            File[] secondRun = activity.getFiles(dir);
            
            assertEquals(
                "second run should contain one file", 1, secondRun.length);
            
            logger_.info("New file activity: " + ArrayUtil.toString(secondRun));
            
            // Run the activity again.. should report no new files
            File[] thirdRun = activity.getFiles(dir);
            assertEquals(0, thirdRun.length);
        }
        finally
        {
            FileUtil.removeDir(dir);
        }
    }
}