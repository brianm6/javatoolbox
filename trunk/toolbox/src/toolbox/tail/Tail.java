package toolbox.tail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import toolbox.util.Stringz;
import toolbox.util.ThreadUtil;

/**
 * Tail enables the following of a live stream/reader/file. APIs are exposed to 
 * facilitate start/stop/pause/wait functionality in addition to adding or 
 * removing multiple listeners that report on the lifecycle of the tail. One or 
 * more outputstreams/writers can also be specified as the destination for the 
 * output of the tail.
 * <p>
 * To tail a file and send the output to System.out:
 * <pre>
 * Tail tail = new Tail();
 * tail.setTailFile("server.log");
 * tail.addOutputStream(System.out);
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
public class Tail implements Runnable
{
    private static final Logger logger_ = 
        Logger.getLogger(Tail.class);
    
    /** Number of line for initial backlog */ 
    public static final int NUM_LINES_BACKLOG = 20;

    /** Tail listeners */ 
    private List listeners_;
    
    /** Streams where tail output will be sent */
    private List streams_;
    
    /** Writer where tail output will be sent */ 
    private List writers_;

    /** Tailer thread */ 
    private Thread thread_;

    /** Reader which tail will follow */
    private Reader reader_;
    
    /** File which tail will follow */ 
    private File file_;

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
        listeners_ = new ArrayList(1);
        streams_   = new ArrayList(1);
        writers_   = new ArrayList(1);
        
        paused_          = false;
        pendingShutdown_ = false;
        
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Tails the given file
     * 
     * @param   filename  File to tail
     * @throws  FileNotFoundException if file not found
     */
    public void setTailFile(String filename) throws FileNotFoundException
    {
        setTailFile(new File(filename));
    }

    /**
     * Tails the given file
     * 
     * @param   f  File to tail
     * @throws  FileNotFoundException if file not found
     */
    public void setTailFile(File f) throws FileNotFoundException
    {
        setFile(f);
    }

    /**
     * Tails an inputstream
     * 
     * @param  stream  Inputstream to tail
     */
    public void setTailStream(InputStream stream)
    {
        setTailReader(new InputStreamReader(stream));
    }

    /**
     * Tails a reader
     * 
     * @param  reader  Reader to tail
     */
    public void setTailReader(Reader reader)
    {
        setReader(reader);
    }
    
    /**
     * @return  True if the tail is running, false otherwise. This has no 
     *          bearing on whether the tail is paused or not
     */
    public boolean isAlive()
    {
        return thread_ != null && thread_.isAlive();
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
                (getFile() != null ? getFile().getName() : "???");
                 
            thread_ = new Thread(this, name);
            connect();
            thread_.start();
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
                thread_.interrupt();
                thread_.join(10000);
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
                thread_ = null;
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
            if (thread_.isAlive())
                thread_.join();
        }
        catch (InterruptedException e)
        {
            logger_.error("join", e);
        }
    }

    /**
     * Adds a writer to the list of tail sinks
     * 
     * @param  writer  Writer to add
     */
    public void addWriter(Writer writer)
    {
        writers_.add(writer);
    }

    /**
     * Removes a writer from the list of tail sinks
     * 
     * @param  writer  Writer to remove
     */
    public void removeWriter(Writer writer)
    {
        writers_.remove(writer);
    }
    
    /**
     * Adds an output stream to the list of tail sinks
     * 
     * @param  os  OutputStream to add
     */
    public void addOutputStream(OutputStream os)
    {
        streams_.add(os);
    }
    
    /**
     * Removes an outputstream from the list of tail sinks
     * 
     * @param  os  Outputstream to remove
     */
    public void removeOutputStream(OutputStream os)
    {
        streams_.remove(os);
    }
    
    /**
     * Returns the file.
     * 
     * @return File
     */
    public File getFile()
    {
        return file_;
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
        return "Listeners = " + listeners_.size() + "\n" + 
               "Streams   = " + streams_.size() + "\n" + 
               "Writers   = " + writers_.size() + "\n";
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
     * Sets the tail file
     * 
     * @param  file  File to tail
     */
    protected void setFile(File file)
    {
        file_ = file;
    }

    /**
     * Returns the reader.
     * 
     * @return Reader
     */
    protected Reader getReader()
    {
        return reader_;
    }

    /**
     * Sets the reader.
     * 
     * @param reader The reader to set
     */
    protected void setReader(Reader reader)
    {
        reader_ = reader;
    }

    /**
     * Connects to the provided stream source
     * 
     * @throws  FileNotFoundException if file not found
     */
    protected void connect() throws FileNotFoundException
    {
        if (getFile() != null)
        {
            reader_ = new LineNumberReader(new FileReader(getFile()));
        }
        else
        {
            reader_ = new LineNumberReader(getReader());
        }
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
        for (int i = 0; i < listeners_.size(); i++)
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

        for (int j = 0; j < streams_.size(); j++)
        {
            try
            {
                OutputStream os = (OutputStream)streams_.get(j);
                os.write((line + Stringz.NL).getBytes());
                os.flush();
            }
            catch (IOException e)
            {
                logger_.error("fireNextLine", e);
            }
        }

        for (int k = 0; k < writers_.size(); k++)
        {
            try
            {
                Writer w = (Writer)writers_.get(k);
                w.write(line + Stringz.NL);
                w.flush();
            }
            catch (IOException e)
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
        for (int i = 0; i < listeners_.size(); i++)
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
        for (int i = 0; i < listeners_.size(); i++)
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
        for (int i = 0; i < listeners_.size(); i++)
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
        for (int i = 0; i < listeners_.size(); i++)
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
        for (int i = 0; i < listeners_.size(); i++)
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
    //  Runnable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Runnable interface 
     */
    public void run()
    {
        try
        {
            LineNumberReader lnr = (LineNumberReader) reader_;
            int cnt = 0;
            int estimatedBytesBacklog = NUM_LINES_BACKLOG * 80;
            //lnr.mark(estimatedBytesBacklog);

            while (lnr.ready())
            {
                cnt++;

                if (((cnt + NUM_LINES_BACKLOG) % NUM_LINES_BACKLOG) == 0)
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
                    if (strikes == timestampThreshHold && getFile() != null)
                    {
                        // record timestamp of file
                        preTimeStamp = new Date(getFile().lastModified());
                    }
                    else if (strikes == resetThreshHold && getFile() != null)
                    {
                        //logger_.debug(method + "reset threshold met");
                        
                        // check timestamps   
                        resetTimeStamp = new Date(getFile().lastModified());
                        
                        // if there wasa activity, the timestamp would be
                        // newer.
                        if (resetTimeStamp.after(preTimeStamp))
                        {
                            // reset the stream and stop plaing around..

                            lnr = new LineNumberReader(
                                new FileReader(getFile()));
                            
                            //long skipped = lnr.skip(Integer.MAX_VALUE);
                            //logger_.debug(method + 
                            //  "Skipped " + skipped + " lines on reset");
                            
                            logger_.debug(
                                "Re-attached to " + getFile().getName());
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