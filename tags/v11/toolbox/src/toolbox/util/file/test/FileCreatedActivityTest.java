package toolbox.util.file.test;

import java.io.File;

import org.apache.log4j.Category;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.file.FileCreatedActivity;

/**
 * Unit test for FileCreatedActivity
 */
public class FileCreatedActivityTest extends TestCase
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(FileCreatedActivityTest.class);
        
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FileCreatedActivityTest.class);
    }


    /**
     * Constructor for FileCreatedActivityTest.
     * 
     * @param arg0 Name
     */
    public FileCreatedActivityTest(String arg0)
    {
        super(arg0);
    }

    
    /**
     * Tests getFiles()
     */
    public void testGetFiles() throws Exception
    {
        // Create a base line dir with two files
        File dir = FileUtil.createTempDir();
        
        String file1 = FileUtil.getTempFilename(dir);            
        String file2 = FileUtil.getTempFilename(dir);
        
        FileUtil.setFileContents(file1, "file1", false);
        FileUtil.setFileContents(file2, "file2", false);
        
        // Get list of new files..should be zero on first run
        FileCreatedActivity activity = new FileCreatedActivity();
        File[] firstRun = activity.getFiles(dir);
        assertEquals("first run should be empty", 0, firstRun.length);
        
        // Add a file to the baseline dir
        String file3 = FileUtil.getTempFilename(dir);
        FileUtil.setFileContents(file3, "file3", false);
        
        // Run the activity again..should report 1  new file
        File[] secondRun = activity.getFiles(dir);
        assertEquals("second run should contain one file", 1, secondRun.length);
        
        logger_.info("New file activity: " + ArrayUtil.toString(secondRun));
    }
}