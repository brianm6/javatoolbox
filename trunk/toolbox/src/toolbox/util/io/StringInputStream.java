package toolbox.util.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import toolbox.util.ClassUtil;
import toolbox.util.StringUtil;
import toolbox.util.Stringz;

/**
 * StringInputStream is an input stream sourced from a String.
 * <p>
 * The following behavior is not supported:
 * <ul>
 *   <li>mark()
 *   <li>reset()
 *   <li>skip()
 * </ul>
 */
public class StringInputStream extends InputStream implements Stringz  
{
    private static final Logger logger_ = 
        Logger.getLogger(StringInputStream.class);
    
    /** 
     * Current position in the stream. 
     */
    private int index_;

    /** 
     * Stream buffer. 
     */
    private StringBuffer buffer_;

    /** 
     * Flag to ignore the EOF. 
     */
    private boolean ignoreEOF_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an empty StringInputStream.
     */
    public StringInputStream()
    {
        this("", false);
    }

    
    /**
     * Creates a StringInputStream with the passed string.
     *
     * @param s String to initialize stream with
     */
    public StringInputStream(String s)
    {
        this(s, false);
    }

    
    /**
     * Creates a StringInputStream.
     * 
     * @param ignoreEOF Ignores EOF (read blocks indefinitely if the end of 
     *        the stream has been reached)
     */
    public StringInputStream(boolean ignoreEOF)
    {
        this("", ignoreEOF);
    }
    
    
    /**
     * Creates a StringInputStream.
     * 
     * @param s String to initialize stream with
     * @param ignoreEOF Ignores EOF (read blocks indefinitely if the end of 
     *        the stream has been reached)
     */
    public StringInputStream(String s, boolean ignoreEOF)
    {
        index_     = 0;
        buffer_    = new StringBuffer(s);
        ignoreEOF_ = ignoreEOF;
    }

    //--------------------------------------------------------------------------
    // Overrides java.io.InputStream
    //--------------------------------------------------------------------------
    
    /**
     * Reads a byte from the stream.
     *
     * @return The current character or -1 if stream is empty
     * @throws IOException if I/O error occurs
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
                catch (InterruptedException ie) 
                {
                    ;   // Ignore
                }
            }
        }
        
        return c;
    }
    
    
    /**
     * Returns number of bytes available to read from the stream without 
     * blocking.
     * 
     * @return Number of bytes available
     * @throws IOException on I/O error
     */
    public int available() throws IOException
    {
        return buffer_.length() - index_;
    }
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Appends a string to the end of the input stream.
     * 
     * @param s String to append
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
     * Sets the flag to ignore EOF.
     * 
     * @param ignoreEOF True to ignore EOF, false otherwise
     */   
    public void setIgnoreEOF(boolean ignoreEOF)
    {
        ignoreEOF_ = ignoreEOF;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        String unread = buffer_.substring(index_);
        int unreadLen = unread.length();
        
        String buffer = buffer_.toString();
        int bufferLen = buffer_.length();
        
        StringBuffer sb = new StringBuffer();
        sb.append(NL + BRNL);
        sb.append(ClassUtil.stripPackage(getClass().getName()));
        sb.append(" (" + super.toString() + ")" + NL);
        sb.append(StringUtil.repeat("-", 80) + NL);        
        sb.append("index     = " + index_ + NL);
        sb.append("ignoreEOF = " + ignoreEOF_ + NL);
        
        sb.append(
            "unread    = [" + unreadLen + "] " + 
            (unreadLen > 80 
                ? NL + StringUtil.wrap(unread, 70, "\t[", "]") 
                : unread) + NL);
             
        sb.append(
            "buffer    = [" + bufferLen + "] " +
            (bufferLen > 80 
                ? NL + StringUtil.wrap(buffer, 70, "\t[", "]")  
                : buffer) + NL); 
            
            
        sb.append(BRNL);
        return sb.toString();
    }
}