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
     * @see OutputStream#write(int)
     */
    public void write(int b) throws IOException
    {
    }
    
    /**
     * @see OutputStream#close()
     */
    public void close() throws IOException
    {
    }

    /**
     * @see OutputStream#flush()
     */
    public void flush() throws IOException
    {
    }
}