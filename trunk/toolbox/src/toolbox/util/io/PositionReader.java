package toolbox.util.io;

import java.io.IOException;
import java.io.Reader;

public class PositionReader extends Reader
{
    protected Reader in_;
    protected long offset_ = 0;
    protected long markOffset_ = 0;

    public PositionReader( Reader reader )
    {
        super( reader );
        this.in_ = reader;;
    }

    // READER METHODS
    
    /**
     * Read a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException 
    {
        int read = in_.read();
        if( read != -1 )
            offset_++;
            
        return read;
    }
    
    /**
      * Read characters into a portion of an array.
      * 
      * @exception  IOException  If an I/O error occurs
      */
    public int read(char[] array, int off, int len) throws IOException
    {
        int read = in_.read( array, off, len );

        // count characters read
        if( read > 0 )
        {
            offset_ += read;
        }

        return read;
    }

    /**
     * Skip characters.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public long skip( long n ) throws IOException 
    {
        long skipped = in_.skip(n);
        
        offset_ += skipped;
        
        return skipped;
    }

    /**
     * Tell whether this stream is ready to be read.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public boolean ready() throws IOException 
    {
        return in_.ready();
    }

    /**
     * Tell whether this stream supports the mark() operation.
     */
    public boolean markSupported() 
    {
        return in_.markSupported();
    }

    /**
     * Mark the present position in the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void mark( int readAheadLimit ) throws IOException 
    {
        in_.mark( readAheadLimit );
        markOffset_ = offset_;
    }

    /**
     * Reset the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void reset() throws IOException 
    {
        in_.reset();
        offset_ = markOffset_;
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException 
    {
        in_.close();
    }

    // API - METHODS

    /**
     * @return the characters from the current postion
     *         until the stopAt or EOF is found.
     */
    public String readUntil( char stopAt ) throws IOException
    {
        StringBuffer sb = new StringBuffer( 256 );
        
        synchronized( lock ) 
        {
            int c;
            
            while( ( c = read() ) != -1 )
            {
                sb.append( (char) c );
                
                if( c == stopAt )
                    break;
            }
            
        }
        
        return sb.toString();
    }


    public long getOffset() 
    { 
        return offset_; 
    }
}