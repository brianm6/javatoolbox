package toolbox.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;

/**
 * Reads a file efficiently in reverse order.
 */
public class ReverseFileReader extends Reader
{
    private static final Logger logger_ =
        Logger.getLogger(ReverseFileReader.class);

    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /** 
     * File to read in reverse order. 
     */        
    private RandomAccessFile file_;
    
    /** 
     * Current position in the file. 
     */
    private long pointer_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ReverseFileReader.
     * 
     * @param file File to read in reverse order.
     * @throws IOException on I/O error.
     * @throws FileNotFoundException for non-existant file. 
     */        
    public ReverseFileReader(File file) throws IOException, 
        FileNotFoundException
    {
        file_ = new RandomAccessFile(file, "r");
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
        
        if (pointer_ >= 0)
        {
            file_.seek(pointer_);
            ch = file_.read();
            --pointer_;
        }
        
        return ch;
    }

    
    /**
     * @see java.io.Reader#read(char[])
     */    
    public int read(char cbuf[]) throws IOException 
    {
        return read(cbuf, 0, cbuf.length);
    }
    
    
    /**
     * @see java.io.Reader#read(char[], int, int)
     */
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        for (int i = 0; i < len; i++)
        {
            int ch = read();
            
            if (ch == -1)
                return i;
            else
                cbuf[off + i] = (char) ch;
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
    
    
    /**
     * Skips in reverse direction.
     * 
     * @see java.io.Reader#skip(long)
     */
    public long skip(long n) throws IOException 
    {
        long skipped;
        
        if (n > pointer_)
        {
            // Attempting to go before beginning of file
            skipped = pointer_;
            pointer_ = 0;
        }
        else
        {
            skipped = n;
            pointer_ -= n;
        }
        
        return skipped;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
        
    /**
     * Reads the next line from the file in the reverse direction. 
     * <p>
     * Given the following file with the carat denoting the current file
     * pointer:
     * <pre>
     * First line
     * Second line
     * Last line
     *         ^
     * 
     * A call to readLine() will return the string "enil tsaL"
     * </pre>
     * 
     * @return Line as string or null if the beginning of file has been reached.
     * @throws IOException on I/O error.
     */
    public String readLine() throws IOException
    {
        String line = null;
        StringBuffer sb = new StringBuffer();
        
        while (pointer_ >= 0)
        {
            int i = read();
            char c = (char) i;
            
            if (c == '\n')
            {
                line = sb.toString();
                break;
            }
            else if (pointer_ == -1)
            {
                sb.append(c);
                line = sb.toString();
                break;
            }
            else
            {
                sb.append(c);
            }
        }
        
        return line;
    }
    
    
    /**
     * Reads the next line from the file in reverse direction, but with the
     * contents of the line in normal reading order.
     * 
     * <p>
     * Given the following file with the carat denoting the current file
     * pointer:
     * <pre>
     * First line
     * Second line
     * Last line
     *         ^
     * 
     * A call to readLineNormal() will return the string "Last line"
     * </pre>
     * 
     * @return Line as string or null if the beginning of file has been reached.
     * @throws IOException on I/O error.
     */
    public String readLineNormal() throws IOException
    {
        String line = readLine();
        return (line != null ? StringUtil.reverse(line) : line);
    }
}