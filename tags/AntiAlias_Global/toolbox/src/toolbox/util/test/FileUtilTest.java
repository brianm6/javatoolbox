package toolbox.util.test;

import java.io.File;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;

/**
 * Unit test for FileUtil
 */
public class FileUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(FileUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Runs the test case in text mode
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FileUtilTest.class);
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
        FileUtil.getFileContents(tempFile); 
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
        String currentContents = FileUtil.getFileContents(file);
        
        // Compare
        assertEquals("contents should be equals", contents, currentContents);
        
        // Clean up
        new File(file).delete();
    }

    /**
     * Tests getFileAsBytes()
     * 
     * @throws Exception on error
     */
    public void testGetFileAsBytes() throws Exception
    {
        logger_.info("Running testGetFileAsBytes...");
        
        String file = FileUtil.getTempFilename();
        
        try
        {
            String contents = "blah blah blah";
            FileUtil.setFileContents(file, contents, false);
            byte[] currentContents = FileUtil.getFileAsBytes(file);
            
            assertEquals("File contents should be equal", contents, 
                new String(currentContents));
        }
        finally
        {
            FileUtil.delete(file);
        }
    }

    /**
     * Tests getFileContents() for a large file (500k)
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
        
        reread.delete();
    }

    /**
     * Tests setFileContents()
     * 
     * @throws Exception on error
     */
    public void testSetFileContentsBytes() throws Exception
    {
        logger_.info("Running testSetFileContentsBytes...");
        
        // Create a file
        String file = FileUtil.getTempFilename();
        byte[] contents = "blah blah blah".getBytes();
        FileUtil.setFileContents(file, contents, false);
        
        // Read it back in
        byte[] currentContents = FileUtil.getFileAsBytes(file);
        
        // Compare
        assertEquals("contents should be equals", 
            new String(contents), 
                new String(currentContents));
                
        new File(file).delete();
    }

    /**
     * Tests setFileContents(File)
     * 
     * @throws Exception on error
     */
    public void testSetFileContents2() throws Exception
    {
        logger_.info("Running testSetFileContents2...");
        
        // Create a file
        String file = FileUtil.getTempFilename();
        String contents = "blah blah blah";
        FileUtil.setFileContents(new File(file), contents, false);
        
        // Read it back in
        File reread = new File(file);
        String currentContents = FileUtil.getFileContents(file);
        
        // Compare
        assertEquals("contents should be equals", contents, currentContents);
        logger_.info("Passed: setFileContents2 on " + file);
        
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
 
        try
        {
            // Make src file
            String srcFilename = FileUtil.getTempFilename(srcDir);
            File   srcFile     = new File(srcFilename);
            String srcContents =  "test file for move";
            FileUtil.setFileContents(srcFilename, srcContents, false);
    
            // Take snapshot before file move
            String[] beforeMoveSrc = srcDir.list();
            String[] beforeMoveDest= destDir.list();
            
            logger_.info(
                "Before move:  src=" + ArrayUtil.toString(beforeMoveSrc));
                
            logger_.info(
                "Before move: dest=" + ArrayUtil.toString(beforeMoveDest));
            
            assertEquals(
                "should be one file in src dir", 1, beforeMoveSrc.length);
                
            assertEquals(
                "should be zero files in dest dir", 0, beforeMoveDest.length);
    
            // Move file
            FileUtil.moveFile(srcFile, destDir);
    
            // Take snapshot again
            String[] afterMoveSrc = srcDir.list();
            String[] afterMoveDest= destDir.list();
    
            logger_.info(
                "After move:  src=" + ArrayUtil.toString(afterMoveSrc));
                
            logger_.info(
                "After move: dest=" + ArrayUtil.toString(afterMoveDest));
                    
            assertEquals(
                "should be zero files in src dir", 0, afterMoveSrc.length);
                
            assertEquals(
                "should be one file in dest dir", 1, afterMoveDest.length);
    
            // Compare contents of moved file
            String destContents = 
                FileUtil.getFileContents(
                    destDir.listFiles()[0].getAbsolutePath());
                
            assertEquals("contents of moved file should be the same", 
                srcContents, destContents);
                
            logger_.info("Passed: moveFile");
        }
        finally
        {
            // Cleanup
            
            FileUtil.cleanDir(destDir);
            srcDir.delete();
            destDir.delete();
        }
    }

    /**
     * Tests trailWithSeparator() with the separator missing
     */
    public void testTrailWithSeparatorMissing()
    {
        logger_.info("Running testTrailWithSeparatorMissing...");
        
        String path = File.separator + "java";
        String trailed = FileUtil.trailWithSeparator(path);
        assertEquals(path + File.separator, trailed);
    }    
    
    /**
     * Tests trailWithSeparator() with separator already there
     */
    public void testTrailWithSeparatorAlreadyExists()
    {
        logger_.info("Running testTrailWithSeparatorAlreadyExists...");
        
        String path = File.separator + "java" + File.separator;
        String trailed = FileUtil.trailWithSeparator(path);
        assertEquals(path, trailed);
    }    
    
    /**
     * Tests matchPlatformSeparator()
     */
    public void testMatchPlatformSeparator()
    {
        logger_.info("Running testMatchPlatformSeparator...");
        
        String match = File.separator + "a" + File.separator + "b";
        assertEquals(match, FileUtil.matchPlatformSeparator("\\a\\b"));
        assertEquals(match, FileUtil.matchPlatformSeparator("/a/b"));
    }
    
    /**
     * Tests dropExtension()
     */
    public void testDropExtension()
    {
        logger_.info("Running testDropExtension...");
 
        assertEquals("foobar", FileUtil.dropExtension("foobar"));
        assertEquals("foobar", FileUtil.dropExtension("foobar.txt"));
        assertEquals("foobar.old", FileUtil.dropExtension("foobar.old.txt"));
    }
    
    /**
     * Tests stripPath()
     */
    public void testStripPath()
    {
        logger_.info("Running testStripPath...");
        
        String s = File.separator;
        
        assertEquals("file.txt", FileUtil.stripPath("c:" + s + "file.txt"));
        assertEquals("file.txt", FileUtil.stripPath(".." + s + "file.txt"));
        assertEquals("file.txt", FileUtil.stripPath("file.txt"));
        assertEquals("file.txt", FileUtil.stripPath(s + "file.txt"));
        assertEquals("a", FileUtil.stripPath("a"));
        assertEquals("", FileUtil.stripPath(""));
        assertEquals("file.txt", FileUtil.stripPath(
            "c:" + s + "a" + s + "c" + s + ".." + s + "file.txt"));
    }
    
    /**
     * Tests stripFile()
     */
    public void testStripFile()
    {
        logger_.info("Running testStripFile...");
        
        String s = File.separator;
        
        assertEquals("c:", FileUtil.stripFile("c:" + s + "file.txt"));
        assertEquals("..", FileUtil.stripFile(".." + s + "file.txt"));
        assertEquals("", FileUtil.stripFile("file.txt"));
        assertEquals("", FileUtil.stripFile(s + "file.txt"));
        assertEquals("", FileUtil.stripFile("a"));
        assertEquals("", FileUtil.stripFile(""));
        
        assertEquals(
            "c:" + s + "a" + s + "c" + s + "..", 
            FileUtil.stripFile(
                "c:" + s + "a" + s + "c" + s + ".." + s + "file.txt"));
    }
    
    /**
     * Tests delete()
     * 
     * @throws Exception on error
     */
    public void testDelete() throws Exception
    {
        logger_.info("Running testDelete...");
        
        String file = FileUtil.getTempFilename();
        FileUtil.setFileContents(file, "test data", false);
        File f = new File(file);
        assertTrue(f.exists());
        FileUtil.delete(file);
        assertTrue(!f.exists());
    }
}