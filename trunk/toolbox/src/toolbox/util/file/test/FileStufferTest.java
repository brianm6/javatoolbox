package toolbox.util.file.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.file.FileStuffer;

/**
 * Unit test for FileStuffer
 */
public class FileStufferTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(FileStufferTest.class);
        
    /**
     * Entrypoint
     *
     * @param  args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FileStufferTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for FileStufferTest.
     * 
     * @param arg0  Name
     */
    public FileStufferTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests running the filestuffer
     */
    public void testFileStuffer() throws Exception
    {
        logger_.info("Running testFileStuffer...");
        
        File tmpDir = FileUtil.getTempDir();
        File outfile = new File(tmpDir, "outfile");
        FileStuffer fs = new FileStuffer(outfile, 500);

        fs.start();
        
        ThreadUtil.sleep(5000);
        
        fs.stop();
        
        String contents = FileUtil.getFileContents(outfile.getAbsolutePath());

        System.out.println(contents);                                           
        
        outfile.delete();
    }
}
