package toolbox.util.io;

import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * An OutputStream that dumps into a JTextArea
 */
public class JTextAreaOutputStream extends OutputStream
{
    /**
     * Textarea to dump to 
     */
    private JTextArea textArea_;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    public JTextAreaOutputStream(JTextArea textArea)
    { 
        textArea_ = textArea; 
    }
 
    //--------------------------------------------------------------------------
    // Overridden from java.io.OutputStream 
    //--------------------------------------------------------------------------
    
    /**
     * Does nothing 
     */
    public void close()
    {
        // Do nothing
    }    
 
    /**
     * Writes array of bytes to textarea
     * 
     * @param  b  Array of bytes
     */
    public void write(byte[] b)
    {     
        write(b, 0, b.length); 
    }

    /**
     * Writes bytes to text area
     * 
     * @param  b    Array of bytes
     * @param  off  Offset in array 
     * @param  len  Number of bytes to write
     */
    public void write(byte[] b, int off, int len)
    {
        byte[] bArray = new byte[len];
        System.arraycopy(b, off, bArray, 0, len);
 
        textArea_.append(new String(bArray));
    }

    /**
     * Writes a single byte to the textarea
     * 
     * @param  b  Byte to write
     */
    public void write(int b)
    { 
       textArea_.append(new String(new byte[] { (byte)b })); 
    }
}