package toolbox.dbconsole.util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Specialization of CouningOutputStream that prints number of bytes
 * written every x number of bytes to the logger.
 */
public class CountingOutputStream
    extends org.apache.commons.io.output.CountingOutputStream {

    private static final Log logger =
        LogFactory.getLog(CountingOutputStream.class);

    //----------------------------------------------------------------------
    // Fields
    //----------------------------------------------------------------------

    /**
     * Notification takes places every x number of bytes.
     */
    private int notifyEvery;

    //----------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------

    /**
     * Creates a CountingOutputStream.
     *
     * @param out Output stream to decorate.
     * @param notifyEvery Notify every x number of bytes.
     */
    public CountingOutputStream(OutputStream out, int notifyEvery) {
        super(out);
        this.notifyEvery = notifyEvery;
    }

    //----------------------------------------------------------------------
    // Overrides CountingOutputStream
    //----------------------------------------------------------------------

    /*
     * @see org.apache.commons.io.output.CountingOutputStream#write(int)
     */
    public void write(int b) throws IOException {

        if ( (getCount() % notifyEvery) == 0)
            printCount();

        super.write(b);
    }


    /*
     * @see org.apache.commons.io.output.CountingOutputStream#write(byte[])
     */
    public void write(byte[] b) throws IOException {

        for (int i = 0; i < b.length; i++)
            if (((getCount() + i) % notifyEvery) == 0)
                printCount();

        super.write(b);
    }


    /*
     * @see org.apache.commons.io.output.CountingOutputStream#write(byte[],
     *      int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {

        for (int i = 0; i < len; i++)
            if (((getCount() + i) % notifyEvery) == 0)
                printCount();

        super.write(b, off, len);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Prints the number of bytes output to the logger.
     */
    protected void printCount() {
        logger.debug(
            "Wrote "
            + DecimalFormat.getNumberInstance().format(getCount())
            + " bytes...");
    }
}