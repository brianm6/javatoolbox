package toolbox.util.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;

/**
 * Unit test for FileUtil
 */
public class FileUtilTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(FileUtilTest.class);

    /**
     * Runs the test case in text mode
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(FileUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
                    
    /**
     * Constructor for FileUtilTest.
     * @param arg0  Name
     */
    public FileUtilTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the getTempDir() method 
     * 
     * @throws Exception on error
     */    
    public void testGetTempDir() throws Exception
    {
        logger_.info("Running testGetTempDir...");
        
        String tempDir = FileUtil.getTempDir().getCanonicalPath();
        File file = new File(tempDir);
        assertTrue("temp is not a directory", file.isDirectory());
        logger_.info("Passed: Temp dir = " + tempDir);
    }

    /**
     * Tests the getTempFilename() method 
     * 
     * @throws Exception on error
     */    
    public void testGetTempFilename() throws Exception
    {
        logger_.info("Running testGetTempFilename...");
        
        // Generate temp file name
        String tempFile = FileUtil.getTempFilename();
        assertNotNull("temp filename is null", tempFile);
        
        // Use temp file name to create a file
        FileUtil.setFileContents(tempFile, "this is a temp file", false);
        String contents = FileUtil.getFileContents(tempFile); 
        File file = new File(tempFile);
        file.delete();
        
        logger_.info("Passed: Created temp file " + tempFile);
    }
    
    /**
     * Tests cleanDir() for failure by passing a file instead of a directory
     * 
     * @throws Exception on error
     */
    public void testCleanDirFailure1() throws Exception
    {
        logger_.info("Running testCleanDirFailure1...");
        
        // Create a file
        String file = FileUtil.getTempFilename();
        FileUtil.setFileContents(file, "hello", false);
        File f = new File(file);
        
        try
        {
            FileUtil.cleanDir(f);
            fail("Should have failed on a file, not a directory");
        }
        catch (IllegalArgumentException e)
        {
            logger_.info("Passed: " + e);
        }
        finally
        {
            f.delete();    
        }
    }
    
    /**
     * Tests cleanDir() for failure by passing in a non-existant directory
     * 
     * @throws Exception on error
     */
    public void testCleanDirFailure2() throws Exception
    {
        logger_.info("Running testCleanDirFailure2...");
        
        // Create a bogus dir name
        String dir = FileUtil.getTempFilename();
        
        try
        {
            FileUtil.cleanDir(new File(dir));
            fail("Should have failed on a non-existant directory");
        }
        catch (IllegalArgumentException e)
        {
            logger_.info("Passed: " + e);
        }
    }
    
    /**
     * Tests cleanDir() for cleaning the contents of a single directory
     * 
     * @throws Exception on error
     */
    public void testCleanDirFailure() throws Exception
    {
        logger_.info("Running testCleanDirFailure...");
        
        int numFiles = 10;
        
        // Create a directory
        String dirName = FileUtil.getTempFilename();
        File dir = new File(dirName);
        dir.mkdir();
        
        // Populate with files
        for (int i=0; i< numFiles; i++)
        {
            String filename = i + ".txt";
            File  file = new File(dir, filename);
            FileUtil.setFileContents(
                file.getAbsolutePath(), "testing..", false);
        }

        // Verify test files created
        String[] before = dir.list();
        logger_.info("Contents before: " + ArrayUtil.toString(before));
        assertEquals("Dir " + dir + " should have files", 
            numFiles, before.length); 

        // Nuke the directory
        try
        {        
            FileUtil.cleanDir(dir);
        }
        finally
        {
            // cleanup
        }
        
        // Verify no files left
        String[] after = dir.list();
        logger_.info("Contents after: " + ArrayUtil.toString(after));
        assertEquals("No files should be left in " + dir, 0, after.length);
    }
    
    /**
     * Tests getFileContents()
     * 
     * @throws Exception on error
     */
    public void testGetFileContents() throws Exception
    {
        logger_.info("Running testGetFileContents...");
        
        // Create a file
        String file = FileUtil.getTempFilename();
        String contents = "blah blah blah";
        FileUtil.setFileContents(file, contents, false);
        
        // Read it back in
        File reread = new File(file);
        String currentContents = FileUtil.getFileContents(file);
        
        // Compare
        assertEquals("contents should be equals", contents, currentContents);
        logger_.info("Passed: getFileContents on " + file);        
        
        // Clean up
        reread.delete();
    }

    /**
     * Tests getFileContents() for a large file
     * 
     * @throws Exception on error
     */
    public void testGetFileContentsLargeFile() throws Exception
    {
        logger_.info("Running testGetFileContentsLargeFile...");
        
        // Half meg file
        int fileSize = 500000;
        
        // Create a file
        String file = FileUtil.getTempFilename();
        StringBuffer contents = new StringBuffer();
        for(int i=0; i<fileSize; i++)
            contents.append(RandomUtil.nextAlpha());
        FileUtil.setFileContents(file, contents.toString(), false);
        
        // Read it back in
        String currentContents = FileUtil.getFileContents(file);
        
        // Compare
        assertEquals("contents should be equals", 
            contents.toString(), currentContents);
        
        logger_.info("Passed: " + file + " length " + currentContents.length());
        
        // Clean up
        File reread = new File(file);        
        reread.delete();
    }

    /**
     * Tests setFileContents()
     * 
     * @throws Exception on error
     */
    public void testSetFileContents() throws Exception
    {
        logger_.info("Running testSetFileContents...");
        
        // Create a file
        String file = FileUtil.getTempFilename();
        String contents = "blah blah blah";
        FileUtil.setFileContents(file, contents, false);
        
        // Read it back in
        File reread = new File(file);
        String currentContents = FileUtil.getFileContents(file);
        
        // Compare
        assertEquals("contents should be equals", contents, currentContents);
        logger_.info("Passed: setFileContents on " + file);
        
        // Clean up
        reread.delete();
    }
    
    /**
     * Tests moveFile() for simple case
     * 
     * @throws Exception on error
     * 
     * <pre>
     * 
     * tmpdir
     *   |
     *   +-sourcedir
     *   |    | 
     *   |    +-file.txt <== move from
     *   |
     *   |
     *   +-destdir       <== move to
     * 
     * 
     * </pre>
     */
    public void testMoveFile() throws Exception
    {
        logger_.info("Running testMoveFile..."); 
        
        // Make src dir
        String srcDirName = FileUtil.getTempFilename();
        File   srcDir     = new File(srcDirName);
        srcDir.mkdir();
        
        // Make dest dir
        String destDirName = FileUtil.getTempFilename();
        File   destDir     = new File(destDirName);
        destDir.mkdir();
 
        // Make src file
        String srcFilename = FileUtil.getTempFilename(srcDir);
        File   srcFile     = new File(srcFilename);
        String srcContents =  "test file for move";
        FileUtil.setFileContents(srcFilename, srcContents, false);

        // Take snapshot before file move
        String[] beforeMoveSrc = srcDir.list();
        String[] beforeMoveDest= destDir.list();
        
        logger_.info("Before move:  src=" + ArrayUtil.toString(beforeMoveSrc));
        logger_.info("Before move: dest=" + ArrayUtil.toString(beforeMoveDest));
        
        assertEquals("should be one file in src dir", 1, beforeMoveSrc.length);
        assertEquals("should be zero files in dest dir", 
            0, beforeMoveDest.length);

        // Move file
        FileUtil.moveFile(srcFile, destDir);

        // Take snapshot again
        String[] afterMoveSrc = srcDir.list();
        String[] afterMoveDest= destDir.list();

        logger_.info("After move:  src=" + ArrayUtil.toString(afterMoveSrc));
        logger_.info("After move: dest=" + ArrayUtil.toString(afterMoveDest));
                
        assertEquals("should be zero files in src dir", 0, afterMoveSrc.length);
        assertEquals("should be one file in dest dir", 1, afterMoveDest.length);

        // Compare contents of moved file
        String destContents = 
            FileUtil.getFileContents(destDir.listFiles()[0].getAbsolutePath());
            
        assertEquals("contents of moved file should be the same", 
            srcContents, destContents);
            
        logger_.info("Passed: moveFile");

        // TODO: add to finally block
        FileUtil.cleanDir(destDir);
        srcDir.delete();
        destDir.delete();
    }
}