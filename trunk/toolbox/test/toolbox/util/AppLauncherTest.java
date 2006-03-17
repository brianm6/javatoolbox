package toolbox.util;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class AppLauncherTest extends TestCase {

    public static final Logger logger = Logger.getLogger(AppLauncherTest.class);
    
    public static void main(String[] args) {
        TestRunner.run(AppLauncherTest.class);
    }
    
    public void testLaunch() throws Exception {
        logger.info("Running testLaunch...");
        
        File tempDir = FileUtil.createTempDir();
        logger.info("tempDir = " + tempDir);
        File tempFile = new File(tempDir, "AppLauncherTestFile.txt");
        
        try {
            FileUtils.writeStringToFile(tempFile, "This is the content of " + tempFile.getAbsolutePath(), "UTF8");
            String verify = FileUtils.readFileToString(tempFile, "UTF8");
            logger.info("Verified: " + verify);
            logger.info("Absolute path = " + tempFile.getAbsolutePath());
            AppLauncher.launch(tempFile.getAbsolutePath());
        }
        finally {
            FileUtils.deleteDirectory(tempDir);
        }
    }

}
