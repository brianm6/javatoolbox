package toolbox.util.io;

import java.io.IOException;
import java.io.Writer;

import toolbox.util.RollingCounter;

/**
 * WrappingWriter is a Writer that automatically line wraps. Options include
 * a configurable line width and line prefix and suffix decorators.
 * 
 * <pre>
 * Simple line wrap with a width of 3: 
 * 
 * wrap("123456789", 3) =
 * 
 * 123
 * 456
 * 789
 * 
 * wrap("123456789", 5, "[", "]") =   *note: width is 5..must take into acct 
 *                                           prefix and suffix which are 
 *                                           included in the width
 * [123]
 * [456]
 * [789] 
 * 
 * </pre>
 */
public class WrappingWriter extends Writer 
    implements RollingCounter.IRollingCounterListener
{
    /** 
     * Default width to wrap at 
     */
    public static final int DEFAULT_WIDTH   = 80;
    
    /** 
     * Default prefix for each line 
     */
    public static final String DEFAULT_PREFIX  = "";
    
    /** 
     * Default suffix for each line 
     */
    public static final String DEFAULT_SUFFIX  = "";
    
    /** 
     * Default newline for each line 
     */
    public static final String DEFAULT_NEWLINE = "\n";

    /** 
     * Delegate writer 
     */
    private Writer writer_;
    
    /** 
     * Rolling counter to keep track of line position 
     */
    private RollingCounter counter_;
    
    /** 
     * Width of writer 
     */
    private int width_;
    
    /** 
     * Prefix decorator prepended to each line 
     */
    private String prefix_;     
    
    /** 
     * Suffix decorator appended to each line 
     */
    private String suffix_;     
    
    /**
     *  Flag to mark the consumption of the first character 
     */
    private boolean first_;
    
    /** 
     * Flag to stagger writing on a new line until the next char is written 
     */
    private boolean stagger_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a wrapping writer with default with of <code>DEFAULT_WIDTH</code>
     * 
     * @param writer Writer to decorate
     */ 
    public WrappingWriter(Writer writer)
    {
        this(writer, DEFAULT_WIDTH);
    }


    /**
     * Creates a wrapping writer with the given width and no prefix/suffix
     * decorators
     * 
     * @param writer Writer to decorate
     * @param width Number of characters after which a line will be wrapped
     */ 
    public WrappingWriter(Writer writer, int width)
    {
        this(writer, width, DEFAULT_PREFIX, DEFAULT_SUFFIX);
    }

        
    /**
     * Creates a wrapping writer
     * 
     * @param writer Writer to decorate
     * @param width Number of characters after which a line will be wrapped
     * @param prefix Decorator prepended to beginning of each line
     * @param suffix Decorator appended to the end of each line
     */ 
    public WrappingWriter(Writer writer, int width, String prefix,String suffix)
    {
        writer_    = writer;
        prefix_    = prefix;
        suffix_    = suffix;
        
        // Subtract the space that the pre/suffix takes up from the width
        width_     = width - prefix_.length() - suffix_.length();
        
        first_     = true;
        stagger_   = false;
        
        // Create a counter with range 1..width 
        counter_     = new RollingCounter(1, width_, 1); 
        counter_.addRollingCounterListener(this);
    }

    //--------------------------------------------------------------------------
    // Overrides java.io.OutputStream
    //--------------------------------------------------------------------------

    /**
     * Write a portion of an array of characters.
     *
     * @param cbuf Array of characters
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     * @throws IOException if an I/O error occurs
     */
    public void write(char cbuf[], int off, int len) throws IOException
    {
        for (int i=off; i<off+len; i++)
        {
            // Add prefix before writing very first char to stream
            if (first_)
            {
                writer_.write(prefix_);
                first_ = false;    
            }

            // If were're on the verge of a new line, wrap
            if(stagger_)
            {
                writer_.write(DEFAULT_NEWLINE);
                writer_.write(prefix_);
                stagger_ = false;
            }
           
            writer_.write(cbuf[i]);
            counter_.increment();            
        }
    }

    
    /**
     * Flush the stream.  If the stream has saved any characters from the
     * various write() methods in a buffer, write them immediately to their
     * intended destination.  Then, if that destination is another character or
     * byte stream, flush it.  Thus one flush() invocation will flush all the
     * buffers in a chain of Writers and OutputStreams.
     *
     * @throws IOException if an I/O error occurs
     */
    public void flush() throws IOException
    {   
        writer_.flush();
    }


    /**
     * Close the stream, flushing it first.  Once a stream has been closed,
     * further write() or flush() invocations will cause an IOException to be
     * thrown.  Closing a previously-closed stream, however, has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException
    {
        if (!counter_.isAtEnd() && !first_ && !stagger_)
            writer_.write(suffix_);
        
        writer_.flush();
        writer_.close();
    }
    
    //--------------------------------------------------------------------------
    // RollingCounter.IRollingCounterListener Interface
    //--------------------------------------------------------------------------

    /**
     * Counter has rolled back to the beginning of the range
     * 
     * @param rc Counter that was rolled
     */
    public void afterRoll(RollingCounter rc)
    {
        stagger_ = true;
    }
    
    
    /**
     * Counter is about to roll to the beginning of the range
     * 
     * @param rc Counter being rolled
     */
    public void beforeRoll(RollingCounter rc)
    {
        try 
        {
            writer_.write(suffix_);
        } 
        catch(IOException e) 
        {
            ;   // Ignore
        }
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * Returns a dump of the writer attributes and contents.
     * 
     * @return String
     */    
    public String toString()
    {
        return "\nWrappingWriter\n" +
               "{\n" +
               "    counter_ = " + counter_ + "\n" +
               "    width_   = " + width_   + "\n" +
               "    prefix_  = " + prefix_  + "\n" +
               "    suffix_  = " + suffix_  + "\n" +
               "    stagger_ = " + stagger_ + "\n" +
               "    first_   = " + first_   + "\n" +
               "}";
    }
}
