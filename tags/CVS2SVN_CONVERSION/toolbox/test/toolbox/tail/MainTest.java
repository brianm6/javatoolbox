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
    // Positive Unit Tests
    // -------------------------------------------------------------------------
    
    public void testMain_OneFile_LineNumbers() throws Exception  
    {
        logger.info("Running testMain_OneFile_LineNumbers ...");

        File tmpFile = FileUtil.createTempFile();
        FileStuffer tmpFileStuffer = new FileStuffer(tmpFile, 100);
        tmpFileStuffer.start();
         
        try
        {
            ThreadUtil.sleep(1000);
            Main.main(new String[] {"-l", tmpFile.getAbsolutePath()});
            ThreadUtil.sleep(1000);
        }
        finally
        {
            tmpFileStuffer.stop();
            FileUtil.deleteQuietly(tmpFile);
        }
    }

    public void testMain_OneFile_PrefixFilename() throws Exception  
    {
        logger.info("Running testMain_OneFile_PrefixFilename...");
        
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

    public void testMain_OneFile() throws Exception  
    {
        logger.info("Running testMain_OneFile ...");

        File tmpFile = FileUtil.createTempFile();
        FileStuffer tmpFileStuffer = new FileStuffer(tmpFile, 100);
        tmpFileStuffer.start();
         
        try
        {
            ThreadUtil.sleep(1000);
            Main.main(new String[] {tmpFile.getAbsolutePath()});
            ThreadUtil.sleep(1000);
        }
        finally
        {
            tmpFileStuffer.stop();
            FileUtil.deleteQuietly(tmpFile);
        }
    }

    public void testMain_OneFile_PrefixFilename_and_LineNumbers() throws Exception  
    {
        logger.info("Running testMain_OneFile_PrefixFilename_and_LineNumbers...");
        
        File tmpFile = FileUtil.createTempFile();
        FileStuffer tmpFileStuffer = new FileStuffer(tmpFile, 100);
        tmpFileStuffer.start();
         
        try
        {
            ThreadUtil.sleep(1000);
            Main.main(new String[] {"-f", "-l", tmpFile.getAbsolutePath()});
            ThreadUtil.sleep(1000);
        }
        finally
        {
            tmpFileStuffer.stop();
            FileUtil.deleteQuietly(tmpFile);
        }
    }
    
    //--------------------------------------------------------------------------
    // Negative Unit Tests
    //--------------------------------------------------------------------------
    
    public void testMain_NoArgs() throws Exception
    {
        logger.info("Running testMain_NoArgs...");
        
        Main.main(new String[0]);
    }
    
}
