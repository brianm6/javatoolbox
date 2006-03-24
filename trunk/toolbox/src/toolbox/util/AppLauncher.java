package toolbox.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

/**
 * Tries to launch files (data files) if possible. For example, launching a
 * text file on windows will open the file in Notepad.exe.
 */
public final class AppLauncher  {

    private static final Logger logger = Logger.getLogger(AppLauncher.class);
    
    private AppLauncher() {
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    /**
     * Launches the given file. Filename must include the complete absolute
     * path.
     * 
     * @param filename File to launch.
     * @throws IOException on I/O Error.
     */
    public static final void launch(String filename) 
        throws IOException, InterruptedException {

        if (SystemUtils.IS_OS_WINDOWS)
            launchInWindows(filename);
        
        // TODO: Fill in other platform specific launchers here.
        
        else
            throw new IllegalArgumentException("App launching supported on windows only.");
    }
    
    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------
    
    private static final void launchInWindows(String filename) 
        throws IOException, InterruptedException {

        // Only works for windows
        String[] cmdArray = new String[3];
        cmdArray[0] = "cmd";
        cmdArray[1] = "/c";
        cmdArray[2] = "\"" + filename + "\"";
    
        // Add extra set of quotes around filename if it contains any special
        // characters. See cmd /c for more info.
        String specialChars = "&<>()@^|";
        if (StringUtils.indexOfAny(filename, specialChars) >= 0)
            cmdArray[2] = "\"" + cmdArray[2] + "\"";
        
        Process p = Runtime.getRuntime().exec(cmdArray);
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();
        int exitValue = ProcessUtil.getProcessOutput(p, stdout, stderr);
        
        if (exitValue > 0) {
    
            File file = new File(filename);
            
            // Failed...lets try to get around access denied errors on mounted
            // clearcase view if we're certain the file exists and is not a 
            // directory
            
            if (StringUtil.containsIgnoreCase(stderr.toString(), "Access is denied") && 
                            file.exists() && file.isFile()) {
                
                File tmpDir = FileUtil.getTempDir();
                FileUtils.copyFileToDirectory(file, tmpDir);
                File tmpFile = new File(tmpDir, FilenameUtils.getName(filename));
                
                if (tmpFile.exists()) {
                    
                    // launch is async so just queue up to be deleted when the
                    // jvm is exited.
                    FileUtils.forceDeleteOnExit(tmpFile);
                    launch (tmpFile.getAbsolutePath());
                }
                else {
                    throw new RuntimeException("Tried to copy file "
                        + filename
                        + " to tmp dir "
                        + tmpDir
                        + " and launch but failed.");
                }
            }
            else { 
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
}