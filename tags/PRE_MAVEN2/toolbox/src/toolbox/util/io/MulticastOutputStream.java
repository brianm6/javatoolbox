package toolbox.util.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;

/**
 * An OutputStream that multicasts stream operations to one or more registered
 * OutputStreams.
 * <p>
 * <b>Example:</b>
 * 
 * <pre class="snippet">
 * MulticastOutputStream mos = new MulticastOutputStream();
 * 
 * try {
 *     mos.addStream(new FileOutputStream(&quot;file.txt&quot;));
 *     mos.addStream(new PipedOutputStream());
 *     mos.addStream(new DataOutputStream(new StringOutputStream()));
 *     mos.write(&quot;hey ya!&quot;.getBytes());
 * }
 * catch (Exception e) {
 *     System.err.println(e);
 * }
 * finally {
 *     IOUtils.closeQuietly(mos);
 * }
 * </pre>
 */
public class MulticastOutputStream extends OutputStream {

    // TODO: Introduce notion of a "master" stream which is the first one 
    //       written to in the group. If an exception is thrown then rethrow
    //       it instead 
    //
    // TODO: Or just add a flag whether or not to let the failure of one stream
    //       affect the others.
    
    private static final Logger logger_ = 
        Logger.getLogger(MulticastOutputStream.class);

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Members of the multicast group of streams.
     */
    private OutputStream[] streams_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a MulticastOutputStream.
     * 
     * @see #addStream(OutputStream)
     */
    public MulticastOutputStream() {
        streams_ = new OutputStream[0];
    }


    /**
     * Creates a MulticastOutputStream.
     * 
     * @param out Stream to add to the multicast group.
     */
    public MulticastOutputStream(OutputStream out) {
        streams_ = new OutputStream[0];
        addStream(out);
    }

    // --------------------------------------------------------------------------
    // Public
    // --------------------------------------------------------------------------

    /**
     * Adds a stream to this multicast group.
     * 
     * @param out Stream that will receive data sent this multicast group.
     */
    public synchronized void addStream(OutputStream out) {
        // TODO: Questionable whether this should be buffered.
        streams_ = (OutputStream[]) 
            ArrayUtil.add(streams_, new BufferedOutputStream(out));
    }


    /**
     * Removes a stream from this multicast group.
     * 
     * @param out Stream that will no longer receive data sent to this multicast
     *        group.
     */
    public synchronized void removeStream(OutputStream out) {
        streams_ = (OutputStream[]) ArrayUtil.remove(streams_, out);
    }

    // --------------------------------------------------------------------------
    // Overrides java.io.OutputStream
    // --------------------------------------------------------------------------

    /*
     * Writes an integer to each stream in the multicast group. If one of the
     * writes should fail, the failure is logged and the operation continues.
     * 
     * @see java.io.OutputStream#write(int)
     */
    public synchronized void write(int b) throws IOException {
        for (int i = 0; i < streams_.length; i++) {
            try {
                streams_[i].write(b);
            }
            catch (IOException ioe) {
                logger_.error(ioe);
            }
        }
    }


    /*
     * Flushes all streams in the multicast group. If one of the streams should
     * fail, the failure is logged and the operation continues.
     * 
     * @see java.io.OutputStream#flush()
     */
    public synchronized void flush() throws IOException {
        for (int i = 0; i < streams_.length; i++) {
            try {
                streams_[i].flush();
            }
            catch (IOException ioe) {
                logger_.error(ioe);
            }
        }
    }


    /*
     * Closes all streams in the multicast group. If one of the streams should
     * fail, the failure is logged and the operation continues.
     * 
     * @see java.io.OutputStream#close()
     */
    public synchronized void close() throws IOException {
        for (int i = 0; i < streams_.length; i++) {
            try {
                streams_[i].close();
            }
            catch (IOException ioe) {
                logger_.error(ioe);
            }
        }
    }
}