package toolbox.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;

import org.apache.log4j.Logger;

/**
 * Reads a file efficiently in reverse order.
 */
public class ReverseFileReader extends Reader
{
    private static final Logger logger_ =
        Logger.getLogger(ReverseFileReader.class);
        
    private RandomAccessFile file_;
    private long pointer_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ReverseFileReader for the given file
     * 
     * @param  f  File to read in reverse order from
     * @throws IOException on I/O error
     * @throws FileNotFoundException for non-existant file 
     */        
    public ReverseFileReader(File f) throws IOException, FileNotFoundException
    {
        file_ = new RandomAccessFile(f, "r");
        pointer_ = file_.length() - 1;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.io.Reader
    //--------------------------------------------------------------------------
    
    /**
     * @see java.io.Reader#read()
     */
    public int read() throws IOException
    {
        int ch = -1;
        
        if (pointer_ != -1)
        {
            file_.seek(pointer_);
            ch = file_.read();
            --pointer_;
        }
        
        return ch;
    }
    
    /**
     * @see java.io.Reader#read(char[], int, int)
     */
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        for (int i=0; i<len; i++)
        {
            int ch = read();
            
            if (ch == -1)
                return i;
            else
                cbuf[off+i] = (char) ch;
        }
        
        return len;
    }
    
    /**
     * @see java.io.Reader#close()
     */
    public void close() throws IOException
    {
        file_.close();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
        
    /**
     * Reads the next previous line starting at the end of the file
     * 
     * @return Line as string or null if the beginning of file has been reached.
     * @throws IOException on I/O error
     */
    public String readLine() throws IOException
    {
        String line = null;
        StringBuffer sb = new StringBuffer();
        
        //logger_.debug("Pointer before: " + pointer_);
        
        while (pointer_ >= 0 )
        {
            file_.seek(pointer_);
            
            int i = file_.read();
            char c = (char) i; 
            
            //logger_.debug("Read char: " + c);
            
            if (c == '\n')
            {
                // Hit a new line. Bundle up the current buffer and return  
                line = sb.reverse().toString();
                --pointer_;
                break;
            }
            else if (pointer_ == 0)
            {
                // Hit the beginning of the file
                sb.append(c);
                line = sb.reverse().toString();
                
                // Cause subsequent calls to return null
                pointer_ = -1; 
                
                break;
            }
            else
            {
                // Business as usual...collect in buffer
                sb.append(c);
                --pointer_;
            }
            
            //logger_.debug(
            //  "Pointer after: " + pointer_ + "'" + sb.toString() + "'");
        }
        
        return line;
    }
}