package toolbox.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

/**
 * Reads a file, efficiently, a line at a time in reverse order. Uses 
 * RandomAccessFile internally to seek in reverse direction. 
 * <p>
 * Note: This class is not a java.io.Reader in the traditional sense. 
 */
public class ReverseFileReader
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
        
        long length = file_.length();
        
        if (length == 0)
            pointer_ = -1;
        else
            pointer_ = length - 1;
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
    public String readPreviousLine() throws IOException
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