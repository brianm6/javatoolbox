package toolbox.tail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import toolbox.util.Stringz;
import toolbox.util.ThreadUtil;
import toolbox.util.io.NullWriter;

/**
 * Tail is similar to the Unix "tail -f" command used to tail or follow the
 * end of a stream (usually a log file of some sort). In addition to covering
 * basic functionality, there is an API to facilitate lifecycle management
 * of a tail process. This includes start/stop/pause/unpause behavior for
 * easy inclusion in your own applications. Additionally, for those of you 
 * interested in an event driven interface, ITailListener is available to 
 * provide notification on the key events occuring in the tail's lifecycle.
 * <p>
 * To tail a file and send the output to System.out:
 * <pre>
 * Tail tail = new Tail();
 * 
 * // Tail server.log and send output to stdout
 * tail.follow(
 *     new File("server.log"), 
 *     new OutputStreamWriter(System.out));
 * 
 * // Starts tailer thread; returns immediately
 * tail.start();
 * 
 * // Later on... 
 * tail.pause();
 * tail.unpause();
 * 
 * // All done..cleanup 
 * tail.stop();
 * 
 * // Change of mind...wheee
 * tail.start();  
 * </pre>
 */
public class Tail
{
    private static final Logger logger_ = 
        Logger.getLogger(Tail.class);
    
    /** Number of line for initial backlog */ 
    public static final int BACKLOG = 20;

    /** Tail listeners */ 
    private List listeners_;
    
    /** Writer where tail output will be sent */ 
    private Writer sink_;

    /** Tailer thread */ 
    private Thread tailer_;

    /** Reader which tail will follow */
    private Reader faucet_;
    
    /** File which tail will follow */ 
    private File fileFaucet_;

    /** Paused state of the tailer (not thread!) */
    private boolean paused_;

    /** Flag set if the tailer thread needs to shutdown */ 
    private boolean pendingShutdown_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public Tail()
    {
        listeners_       = new ArrayList(1);
        sink_            = new NullWriter();
        paused_          = false;
        pendingShutdown_ = false;
    }

    /**
     * Follows the given file sending the tail output to the writer.
     * 
     * @param   readFrom  File to tail
     * @param   writeTo   Writer to send tail output to
     * @throws  FileNotFoundException if file not found
     */
    public void follow(File readFrom, Writer writeTo) 
        throws FileNotFoundException
    {
        fileFaucet_ = readFrom;
        sink_ = writeTo;
    }

    /**
     * Follows the given reader sending the tail output to the writer.
     * 
     * @param   readFrom  Reader to follow
     * @param   writeTo   Writer to send the tail output to
     */
    public void follow(Reader readFrom, Writer writeTo) 
    {
        faucet_ = readFrom;
        sink_   = writeTo;
    }
    
    /**
     * @return  True if the tail is running, false otherwise. This has no 
     *          bearing on whether the tail is paused or not
     */
    public boolean isAlive()
    {
        return (tailer_ != null && tailer_.isAlive());
    }
    
    /**
     * Starts the tail
     * 
     * @throws  FileNotFoundException on file error
     */
    public void start() throws FileNotFoundException
    {
        if (!isAlive())
        {
            String name = "Tail-" + 
                (fileFaucet_ != null ? fileFaucet_.getName() : "???");
                 
            tailer_ = new Thread(new Tailer(), name);
            connect();
            tailer_.start();
            fireTailStarted();
        }
        else
            logger_.warn("Tail is already running");
    }

    /**
     * Stops the tail
     */
    public void stop()
    {
        if (isAlive())
        {
            try
            {
                pendingShutdown_ = true;
                unpause();
                faucet_.close();
                tailer_.interrupt();
                tailer_.join(10000);
            }
            catch (IOException e)
            {
                logger_.error("stop", e);
            }
            catch (InterruptedException e)
            {
                logger_.error("stop", e);
            }
            finally
            {
                tailer_ = null;
                pendingShutdown_ = false;
                fireTailStopped();
            }
        }
        else
            logger_.warn("Tail is already stopped");
    }

    /**
     * Pauses the tail
     */
    public void pause()
    {
        if (isAlive() && !isPaused())
            paused_ = true;
    }

    /**
     * Unpauses the tail 
     */
    public void unpause()
    {
        if (isAlive() && isPaused())
            paused_ = false;
    }
    
    /**
     * Wait for the tail to reach end of stream
     */
    public void join()
    {
        try
        {
            if (tailer_.isAlive())
                tailer_.join();
        }
        catch (InterruptedException e)
        {
            logger_.error("join", e);
        }
    }

    /**
     * @return  True if the tail is paused, false otherwise
     */
    public boolean isPaused()
    {
        return paused_;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @return Number of listeners of each type
     */
    public String toString()
    {
        return "Listeners = " + listeners_.size() + "\n";
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Spin while tail is paused
     */
    protected void checkPaused()
    {
        // Loop de loop while paused
        while (paused_)
        {
            fireTailPaused();

            while (paused_)
                ThreadUtil.sleep(1);

            fireTailUnpaused();
        }
    }

    /**
     * Connects to the available source for data. Reader or File
     * 
     * @throws  FileNotFoundException if file not found
     */
    protected void connect() throws FileNotFoundException
    {
        if (fileFaucet_ != null)
            faucet_ = new BufferedReader(new FileReader(fileFaucet_));
        else
            faucet_ = new BufferedReader(faucet_);
    }

    //--------------------------------------------------------------------------
    // Event Listener Support
    //--------------------------------------------------------------------------

    /**
     * @param  listener   Listener to add
     */
    public void addTailListener(ITailListener listener)
    {
        listeners_.add(listener);
    }

    /**
     * @param  listener  Listener to remove
     */
    public void removeTailListener(ITailListener listener)
    {
        listeners_.remove(listener);
    }
    
    /**
     * Fires event for availability of the next line of the tail
     * 
     * @param  line  Next line of the tail
     */
    protected void fireNextLine(String line)
    {
        try
        {
            sink_.write(line + Stringz.NL);
        }
        catch (Exception e)
        {
            logger_.error("fireNextLine", e);
        }
        
        for (int i = 0, n = listeners_.size(); i < n; i++)
        {
            try
            {
                ITailListener listener = (ITailListener)listeners_.get(i);
                listener.nextLine(line);
            }
            catch (Exception e)
            {
                logger_.error("fireNextLine", e);
            }
        }
    }

    /**
     * Fires event when tail is stopped
     */
    protected void fireTailStopped()
    {
        for (int i = 0, n = listeners_.size(); i < n; i++)
        {
            try
            {
                ITailListener listener = (ITailListener)listeners_.get(i);
                listener.tailStopped();
            }
            catch (Exception e)
            {
                logger_.error("fireTailStopped", e);
            }
        }
    }
    
    /**
     * Fires event when tail is started
     */
    protected void fireTailStarted()
    {
        for (int i = 0, n = listeners_.size(); i < n; i++)
        {
            try
            {
                ITailListener listener = (ITailListener)listeners_.get(i);
                listener.tailStarted();
            }
            catch (Exception e)
            {
                logger_.error("fireTailStarted", e);
            }
        }
    }
    
    /**
     * Fires event when tail has reached the end of stream/reader/etc
     */
    protected void fireTailEnded()
    {
        for (int i = 0, n =listeners_.size(); i<n; i++)
        {
            try
            {
                ITailListener listener = (ITailListener)listeners_.get(i);
                listener.tailEnded();
            }
            catch (Exception e)
            {
                logger_.error("fireTailEnded", e);
            }
        }
    }
    
    /**
     * Fires an event when the tail is unpaused 
     */
    protected void fireTailUnpaused()
    {
        for (int i = 0, n = listeners_.size(); i < n; i++)
        {
            try
            {
                ITailListener listener = (ITailListener)listeners_.get(i);
                listener.tailUnpaused();
            }
            catch (Exception e)
            {
                logger_.error("fireTailUnpaused", e);
            }
        }
    }
    
    /**
     * Fires an event when the tail is paused 
     */
    protected void fireTailPaused()
    {
        for (int i = 0, n = listeners_.size(); i < n; i++)
        {
            try
            {
                ITailListener listener = (ITailListener)listeners_.get(i);
                listener.tailPaused();
            }
            catch (Exception e)
            {
                logger_.error("fireTailPaused", e);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Tailer Inner Class
    //--------------------------------------------------------------------------
    
    class Tailer implements Runnable
    {
        public void run()
        {
            try
            {
                BufferedReader lnr = (BufferedReader) faucet_;
                int cnt = 0;
                int estimatedBytesBacklog = BACKLOG * 80;
                //lnr.mark(estimatedBytesBacklog);
    
                while (lnr.ready())
                {
                    cnt++;
    
                    if (((cnt + BACKLOG) % BACKLOG) == 0)
                        lnr.mark(estimatedBytesBacklog);
    
                    lnr.readLine();
                }
    
                lnr.reset();
                int strikes = 0;
    
                Date preTimeStamp = null;
                Date resetTimeStamp = null;
                int timestampThreshHold = 1000;
                int resetThreshHold = 5000;
                                
                while (!pendingShutdown_)
                {
    
                    checkPaused();
                    
    
                    String line = lnr.readLine();
    
                    if (line != null)
                    {
                        fireNextLine(line);
                        strikes = 0;
                    }
                    else    
                    {
                        // check if stream was closed and then reactivated
                        if (strikes == timestampThreshHold && fileFaucet_ != null)
                        {
                            // record timestamp of file
                            preTimeStamp = new Date(fileFaucet_.lastModified());
                        }
                        else if (strikes == resetThreshHold && fileFaucet_ != null)
                        {
                            //logger_.debug(method + "reset threshold met");
                            
                            // check timestamps   
                            resetTimeStamp = new Date(fileFaucet_.lastModified());
                            
                            // if there wasa activity, the timestamp would be
                            // newer.
                            if (resetTimeStamp.after(preTimeStamp))
                            {
                                // reset the stream and stop plaing around..
    
                                lnr = new BufferedReader(new FileReader(fileFaucet_));
                                
                                //long skipped = lnr.skip(Integer.MAX_VALUE);
                                //logger_.debug(method + 
                                //  "Skipped " + skipped + " lines on reset");
                                
                                logger_.debug(
                                    "Re-attached to " + fileFaucet_.getName());
                            }
                            else
                            {
                                ;
                                
                                //logger_.debug(method + 
                                //    "Failed criterai for reset");
                            }
                                
                            strikes = 0;
                        }
                        
                        ThreadUtil.sleep(1);
                        strikes++;
                    }
                }
            }
            catch (Exception e)
            {
                logger_.error("Tail.run", e);
            }
        }
    }
}