package toolbox.tail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.Stack;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.Stringz;
import toolbox.util.ThreadUtil;
import toolbox.util.collections.AsMap;
import toolbox.util.io.NullWriter;
import toolbox.util.io.ReverseFileReader;

/**
 * Tail is similar to the Unix "tail -f" command used to tail or follow the
 * end of a stream (usually a log file of some sort). In addition to covering
 * basic functionality, there is an API to facilitate lifecycle management
 * of a tail process. This includes start/stop/pause/unpause behavior for
 * easy inclusion in your own applications. Additionally, for those of you 
 * interested in an event driven interface, TailListener is available to 
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
    
    /** Default number of lines to print for backlog */ 
    public static final int DEFAULT_BACKLOG = 20;

    /** Tail listeners */ 
    private TailListener[] listeners_;
    
    /** Writer where tail output will be sent */ 
    private Writer sink_;

    /** Tailer thread */ 
    private Thread tailer_;

    /** Reader which tail will follow */
    private BufferedReader reader_;
    
    /** File which tail will follow */ 
    private File file_;

    /** Number of lines backlog to print if tailing a file */
    private int backlog_;
     
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
        listeners_       = new TailListener[0];
        sink_            = new NullWriter();
        paused_          = false;
        pendingShutdown_ = false;
        backlog_         = DEFAULT_BACKLOG;
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
        file_    = readFrom;
        sink_    = writeTo;
    }

    /**
     * Follows the given reader sending the tail output to the writer.
     * 
     * @param   readFrom  Reader to follow
     * @param   writeTo   Writer to send the tail output to
     */
    public void follow(Reader readFrom, Writer writeTo) 
    {
        if (readFrom instanceof BufferedReader)
            reader_ = (BufferedReader) readFrom;
        else
            reader_ = new BufferedReader(readFrom);            
            
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
            String name = "Tail-" + (isFile() ? file_.getName() : "Reader");
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
                reader_.close();
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
        {
            paused_ = false;
            
            synchronized(this)
            {
                notify();
            }
        }
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

    /**
     * @return Number of backlog lines to print when initially tailing a file
     */
    public int getBacklog()
    {
        return backlog_;
    }

    /**
     * @param i Number of backlog lines to print when initially tailing a file
     */
    public void setBacklog(int i)
    {
        backlog_ = i;
    }

    /**
     * @return  True if tailing a file, false if tailing a reader
     */
    public boolean isFile()
    {
        return file_ != null;        
    }

    /**
     * @return File being tailed
     */
    public File getFile()
    {
        return file_;
    }


    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @return String dump
     */
    public String toString()
    {
        return AsMap.of(this).toString();
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Spin while tail is paused
     */
    protected void checkPaused()
    {
        if (paused_)
        {
            fireTailPaused();

            synchronized(this)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException e)
                {
                    logger_.error(e);
                }
            }

            fireTailUnpaused();
        }
    }

    /**
     * Shows the backlog of the file being followed
     * 
     * @throws IOException on I/O error
     * @throws FileNotFoundException on non-existant file
     */
    protected void showBacklog() throws IOException, FileNotFoundException
    {
        if (isFile())
        {
            ReverseFileReader reverser = new ReverseFileReader(file_);
            Stack backlog = new Stack();
                            
            for (int i=0; i < backlog_; i++)
            {
                String line = reverser.readLineNormal();
                
                if (line != null)
                    backlog.push(line);
                else
                    break;
            }
            
            while (!backlog.isEmpty())
                fireNextLine((String)backlog.pop());
                
            reverser.close();
        }
    }

    /**
     * Connects to the available source for data. Reader or File
     * 
     * @throws  FileNotFoundException if file not found
     */
    protected void connect() throws FileNotFoundException
    {
        if (isFile())
            reader_ = new BufferedReader(new FileReader(file_));
        else
            reader_ = new BufferedReader(reader_);
    }

    //--------------------------------------------------------------------------
    // Event Listener Support
    //--------------------------------------------------------------------------

    /**
     * @param  listener   Listener to add
     */
    public void addTailListener(TailListener listener)
    {
        listeners_ = (TailListener[]) ArrayUtil.add(listeners_, listener);
    }

    /**
     * @param  listener  Listener to remove
     */
    public void removeTailListener(TailListener listener)
    {
        listeners_ = (TailListener[]) ArrayUtil.remove(listeners_, listener);
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
            sink_.flush();
        }
        catch (Exception e)
        {
            logger_.error("fireNextLine", e);
        }
        
        for (int i = 0; i< listeners_.length; i++)
        {
            try
            {
                listeners_[i].nextLine(this, line);
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
        ArrayUtil.invoke(listeners_, "tailStopped", new Object[] { this });
    }
    
    /**
     * Fires event when tail is started
     */
    protected void fireTailStarted()
    {
        ArrayUtil.invoke(listeners_, "tailStarted", new Object[] { this });
    }
    
    /**
     * Fires event when tail has reached the end of stream/reader/etc
     */
    protected void fireTailEnded()
    {
        ArrayUtil.invoke(listeners_, "tailEnded", new Object[] { this });
    }
    
    /**
     * Fires an event when the tail is unpaused 
     */
    protected void fireTailUnpaused()
    {
        ArrayUtil.invoke(listeners_, "tailUnpaused", new Object[] { this });
    }
    
    /**
     * Fires an event when the tail is paused 
     */
    protected void fireTailPaused()
    {
        ArrayUtil.invoke(listeners_, "tailPaused", new Object[] { this });
    }

    /**
     * Fires an event when the tail is re-attached to its source 
     */
    protected void fireReattached()
    {
        ArrayUtil.invoke(listeners_, "tailReattached", new Object[] { this });
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
        
    class Tailer implements Runnable
    {
        
        public void run()
        {
            try
            {
                showBacklog();
                
                int cnt                 = 0;
                int strikes             = 0;
                Date preTimeStamp       = null;
                Date resetTimeStamp     = null;
                int timestampThreshHold = 1000;
                int resetThreshHold     = 5000;

                // Seek to end of file
                if (isFile()) 
                    reader_.skip(Integer.MAX_VALUE);          
                                      
                while (!pendingShutdown_)
                {
                    checkPaused();
                    
                    //logger_.info("Tail:before readLine()");
                    
                    String line = reader_.readLine();
    
                    //logger_.info("Tail:readLine(): " + line);
    
                    if (line != null)
                    {
                        fireNextLine(line);
                        strikes = 0;
                    }
                    else    
                    {
                        // check if stream was closed and then reactivated
                        if (strikes == timestampThreshHold && isFile())
                        {
                            // record timestamp of file
                            preTimeStamp = new Date(file_.lastModified());
                        }
                        else if (strikes == resetThreshHold && isFile())
                        {
                            //logger_.debug(method + "reset threshold met");
                            
                            // check timestamps   
                            resetTimeStamp = new Date(file_.lastModified());
                            
                            // if there wasa activity, the timestamp would be
                            // newer.
                            if (resetTimeStamp.after(preTimeStamp))
                            {
                                // reset the stream and resume...
                                reader_ = 
                                    new BufferedReader(new FileReader(file_));
                                
                                fireReattached();
                                
                                logger_.debug(
                                    "Re-attached to " + file_.getName());
                            }
                            else
                            {
                                ; // logger_.debug(method + 
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