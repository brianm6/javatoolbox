package toolbox.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * I/O Stream Utility Class
 */
public class StreamUtil
{
    private static final Logger logger_ = 
        Logger.getLogger(StreamUtil.class);

    // Clover private constructor workaround
    static { new StreamUtil(); }
 
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
           
    /**
     * Prevent construction
     */
    private StreamUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Converts the contents of an character input stream to a string.
     *
     * @param   inputStream     Input stream to read from
     * @return  String representation of the input stream contents.
     * @throws  IOException on IO error
     */
    public static String asString(InputStream inputStream) throws IOException
    {
        return new String(toBytes(inputStream));
    }

    /**
     * Converts the remaining contents of an InputStream to a byte array
     * 
     * @param   is   InputStream to convert
     * @return  Contents of the stream as a byte array
     * @throws  IOException on IO error
     */    
    public static byte[] toBytes(InputStream is) throws IOException
    {
        List buffer = new ArrayList();
        int c;
        
        while ( (c = is.read()) != -1)
        {
            Byte b = new Byte((byte)c);
            buffer.add(b);
        }
        
        byte[] byteArray = new byte[buffer.size()];
        
        for(int i=0; i<buffer.size(); i++)
        {
            Byte b = (Byte)buffer.get(i);
            byteArray[i] = b.byteValue();
        }
        
        return byteArray;
    }
    
    /**
     * Reads an exact number of characters from a stream and returns them as a
     * String. If the given number of characters is not availble, the method
     * blocks until the entire length is satisfied.
     * 
     * @param   is     InputStream to read characters from
     * @param   len    Number of characters to read
     * @return  String containing len characters read from input stream
     * @throws  IOException if underlying stream times out or IO error occurs
     */
    public static byte[] readExactly(InputStream is, int len) 
        throws IOException
    {
        byte[] buffer = new byte[len];

        for (int i = 0; i < len; i++)
        {
            int c = -1;

            try
            {
                c = is.read();
            }
            catch (InterruptedIOException iioe)
            {
                logger_.debug("Read probably timed out\n len=" + len + 
                    " index=" + i + " buffer=" + buffer, iioe);
            }

            if (c != -1)
            {
                buffer[i] = (byte)c;
            }
            else
            {
                // Throw all bytes read into a string
                StringBuffer bytesRead = new StringBuffer();

                for (int j = 0; j < i; j++)
                    bytesRead.append(buffer[j]);

                throw new EOFException(
                    "End of inputstream reached. Bytes read = " + bytesRead);
            }
        }
        
        return buffer;
    }

    /**
     * Reads an exact number of characters from a reader and returns them as a
     * String. If the given number of characters is not availble, the method
     * blocks until the entire length is satisfied.
     * 
     * @param   reader  Reader to read characters from
     * @param   len     Number of characters to read
     * @return  String of characters read
     * @throws  IOException on IO error
     */
    public static String readExactly(Reader reader, int len) 
        throws IOException
    {
        /* 
         * Could have just wrapped reader in a stream and called input streadm
         * version but didn't want to risk all the charset/unicode/char/i18n
         * stuff
         */
        StringBuffer sb = new StringBuffer(len);

        for (int i = 0; i < len; i++)
        {
            int c = -1;

            try
            {
                c = reader.read();
            }
            catch (InterruptedIOException iioe)
            {
                logger_.debug("Read probably timed out\n len=" + len + 
                    " index=" + i + " buffer=" + sb, iioe);
            }

            if (c != -1)
            {
                sb.append((char)c);
            }
            else
            {
                throw new EOFException(
                    "End of inputstream reached. Chars read = " + sb);
            }
        }
        
        return sb.toString();
    }

    /**
     * Empties the contents of an inputstream based on the currently available
     * contents.
     * 
     * @param  is   InputStream to empty
     * @return Number of bytes discarded
     * @throws IOException on IO error
     */
    public static int empty(InputStream is) throws IOException
    {
        int avl = is.available();
        is.skip(avl);

        return avl;
    }

    /**
     * Closes an inputstream quitely
     * 
     * @param  inputStream  Inputstraem to close
     */    
    public static void close(InputStream inputStream)
    {
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                logger_.warn(
                    "An error occurred while closing an InputStream.", e);
            }
        }
    }

    /**
     * Closes an outputstream quitely
     * 
     * @param  outputStream  Output stream to close
     */    
    public static void close(OutputStream outputStream)
    {
        if (outputStream != null)
        {
            try
            {
                outputStream.close();
            }
            catch (IOException e)
            {
                logger_.warn(
                    "An error occurred while closing an OutputStream.", e);
            }
        }
    }
}