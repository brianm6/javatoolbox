package toolbox.util;

import java.io.IOException;

import org.apache.log4j.Logger;

public final class AppLauncher  {

    private static final Logger logger = Logger.getLogger(AppLauncher.class);
    
    private AppLauncher() {
    }

    
    public static final void launch(String filename) 
        throws IOException, InterruptedException {

        // Only works for windows
        String command = "cmd /c \"" + filename + "\"";
        
        Process p = Runtime.getRuntime().exec(command);
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();
        int exitValue = ProcessUtil.getProcessOutput(p, stdout, stderr);
        logger.info("Launch Output: " + stdout);
        logger.info("Launch Error: " + stderr);
        logger.info("Launch Exit value: " + exitValue);
    }
}
