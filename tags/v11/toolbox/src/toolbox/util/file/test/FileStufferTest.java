package toolbox.util.file.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.file.FileStuffer;

/**
 * Unit test for FileStuffer
 */
public class FileStufferTest extends TestCase
{
    /**
     * Entrypoint
     *
     * @param  args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FileStufferTest.class);
    }
    
    
    /**
     * Constructor for FileStufferTest.
     * 
     * @param arg0  Name
     */
    public FileStufferTest(String arg0)
    {
        super(arg0);
    }
    
    
    /**
     * Tests running the filestuffer
     */
    public void testFileStuffer() throws Exception
    {
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
