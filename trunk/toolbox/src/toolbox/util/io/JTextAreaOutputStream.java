package toolbox.util.io;

import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * An outputStream that dumps into a {@link javax.swing.JTextArea}.
 */
public class JTextAreaOutputStream extends OutputStream
{
    /** 
     * Textarea to dump to. 
     */
    private JTextArea textArea_;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JTextAreaOutputStream.
     * 
     * @param textArea JTextArea sink for the output stream.
     */
    public JTextAreaOutputStream(JTextArea textArea)
    { 
        textArea_ = textArea; 
    }
 
    //--------------------------------------------------------------------------
    // Implements Abstract from java.io.OutputStream 
    //--------------------------------------------------------------------------

    /**
     * Writes a byte to the textarea.
     * 
     * @param b Byte
     */
    public void write(int b)
    {
        textArea_.append(new String(new byte[] {(byte) b })); 
    }
}