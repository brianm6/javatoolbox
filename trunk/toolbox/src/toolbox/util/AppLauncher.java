package toolbox.util;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public final class AppLauncher  {

    private static final Logger logger = Logger.getLogger(AppLauncher.class);
    
    private AppLauncher() {
    }
    
    public static final void launch(String filename) 
        throws IOException, InterruptedException {

        // Only works for windows
        String[] cmdArray = new String[3];
        cmdArray[0] = "cmd";
        cmdArray[1] = "/c";
        cmdArray[2] = "\"" + filename + "\"";
        
        Process p = Runtime.getRuntime().exec(cmdArray);
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();
        int exitValue = ProcessUtil.getProcessOutput(p, stdout, stderr);
        
        if (exitValue > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("Launch failed with command '" + ArrayUtil.toString(cmdArray) + "'\n");
            
            if (StringUtils.isNotEmpty(stdout.toString())) {
                sb.append("stdout:\n");
                sb.append(stdout);
                sb.append("\n");
            }
            
            if (StringUtils.isNotEmpty(stderr.toString())) {
                sb.append("stderr:\n");
                sb.append(stderr);
            }
            
            throw new RuntimeException(sb.toString());
        }
    }
}