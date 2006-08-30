package toolbox.tail;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.file.FileStuffer;

/**
 * Unit test for {@link MainTest}.
 * 
 * @author x1700
 */
public class MainTest extends TestCase 
{
    private static final Logger logger = Logger.getLogger(MainTest.class);
    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static void main(String[] args) 
    {
        TestRunner.run(MainTest.class);
    }

    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------
    
    public void testMain_PrefixWithFilename() throws Exception  
    {
        logger.info("Running testMain_PrefixWithFilename ...");

        File tmpFile = FileUtil.createTempFile();
        FileStuffer tmpFileStuffer = new FileStuffer(tmpFile, 100);
        tmpFileStuffer.start();
         
        try
        {
            ThreadUtil.sleep(1000);
            Main.main(new String[] {"-f", tmpFile.getAbsolutePath()});
            ThreadUtil.sleep(1000);
        }
        finally
        {
            tmpFileStuffer.stop();
            FileUtil.deleteQuietly(tmpFile);
        }
    }
    
    public void testMain() throws Exception  
    {
        logger.info("Running testMain ...");

        File tmpFile = FileUtil.createTempFile();
        FileStuffer tmpFileStuffer = new FileStuffer(tmpFile, 100);
        tmpFileStuffer.start();
         
        try
        {
            ThreadUtil.sleep(1000);
            Main.main(new String[] {"-f", tmpFile.getAbsolutePath()});
            ThreadUtil.sleep(1000);
        }
        finally
        {
            tmpFileStuffer.stop();
            FileUtil.deleteQuietly(tmpFile);
        }
    }
}
