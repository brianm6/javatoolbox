package toolbox.util.io;

import java.io.*;

public class PositionInputStream extends InputStream
{
    protected InputStream in_;
    protected long offset_ = 0;
    protected long markOffset_ = 0;
    protected int lastByteRead_ = -1;

    /**
     * Constructor for PositionInputStream
     */
    public PositionInputStream( InputStream iStream )
    {
        super();
        
        if( iStream == null )
            throw new NullPointerException();
            
        this.in_ = iStream;
    }

    // INPUTSTREAM METHODS
    
    /**
     * Read a single byte.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException 
    {
        int read = in_.read();
        if( read != -1 )
            offset_++;
            
        lastByteRead_ = read;
        return read;
    }
    
    /**
      * Read a byte into a portion of an array.
      * 
      * @exception  IOException  If an I/O error occurs
      */
    public int read(byte b[], int off, int len) throws IOException 
    {
        int read = in_.read( b, off, len );

        // count characters read
        if( read > 0 )
        {
            offset_ += read;
            lastByteRead_ = b[ off + read - 1 ];
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


    public int available() throws IOException 
    {
        return in_.available();
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
     */
    public void mark( int readAheadLimit )
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

    // API - METHODS

    /**
     * @param stopAt the byte to stop at
     * @return the bytes from the current postion
     *         until the stopAt or EOF is found.
     */
    public byte[] readUntil( byte stopAt ) throws IOException
    {
        return readUntil( new byte[] { stopAt } );
    }

    /**
     * @param stopAt any byte to stop at
     * @return the bytes from the current postion
     *         until the stopAt or EOF is found.
     */
    public byte[] readUntil( byte[] stopAt ) throws IOException
    {
        ByteArrayOutputStream oStream = new ByteArrayOutputStream( 256 );
        
        synchronized( this )
        {
            int b;
            
            outer:
            while( ( b = read() ) != -1 )
            {
                oStream.write( b );
                for( int i = 0; i < stopAt.length; i++ )
                {
                    if( b == stopAt[i] )
                        break outer;
                }
            }
            
        }
        
        return oStream.toByteArray();
    }


    /**
     * @returns the current offset read using the InputStream
     */
    public long getOffset() 
    { 
        return offset_; 
    }
    
    /**
     * Returns the last byte read by this InputStream.
     */
    public int getLastByteRead()
    {
        return lastByteRead_;
    }
}

