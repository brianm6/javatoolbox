package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import toolbox.util.FileUtil;

public class FileUtilTest extends TestCase
{
    /** Logger **/
    private static final Category logger = 
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
        logger.info("Temp dir = " + tempDir);
    }
}
