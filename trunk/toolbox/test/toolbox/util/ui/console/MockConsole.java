package toolbox.util.ui.console;

import java.io.InterruptedIOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * MockConsole
 */
public class MockConsole implements Console
{
    private static final Logger logger_ = Logger.getLogger(MockConsole.class);
    
    /**
     * Creates a MockConsole.
     */
    public MockConsole()
    {
        super();
    }


    /**
     * @see toolbox.util.ui.console.Console#getName()
     */
    public String getName()
    {
        return "MockConsole";
    }


    /**
     * @see toolbox.util.ui.console.Console#read()
     */
    public String read() throws InterruptedIOException
    {
        return "read";
    }


    /**
     * @see toolbox.util.ui.console.Console#write(java.lang.String)
     */
    public void write(String msg)
    {
        logger_.debug(StringUtils.chomp(msg));
    }


    /**
     * @see toolbox.util.ui.console.Console#getCommandHandler()
     */
    public CommandHandler getCommandHandler()
    {
        return null;
    }


    /**
     * @see toolbox.util.ui.console.Console#clear()
     */
    public void clear()
    {
    }


    /**
     * @see toolbox.util.ui.console.Console#setPrompt(java.lang.String)
     */
    public void setPrompt(String prompt)
    {
    }
}
