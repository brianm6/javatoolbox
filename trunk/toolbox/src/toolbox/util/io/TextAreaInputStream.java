package toolbox.util.ui.console;

import java.awt.Toolkit;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
// TextAreaInputStream
//--------------------------------------------------------------------------

public class TextAreaInputStream extends PipedInputStream
{
    private static final Logger logger_ = 
        Logger.getLogger(TextAreaInputStream.class);
    
    private static String EOL = System.getProperty("line.separator", "\n");
    
    private OutputStream out;
    private int numKeysTyped;

    public TextAreaInputStream()
    {
        try
        {
            out = new PipedOutputStream(this);
            numKeysTyped = 0;
        }
        catch (IOException e)
        {
            logger_.error("<init>", e);
        }
    }


    /**
     * Process the end of line (as received from paste) but no other.
     */
    private void send(char ch)
    {
        try
        {
            if (ch == 10)
            { 
                // LF
                byte[] beol = EOL.getBytes();
                out.write(beol, 0, beol.length);
                out.flush();
                numKeysTyped = 0;
            }
            else if (ch >= 32 && ch < 256)
            {
                out.write(ch);
                numKeysTyped++;
            }
            else if (ch == 13)
            {
                ; // ignore RETURN
            }
            else
            {
                out.write('?');
                numKeysTyped++;
            }
        }
        catch (IOException e)
        {
            Toolkit.getDefaultToolkit().beep();
            logger_.warn("send", e);
        }
    }


    /**
     * send
     * 
     * @param s
     */
    public void stuff(String s)
    {
        for (int i = 0; i < s.length(); i++)
        {
            send(s.charAt(i));
        }
    }
}