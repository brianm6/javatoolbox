package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An OutputStream that keeps track of the number of bytes written to it 
 */
public class CountingOutputStream extends FilterOutputStream
{
    /** 
     * Total number of bytes written to the outputstream 
     */
    private int count_;
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * @param out
     */
    public CountingOutputStream(OutputStream out)
    {
        super(out);
        count_ = 0;
    }
    
    //--------------------------------------------------------------------------
    // Overridden from java.io.FilterOutputStream
    //--------------------------------------------------------------------------
    
    /**
     * Writes byte to stream keeping track of the count
     * 
     * @param  b  Byte to write 
     */
    public void write(int b) throws IOException
    {
        super.write(b);
        count_++;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @return  Number of bytes written to the stream
     */
    public int getCount()
    {
        return count_;
    }
    
    /**
     * Resets the count back to zero
     */
    public void reset()
    {
        count_ = 0;
    }
}