package toolbox.util;

import java.io.OutputStream;

import org.apache.log4j.Logger;

import toolbox.util.io.StringOutputStream;
import toolbox.util.io.relay.StreamRelay;

/**
 * Process related utility methods.
 */
public final class ProcessUtil {

    // TODO: Write unit test
    
    private static final Logger logger_ = Logger.getLogger(ProcessUtil.class);

    
    private ProcessUtil() {
    }

    /**
     * Extracts the stdout and stderr text from the execution of a process via
     * {@link Runtime#exec(java.lang.String)}. Returns the exit value returned
     * from the process.
     * 
     * @param process Process returned from Runtime.exec(...)
     * @param stdout String buffer that the process' stdout will be appended to.
     * @param stderr String buffer that the process' stderr will be appended to.
     * @return int 
     * @throws InterruptedException on error.
     */
    public static int getProcessOutput(
        Process process,
        StringBuffer stdout,
        StringBuffer stderr) 
        throws InterruptedException {
        
        OutputStream stdoutStream = new StringOutputStream();
        OutputStream stderrStream = new StringOutputStream();
        int exitValue = getProcessOutput(process, stdoutStream, stderrStream);
        stdout.append(stdoutStream.toString());
        stderr.append(stderrStream.toString());
        return exitValue;
    }

    
    /**
     * Redirects the stdout and stderr streams from the execution of a process 
     * via {@link Runtime#exec(java.lang.String)}. Returns the exit value 
     * returned from the process.
     * 
     * @param process Process returned from Runtime.exec(...)
     * @param stdout Stream that the process' stdout will be written to.
     * @param stderr Stream that the process' stderr will be written to.
     * @return int 
     * @throws InterruptedException on error.
     */
    public static int getProcessOutput(
        Process process,
        OutputStream stdout,
        OutputStream stderr) 
        throws InterruptedException {
        
        StreamRelay stdoutRelay = new StreamRelay(process.getInputStream(), stdout);
        Thread stdoutThread = new Thread(stdoutRelay, "stdoutRelay");
        stdoutThread.start();
        
        StreamRelay stderrRelay = new StreamRelay(process.getErrorStream(), stderr);
        Thread stderrThread = new Thread(stderrRelay, "stderrRelay");
        stderrThread.start();
        
        logger_.trace("Waiting for exit...");
        int exitValue = process.waitFor();
    
        logger_.trace("Joining stdout thread...");
        stdoutThread.join();
        
        logger_.trace("Joining stderr thread...");
        stderrThread.join();
        return exitValue;
    }
}