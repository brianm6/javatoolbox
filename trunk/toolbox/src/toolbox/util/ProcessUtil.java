package toolbox.util;

import java.io.OutputStream;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.log4j.Logger;

import toolbox.tunnel.Relay;
import toolbox.util.io.StringOutputStream;

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
     * @param process Process returned from Runtime.exect(...)
     * @param stdout String buffer that the process' stdout will be appeneded to.
     * @param stderr String buffer that the process' stderr will be appeneded to.
     * @return int 
     * @throws InterruptedException on error.
     */
    public static int getProcessOutput(
        Process process,
        StringBuffer stdout,
        StringBuffer stderr) 
        throws InterruptedException {
        
        OutputStream stdoutStream = new StringOutputStream();
        Relay stdoutRelay = new Relay(process.getInputStream(), stdoutStream);
        Thread stdoutThread = new Thread(stdoutRelay, "stdoutRelay");
        stdoutThread.start();
        
        OutputStream stderrStream = new StringOutputStream();
        Relay stderrRelay = new Relay(process.getErrorStream(), stderrStream);
        Thread stderrThread = new Thread(stderrRelay, "stderrRelay");
        stderrThread.start();
        
        logger_.trace("Waiting for exit...");
        int exitValue = process.waitFor();
    
        logger_.trace("Joining stdout thread...");
        stdoutThread.join();
        
        logger_.trace("Joining stderr thread...");
        stderrThread.join();
        
        stdout.append(stdoutStream.toString());
        stderr.append(stderrStream.toString());
        
        return exitValue;
    }
}