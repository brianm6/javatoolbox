package toolbox.tunnel;

import java.awt.TextArea;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * A Relay object is used by TcpTunnel and JTcpTunnel to relay bytes from an
 * InputStream to a OutputStream.
 */
public class Relay extends Thread
{
    private static final int BUFSIZ = 1000;
    
    private InputStream   in_;
    private OutputStream  out_;
    private byte[]        buf_ = new byte[BUFSIZ];
    private JTextArea     textArea_;
    private boolean       follow_;

    /**
     * Creates a new relay
     * 
     * @param  in  Input stream
     * @param  out Output stream
     */
    public Relay(InputStream in, OutputStream out)
    {
        this(in, out, null, false);
    }


    /**
     * Creates a new relay
     * 
     * @param  in       Input stream
     * @param  out      Output stream
     * @param  ta       Textarea
     * @param  follow   Should text area autoscroll w/ output?
     */
    public Relay(InputStream in, OutputStream out, JTextArea ta, boolean follow)
    {
        in_ = in;
        out_ = out;
        textArea_ = ta;
        follow_ = follow;
    }

    /**
     * Starts the relay
     */
    public void run()
    {
        int n;

        try
        {
            while ((n = in_.read(buf_)) > 0)
            {
                out_.write(buf_, 0, n);
                out_.flush();

                if (textArea_ != null)
                {
                    textArea_.append(new String(buf_, 0, n));
                    
                    if (follow_)
                        textArea_.setCaretPosition(
                            textArea_.getDocument().getLength());
                }
            }
        }
        catch (IOException e)
        {
        }
        finally
        {
            try
            {
                in_.close();
                out_.close();
            }
            catch (IOException e)
            {
            }
        }
    }
}