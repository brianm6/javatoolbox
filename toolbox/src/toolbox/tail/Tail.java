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
import toolbox.util.StringUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.collections.AsMap;
import toolbox.util.io.NullWriter;
import toolbox.util.io.ReverseFileReader;
import toolbox.util.service.AbstractService;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.Startable;
import toolbox.util.service.Suspendable;
import toolbox.util.statemachine.StateMachine;

/**
 * Tail is similar to the Unix "tail -f" command used to tail or follow the end
 * of a stream (usually a log file of some sort). In addition to covering basic
 * functionality, there is an API to facilitate lifecycle management of a tail
 * process. This includes start/stop/pause/unpause behavior for easy inclusion
 * in your own applications. Additionally, for those of you interested in an
 * event driven interface, TailListener is available to provide notification on
 * the key events occuring in the tail's lifecycle.
 * <p>
 * To tail a file and send the output to System.out:
 *
 * <pre class="snippet">
 * Tail tail = new Tail();
 *
 * // Tail server.log and send output to stdout
 * tail.follow(new File("server.log"), new OutputStreamWriter(System.out));
 *
 * // Starts tail in another thread and returns immediately
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
public class Tail implements Startable, Suspendable
{
    private static final Logger logger_ = Logger.getLogger(Tail.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * Default number of lines to print from the bottom of an existing file
     * when a new tail is started.
     */
    public static final int DEFAULT_BACKLOG = 20;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Tail listeners.
     */
    private TailListener[] listeners_;

    /**
     * Writer where tail output is sent.
     */
    private Writer sink_;

    /**
     * Thread attached to the tailing behavior.
     */
    private Thread tailer_;

    /**
     * Reader which is the source of data that is tailed.
     */
    private BufferedReader reader_;

    /**
     * File which is the source of data that is tailed.
     */
    private File file_;

    /**
     * Number of lines to print from the bottom of an existing file when a new
     * tail is started.
     */
    private int backlog_;

    /**
     * Flag that notifies tail to shutdown gracefully.
     */
    private boolean pendingShutdown_;

    /**
     * Thread name..mostly for debugging.
     */
    private String threadName_;
    
    /**
     * State machine for this tail's lifecycle.
     */
    private StateMachine machine_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a Tail.
     */
    public Tail()
    {
        listeners_       = new TailListener[0];
        sink_            = new NullWriter();
        pendingShutdown_ = false;
        backlog_         = DEFAULT_BACKLOG;
        machine_         = AbstractService.createStateMachine(this);
    }

    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Starts the tail.
     * 
     * @see toolbox.util.service.Startable#start()
     */
    public void start() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.START);
        
        String name = "Tail-" + (isFile() ? file_.getName() : threadName_);
        tailer_ = new Thread(new Tailer(), name);
        
        try
        {
            connect();
        }
        catch (FileNotFoundException fnfe)
        {
            throw new ServiceException(fnfe);
        }
        
        tailer_.start();
        machine_.transition(ServiceTransition.START);            
        fireTailStarted();
    }


    /**
     * Stops the tail.
     * 
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop()
    {
        machine_.checkTransition(ServiceTransition.STOP);
        
        try
        {
            pendingShutdown_ = true;

            if (isSuspended())
                resume();

            ThreadUtil.stop(tailer_);

            // Change of plans..when the tail is stopped,
            // don't close the stream.

            //IOUtils.closeQuietly(reader_);
        }
        finally
        {
            tailer_ = null;
            pendingShutdown_ = false;
            machine_.transition(ServiceTransition.STOP);            
            fireTailStopped();
        }
    }


    /**
     * Returns true if the tail is running, false otherwise. This has no
     * bearing on whether the tail is paused or not.
     *
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        //return (tailer_ != null && tailer_.isAlive());
        return getState() == ServiceState.RUNNING;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // Suspendable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if the tail is paused, false otherwise.
     *
     * @see toolbox.util.service.Suspendable#isSuspended()
     */
    public boolean isSuspended()
    {
        return getState() == ServiceState.SUSPENDED;
    }
    
    
    /**
     * Pauses the tail.
     * 
     * @see toolbox.util.service.Suspendable#suspend()
     */
    public void suspend()
    {
        machine_.checkTransition(ServiceTransition.SUSPEND);
        machine_.transition(ServiceTransition.SUSPEND);
    }


    /**
     * Unpauses the tail.
     * 
     * @see toolbox.util.service.Suspendable#resume()
     */
    public void resume()
    {
        machine_.checkTransition(ServiceTransition.RESUME);

        synchronized (this)
        {
            notify();
        }

        machine_.transition(ServiceTransition.RESUME);    
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Follows the given file sending the tail output to the writer.
     *
     * @param readFrom File to tail.
     * @param writeTo Writer to send tail output to.
     */
    public void follow(File readFrom, Writer writeTo)
    {
        file_ = readFrom;
        sink_ = writeTo;
    }


    /**
     * Follows the given reader sending the tail output to the writer.
     *
     * @param readFrom Reader to follow.
     * @param writeTo Writer to send the tail output to.
     * @param threadName Will name the thread. Useful for debugging.
     */
    public void follow(Reader readFrom, Writer writeTo, String threadName)
    {
        if (readFrom instanceof BufferedReader)
            reader_ = (BufferedReader) readFrom;
        else
            reader_ = new BufferedReader(readFrom);

        sink_   = writeTo;
        threadName_ = threadName;
    }


    /**
     * Returns the number of backlog lines to print when initially tailing a
     * file.
     *
     * @return int
     */
    public int getBacklog()
    {
        return backlog_;
    }


    /**
     * Sets the number of lines to backlog.
     *
     * @param i Number of backlog lines to print when initially tailing a file.
     */
    public void setBacklog(int i)
    {
        backlog_ = i;
    }


    /**
     * Returns true if tailing a file, false if tailing a reader.
     *
     * @return boolean
     */
    public boolean isFile()
    {
        return file_ != null;
    }


    /**
     * Returns the file being tailed.
     *
     * @return File
     */
    public File getFile()
    {
        return file_;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * Dumps tail a debug string.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return AsMap.of(this).toString();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Wait while the tail is paused.
     */
    protected void checkPaused()
    {
        if (isSuspended())
        {
            fireTailPaused();

            synchronized (this)
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
     * Shows the backlog of the file being followed.
     *
     * @throws IOException on I/O error.
     * @throws FileNotFoundException on non-existant file.
     */
    protected void showBacklog() throws IOException, FileNotFoundException
    {
        if (isFile())
        {
            ReverseFileReader reverser = new ReverseFileReader(file_);
            Stack backlog = new Stack();

            for (int i = 0; i < backlog_; i++)
            {
                String line = reverser.readLineNormal();

                if (line != null)
                    backlog.push(line);
                else
                    break;
            }

            while (!backlog.isEmpty())
                fireNextLine((String) backlog.pop());

            reverser.close();
        }
    }


    /**
     * Connects to the available source for data. Reader or File.
     *
     * @throws FileNotFoundException if file not found.
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
     * Adds a listener to the tail.
     *
     * @param listener Listener to add.
     */
    public void addTailListener(TailListener listener)
    {
        listeners_ = (TailListener[]) ArrayUtil.add(listeners_, listener);
    }


    /**
     * Removes a listener from the tail.
     *
     * @param listener Listener to remove.
     */
    public void removeTailListener(TailListener listener)
    {
        listeners_ = (TailListener[]) ArrayUtil.remove(listeners_, listener);
    }


    /**
     * Fires event for availability of the next line of the tail.
     *
     * @param line Next line of the tail.
     */
    protected void fireNextLine(String line)
    {
        try
        {
            sink_.write(line + StringUtil.NL);
            sink_.flush();
        }
        catch (Exception e)
        {
            logger_.error("fireNextLine", e);
        }

        for (int i = 0; i < listeners_.length; i++)
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
     * Fires event when tail is stopped.
     */
    protected void fireTailStopped()
    {
        ArrayUtil.invoke(listeners_, "tailStopped", new Object[] {this});
    }


    /**
     * Fires event when tail is started.
     */
    protected void fireTailStarted()
    {
        ArrayUtil.invoke(listeners_, "tailStarted", new Object[] {this});
    }


    /**
     * Fires event when tail has reached the end of stream/reader/etc.
     */
    protected void fireTailEnded()
    {
        ArrayUtil.invoke(listeners_, "tailEnded", new Object[] {this});
    }


    /**
     * Fires an event when the tail is unpaused.
     */
    protected void fireTailUnpaused()
    {
        ArrayUtil.invoke(listeners_, "tailUnpaused", new Object[] {this});
    }


    /**
     * Fires an event when the tail is paused.
     */
    protected void fireTailPaused()
    {
        ArrayUtil.invoke(listeners_, "tailPaused", new Object[] {this});
    }


    /**
     * Fires an event when the tail is re-attached to its source.
     */
    protected void fireReattached()
    {
        ArrayUtil.invoke(listeners_, "tailReattached", new Object[] {this});
    }

    //--------------------------------------------------------------------------
    // Tailer
    //--------------------------------------------------------------------------

    /**
     * Continuous tailer.
     */
    class Tailer implements Runnable
    {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            try
            {
                showBacklog();

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
                if (!pendingShutdown_)
                    logger_.error("Tailer.run", e);
            }
        }
    }
}