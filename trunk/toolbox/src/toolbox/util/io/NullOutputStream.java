package toolbox.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * OutputStream that sends all data  to /dev/null 
 */
public class NullOutputStream extends OutputStream
{
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor 
     */
    public NullOutputStream()
    {
    }

    //--------------------------------------------------------------------------
    // Overridden from java.io.OutputStream 
    //--------------------------------------------------------------------------
    
    /**
     * Noop
     */
    public void write(int b) throws IOException
    {
    }
    
    /**
     * Noop
     */
    public void close() throws IOException
    {
    }

    /**
     * Noop
     */
    public void flush() throws IOException
    {
    }
}