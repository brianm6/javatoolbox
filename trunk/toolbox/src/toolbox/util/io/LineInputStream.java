package toolbox.util.io;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FilterInputStream that buffers input until newline occurs. Then
 * everything is passed along. If backspace (ch = 8) is received then the
 * last character in the buffer is removed.
 */
public class LineInputStream extends FilterInputStream
{
    byte byteArray_[];
    int arrayOffset_;
    int arrayLength_;

    //----------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------
    
    /**
     * Creates a LineInputStream.
     * 
     * @param in Stream to chain.
     */
    public LineInputStream(InputStream in)
    {
        super(in);
    }

    //----------------------------------------------------------------------
    // Overrides InputStream
    //----------------------------------------------------------------------
    
    /**
     * @see java.io.InputStream#read()
     */
    public synchronized int read() throws IOException
    {
        // If there are data in buffer the return the first character
        // in buffer.
        if (byteArray_ != null && arrayOffset_ < arrayLength_)
            return byteArray_[arrayOffset_++];

        // if buffer is empty, fill buffer...
        byteArray_ = readLine();
        arrayOffset_ = 0;
        arrayLength_ = byteArray_.length;

        // If there are data in buffer the return the first character
        // in buffer.
        if (byteArray_ != null && arrayOffset_ < arrayLength_)
            return byteArray_[arrayOffset_++];
        else
            return -1;
    }


    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public synchronized int read(byte bytes[], int offset, int length)
        throws IOException
    {

        if (byteArray_ != null && arrayOffset_ < arrayLength_)
        {
            int available = available();

            if (length > available)
                length = available;

            System.arraycopy(
                byteArray_, 
                arrayOffset_, 
                bytes, 
                offset, 
                length);
            
            arrayOffset_ += length;
            return length;
        }

        byteArray_ = readLine();
        arrayOffset_ = 0;
        arrayLength_ = byteArray_.length;

        if (byteArray_ == null || arrayOffset_ >= arrayLength_)
            return -1;

        int available = available();

        if (length > available)
            length = available;

        System.arraycopy(byteArray_, arrayOffset_, bytes, offset, length);
        arrayOffset_ += length;
        return length;
    }


    /**
     * @see java.io.InputStream#available()
     */
    public synchronized int available() throws IOException
    {
        return arrayLength_ - arrayOffset_ + super.available();
    }

    //----------------------------------------------------------------------
    // Public 
    //----------------------------------------------------------------------
    
    /**
     * Reads a line from this input stream.
     * 
     * @return Array of characters read.
     * @throws IOException on I/O exception.
     */
    public synchronized byte[] readLine() throws IOException
    {
        byte bytes[];
        int ch;
        boolean ready = false;
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

        while (!ready)
        {
            ch = in.read();

            if (ch == -1)
            {
                // EOF
                ready = true;
            }
            else if (ch == 8)
            {
                // Backspace: Remove last character in buffer.
                bytes = bytesOut.toByteArray();
                bytesOut.reset();
                int length = bytes.length - 1;

                if (length > 0)
                    bytesOut.write(bytes, 0, length);
            }
            else if (ch == 21)
            {
                // ^U: Remove all character in buffer.
                bytesOut.reset();
                int length = 0;
            }
            else if (ch == 10)
            {
                bytesOut.write(ch);
                // NewLine: Return current buffer.
                ready = true;
            }
            else
            {
                // Other: Add to buffer.
                bytesOut.write(ch);

                //out.write(ch);
                //out.flush();
            }
        }

        return bytesOut.toByteArray();
    }
}