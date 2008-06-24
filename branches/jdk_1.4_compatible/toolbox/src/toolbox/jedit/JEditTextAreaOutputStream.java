package toolbox.jedit;

import java.io.OutputStream;

/**
 * An outputStream that dumps into a {@link JEditTextArea}.
 */
public class JEditTextAreaOutputStream extends OutputStream
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Textarea to dump the outputstream contents to. 
     */
    private JEditTextArea textArea_;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JEditTextAreaOutputStream.
     * 
     * @param textArea JEditTextArea sink for the output stream.
     */
    public JEditTextAreaOutputStream(JEditTextArea textArea)
    { 
        textArea_ = textArea; 
    }
 
    //--------------------------------------------------------------------------
    // Implements Abstract from java.io.OutputStream 
    //--------------------------------------------------------------------------

    /**
     * Writes a byte to the textarea.
     * 
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b)
    {
        int i = textArea_.getCaretPosition();
        textArea_.setSelectionStart(i);
        textArea_.setSelectionEnd(i);
        textArea_.setSelectedText(new String(new byte[] {(byte) b })); 
    }
}