package toolbox.util.ui.console;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.log4j.Logger;

/**
 * TextConsole is a character based console.
 */
public class TextConsole extends AbstractConsole
{
    private static final Logger logger_ = Logger.getLogger(TextConsole.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Read input from here.
     */
    private BufferedReader stdin_;

    /**
     * Write output here. 
     */
    private BufferedWriter stdout_;

    /**
     * Command prompt
     */
    private String prompt_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a new console
     * 
     * @param useName Console name.
     * @param in Reader to read input from.
     * @param out Writer to write output to.
     */
    public TextConsole(String useName, Reader in, Writer out)
    {
        super(useName);
        stdin_ = new BufferedReader(in);
        stdout_ = new BufferedWriter(out);
    }

    //--------------------------------------------------------------------------
    // AbstractConsole Impl
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.console.AbstractConsole#setCommandLine(java.lang.String)
     */
    public void setCommandLine(String cmd)
    {
        // Not supported
    }

    
    /**
     * @see toolbox.util.ui.console.AbstractConsole#getCursorDownName()
     */
    public String getCursorDownName()
    {
        return "";
    }

    
    /**
     * @see toolbox.util.ui.console.AbstractConsole#getCursorUpName()
     */
    public String getCursorUpName()
    {
        return "";
    }

    //--------------------------------------------------------------------------
    // Console Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.console.Console#setPrompt(java.lang.String)
     */
    public void setPrompt(String newPrompt)
    {
        prompt_ = newPrompt;
    }


    /**
     * @see toolbox.util.ui.console.Console#clear()
     */
    public synchronized void clear()
    {
        write("\u000c");
    }


 
    /**
     * @see toolbox.util.ui.console.Console#read()
     */
    public String read() throws InterruptedIOException
    {

        if (null != prompt_)
        {
            write(prompt_);
            prompt_ = "";
        }

        try
        {
            return stdin_.readLine();
        }
        catch (IOException ex)
        {
            logger_.warn("Could not read from stdin", ex);
            return null;
        }
    }


    /**
     * @see toolbox.util.ui.console.Console#write(java.lang.String)
     */
    public void write(String msg)
    {
        try
        {
            stdout_.write(msg);
            stdout_.flush();
        }
        catch (IOException ex)
        {
            logger_.warn("Failed writing to stdout", ex);
        }
    }
}