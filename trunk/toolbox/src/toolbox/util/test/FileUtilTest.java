package toolbox.util.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;

/**
 * Unit test for FileUtil
 */
public class FileUtilTest extends TestCase
{
    /** Logger **/
	private static final Category logger_ = 
	    Category.getInstance(FileUtilTest.class);
                
    /**
     * Constructor for FileUtilTest.
     * @param arg0
     */
    public FileUtilTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Runs the test case in text mode
     */
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        TestRunner.run(FileUtilTest.class);
    }

    /**
     * Tests the getTempDir() method 
     */    
    public void testGetTempDir() throws Exception
    {
        String tempDir = FileUtil.getTempDir().getCanonicalPath();
        File file = new File(tempDir);
        assertTrue("temp is not a directory", file.isDirectory());
        logger_.info("Passed: Temp dir = " + tempDir);
    }

    /**
     * Tests the getTempFilename() method 
     */    
    public void testGetTempFilename() throws Exception
    {
        /* generate temp file name */
        String tempFile = FileUtil.getTempFilename();
        assertNotNull("temp filename is null", tempFile);
        
        /* use temp file name to create a file */
        FileUtil.setFileContents(tempFile, "this is a temp file", false);
        String contents = FileUtil.getFileContents(tempFile); 
        File file = new File(tempFile);
        file.delete();
        
        logger_.info("Passed: Created temp file " + tempFile);
    }
    
    /**
     * Tests cleanDir() for failure by passing a file instead of a directory
     */
    public void testCleanDirFailure1() throws Exception
    {
        /* create a file */
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
     */
    public void testCleanDirFailure2() throws Exception
    {
        /* create a bogus dir name */
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
     */
    public void testCleanDirFailure() throws Exception
    {
        int numFiles = 10;
        
        /* create a directory */
        String dirName = FileUtil.getTempFilename();
        File dir = new File(dirName);
        dir.mkdir();
        
        /* populate with files */
        for (int i=0; i< numFiles; i++)
        {
            String filename = i + ".txt";
            File  file = new File(dir, filename);
            FileUtil.setFileContents(file.getAbsolutePath(), "testing..", false);
        }

        /* verify test files created */
        String[] before = dir.list();
        logger_.info("Contents before: " + ArrayUtil.toString(before));
        assertEquals("Dir " + dir + " should have files", numFiles, before.length); 

        /* nuke the directory */
        try
        {        
            FileUtil.cleanDir(dir);
        }
        finally
        {
            /* cleanup */
        }
        
        /* verify no files left */
        String[] after = dir.list();
        logger_.info("Contents after: " + ArrayUtil.toString(after));
        assertEquals("No files should be left in " + dir, 0, after.length);
    }
    
    /**
     * Tests getFileContents()
     */
    public void testGetFileContents() throws Exception
    {
        /* create a file */
        String file = FileUtil.getTempFilename();
        String contents = "blah blah blah";
        FileUtil.setFileContents(file, contents, false);
        
        /* read it back in */
        File reread = new File(file);
        String currentContents = FileUtil.getFileContents(file);
        
        /* compare */
        assertEquals("contents should be equals", contents, currentContents);
        logger_.info("Passed: getFileContents on " + file);        
        
        /* clean up */
        reread.delete();
    }

    /**
     * Tests getFileContents() for a large file
     */
    public void testGetFileContentsLargeFile() throws Exception
    {
        /* half meg file */
        int fileSize = 500000;
        
        /* create a file */
        String file = FileUtil.getTempFilename();
        StringBuffer contents = new StringBuffer();
        for(int i=0; i<fileSize; i++)
            contents.append(RandomUtil.nextAlpha());
        FileUtil.setFileContents(file, contents.toString(), false);
        
        /* read it back in */
        String currentContents = FileUtil.getFileContents(file);
        
        /* compare */
        assertEquals("contents should be equals", 
            contents.toString(), currentContents);
        
        logger_.info("Passed: " + file + " length " + currentContents.length());
        
        /* clean up */
        File reread = new File(file);        
        reread.delete();
    }

    /**
     * Tests setFileContents()
     */
    public void testSetFileContents() throws Exception
    {
        /* create a file */
        String file = FileUtil.getTempFilename();
        String contents = "blah blah blah";
        FileUtil.setFileContents(file, contents, false);
        
        /* read it back in */
        File reread = new File(file);
        String currentContents = FileUtil.getFileContents(file);
        
        /* compare */
        assertEquals("contents should be equals", contents, currentContents);
        logger_.info("Passed: setFileContents on " + file);
        
        /* clean up */
        reread.delete();
    }
    
    /**
     * Tests moveFile() for simple case
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
        /* make src dir */
        String srcDirName = FileUtil.getTempFilename();
        File   srcDir     = new File(srcDirName);
        srcDir.mkdir();
        
        /* make dest dir */
        String destDirName = FileUtil.getTempFilename();
        File   destDir     = new File(destDirName);
        destDir.mkdir();
 
        /* make src file */       
        String srcFilename = FileUtil.getTempFilename(srcDir);
        File   srcFile     = new File(srcFilename);
        String srcContents =  "test file for move";
        FileUtil.setFileContents(srcFilename, srcContents, false);

        /* take snapshot before file move */     
        String[] beforeMoveSrc = srcDir.list();
        String[] beforeMoveDest= destDir.list();
        
        logger_.info("Before move:  src=" + ArrayUtil.toString(beforeMoveSrc));
        logger_.info("Before move: dest=" + ArrayUtil.toString(beforeMoveDest));
        
        assertEquals("should be one file in src dir", 1, beforeMoveSrc.length);
        assertEquals("should be zero files in dest dir", 0, beforeMoveDest.length);

        /* move file */     
        FileUtil.moveFile(srcFile, destDir);

        /* take snapshot again */
        String[] afterMoveSrc = srcDir.list();
        String[] afterMoveDest= destDir.list();

        logger_.info("After move:  src=" + ArrayUtil.toString(afterMoveSrc));
        logger_.info("After move: dest=" + ArrayUtil.toString(afterMoveDest));
                
        assertEquals("should be zero files in src dir", 0, afterMoveSrc.length);
        assertEquals("should be one file in dest dir", 1, afterMoveDest.length);

        /* compare contents of moved file */
        String destContents = 
            FileUtil.getFileContents(destDir.listFiles()[0].getAbsolutePath());
            
        assertEquals("contents of moved file should be the same", 
            srcContents, destContents);
            
        logger_.info("Passed: moveFile");

        /* TODO: add to finally block */        
        FileUtil.cleanDir(destDir);
        srcDir.delete();
        destDir.delete();
    }
}
