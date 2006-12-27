package toolbox.util.ui.console;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.log4j.Logger;

/**
 * TextConsole is a character based implementation of a {@link Console}.
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

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new console using stdin and stdout.
     * 
     * @param name Console name.
     */
    public TextConsole(String name)
    {
        this(
            name, 
            new InputStreamReader(System.in), 
            new OutputStreamWriter(System.out));
    }

    
    /**
     * Creates a new console.
     * 
     * @param name Console name.
     * @param in Reader to read input from.
     * @param out Writer to write output to.
     */
    public TextConsole(String name, Reader in, Writer out)
    {
        super(name);
        
        // TODO: Figure out if already buffered so we're not buffering twice.
        stdin_ = new BufferedReader(in);
        stdout_ = new BufferedWriter(out);
    }

    //--------------------------------------------------------------------------
    // AbstractConsole Impl
    //--------------------------------------------------------------------------
    
    /**
     * Not supported since we don't have the ability to position the cursor.
     * 
     * @see toolbox.util.ui.console.AbstractConsole#setCommandLine(
     *      java.lang.String)
     */
    public void setCommandLine(String cmd)
    {
        // NO-OP
    }

    
    /**
     * @see toolbox.util.ui.console.AbstractConsole#getCursorDownName()
     */
    public String getCursorDownName()
    {
        return "Fix me";
    }

    
    /**
     * @see toolbox.util.ui.console.AbstractConsole#getCursorUpName()
     */
    public String getCursorUpName()
    {
        return "Fix me";
    }

    //--------------------------------------------------------------------------
    // Console Interface
    //--------------------------------------------------------------------------
    
    /**
     * Writes the prompt to the console.
     * 
     * @see toolbox.util.ui.console.Console#renderPrompt()
     */
    public void renderPrompt()
    {
        write(getPrompt());
    }

    

    /**
     * Sends '\u000c' to clear the console.
     * 
     * @see toolbox.util.ui.console.Console#clear()
     */
    public synchronized void clear()
    {
        write("\u000c");
    }

 
    /**
     * Reads an entire line of text from the console.
     * 
     * @see toolbox.util.ui.console.Console#read()
     */
    public String read() throws InterruptedIOException
    {
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
     * Writes a message to the console and flushes the stream.
     * 
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