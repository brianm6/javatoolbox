package toolbox.util.io;

import java.io.Writer;

/**
 * NullWriter sends all characters written to it to /dev/null.
 */
public class NullWriter extends Writer {

    // -------------------------------------------------------------------------
    // Overrides java.io.Writer
    // -------------------------------------------------------------------------

    /*
     * @see java.io.Writer#close()
     */
    public void close() {
        // Nothing to do
    }


    /*
     * @see java.io.Writer#flush()
     */
    public void flush() {
        // Nothing to do
    }


    /**
     * Eat written characters.
     * 
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] cbuf, int off, int len) {
        // Do nothing
    }
}