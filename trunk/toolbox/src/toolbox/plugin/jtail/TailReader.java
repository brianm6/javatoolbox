package toolbox.jtail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;


public class TailReader implements Runnable
{
    private static final int INIT_LINES = 10;
    
    PrintStream ps;
    LineNumberReader lnr;    
    Thread tailThread;
    boolean keepGoing = true;
        
    /**
     * Creates a TailReader with the given streams
     */
    public TailReader(InputStream is, OutputStream os)
    {
        ps = new PrintStream(os);
        lnr = new LineNumberReader(new InputStreamReader(is));
    }
    
    /**
     * Starts tailing the inputstream 
     */
    public void start()
    {
        tailThread = new Thread(this);
        tailThread.start();
    }
    
    /**
     * Stops tailing the inputstream
     */
    public void stop()
    {
        keepGoing = false;
        try
        {
            tailThread.interrupt();
            tailThread.join();
        }
        catch(InterruptedException e)
        {
        }
        
    }
    
    /**
     * Runnable interface 
     */
    public void run()
    {
        try
        {
            int cnt = 0;
            lnr.mark(1000);

            /* skip to the very end of the stream */
            while (lnr.ready())
            {
                cnt++;
                if ((cnt % INIT_LINES) == 0)
                    lnr.mark(1000);
                lnr.readLine();
            }

            /* skip back to the last mark() */
            lnr.reset();

            
            while (keepGoing)
            {

                if (lnr.ready())
                {
                    String line = lnr.readLine();
                    if (line != null)
                        ps.println(line);
                }
                //Thread.currentThread().sleep(1);
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
