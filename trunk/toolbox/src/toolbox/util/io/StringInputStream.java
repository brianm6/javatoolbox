package toolbox.util.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;

/**
 * StringInputStream is an input stream that is backed by a string
 */
public class StringInputStream extends InputStream 
{
    private static final Logger logger_ = 
        Logger.getLogger(StringInputStream.class);
    
    /** 
     * Current position in stream 
     */
    private int index_;

    /** 
     * Stream buffer 
     */
    private StringBuffer buffer_;

    /**
     * Flag to ignore the eof of stream
     */
    private boolean ignoreEOF_;
    

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an empty StringInputStream
     */
    public StringInputStream()
    {
        this("", false);
    }

    /**
     * Creates a StringInputStream with the passed string
     *
     * @param  s  String to initialize stream with
     */
    public StringInputStream(String s)
    {
        this(s, false);
    }

    /**
     * Creates a StringInputStream
     * 
     * @param  ignoreEOF    Ignores EOF (read blocks indefinitely if the end
     *                      of the stream has been reached)
     */
    public StringInputStream(boolean ignoreEOF)
    {
        this("", ignoreEOF);
    }
    
    /**
     * Creates a StringInputStream
     * 
     * @param  s            String to initialize stream with
     * @param  ignoreEOF    Ignores EOF (read blocks indefinitely if the end
     *                      of the stream has been reached)
     */
    public StringInputStream(String s, boolean ignoreEOF)
    {
        index_     = 0;
        buffer_    = new StringBuffer(s);
        ignoreEOF_ = ignoreEOF;
    }

    //--------------------------------------------------------------------------
    //  Overridden Methods from InputStream
    //--------------------------------------------------------------------------
    
    /**
     * Reads a byte from the stream
     *
     * @return The current character or -1 if stream is empty
     * @throws IOException if IO error occurs
     */
    public int read() throws IOException 
    {
        int c = -1;
        
        if (index_ < buffer_.length())
        {
            c = buffer_.charAt(index_++);
        }
        else if (ignoreEOF_)
        {
            synchronized(this)
            {
                try
                {
                    wait();    
                    c = buffer_.charAt(index_++);
                }
                catch (InterruptedException ie) {};
            }
        }
        
        return c;
    }
    
    /**
     * Returns number of bytes available to read from the stream
     * 
     * @return  Number of bytes available
     * @throws  IOException
     */
    public int available() throws IOException
    {
        return buffer_.length() - index_;
    }
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Appends a string to the end of the input stream
     * 
     * @param  s  String to append
     */
    public synchronized void append(String s)
    {
        if (!StringUtil.isNullOrEmpty(s))
        {
            buffer_.append(s);
            
            if (ignoreEOF_)
                notify();
        }
    }   
 
    /**
     * Sets the flag to ignore EOF
     * 
     * @param  ignoreEOF   True to ignore EOF, false otherwise
     */   
    public void setIgnoreEOF(boolean ignoreEOF)
    {
        ignoreEOF_ = ignoreEOF;
    }
}