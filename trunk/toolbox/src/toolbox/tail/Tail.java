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

import toolbox.util.ThreadUtil;

/**
 * Tail enables the following of a live stream/reader/file. APIs are 
 * exposed to facilitate start/stop/pause/wait functionality in 
 * addition to adding or removing multiple listeners that report on 
 * the lifecycle of the tail. One or more outputstreams/writers can 
 * also be specified as the destination for the output of the tail.
 */
public class Tail implements Runnable
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(Tail.class);
    
    /** 
     * Number of line for initial backlog 
     */
    public static final int NUM_LINES_BACKLOG = 10;

    /** 
     * Tail listeners 
     */
    private List listeners_ = new ArrayList();
    
    /** 
     * Stream listeners
     */
    private List streams_ = new ArrayList();
    
    /** 
     * Writer listeners 
     */
    private List writers_ = new ArrayList();

    /** 
     * Thread that runner is associated with 
     */
    private Thread thread_;

    /** 
     * Reader to tail 
     */
    private Reader reader_;
    
    /** 
     * File if reader originated at one 
     */
    private File file_;

    /** 
     * Paused state of the runner 
     */
    private boolean paused_ = false;

    /** 
     * Active state of the tail 
     */
    private boolean isAlive_ = false;

    /** 
     * Flag to signify a shutdown is pending 
     */
    private boolean pendingShutdown_ = false;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for Tail.
     */
    public Tail()
    {
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Tails the given file
     * 
     * @param  filename  File to tail
     * @throws FileNotFoundException if file not found
     */
    public void setTailFile(String filename) 
        throws FileNotFoundException
    {
        setTailFile(new File(filename));
    }


    /**
     * Tails the given file
     * 
     * @param  f  File to tail
     * @throws FileNotFoundException if file not found
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
     * @return  True if the tail is running, false otherwise. 
     *           This has no bearing on whether the tail is paused or not
     */
    public boolean isAlive()
    {
        return thread_ != null && thread_.isAlive();
    }
    
    
    /**
     * Starts tail
     * 
     * @throws FileNotFoundException on file error
     */
    public void start() throws FileNotFoundException
    {
        if (!isAlive())
        {
            thread_ = new Thread(this);
            connect();
            thread_.start();
            fireTailStarted();
        }
        else
            logger_.warn("Tail is already running");
    }


    /**
     * Stops tail
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
     * @param  listener   Listener to add
     */
    public void addTailListener(ITailListener listener)
    {
        listeners_.add(listener);
    }


    /**
     * @param  writer  Writer to add
     */
    public void addWriter(Writer writer)
    {
        writers_.add(writer);
    }

    
    /**
     * @param  listener  Listener to remove
     */
    public void removeTailListener(ITailListener listener)
    {
        listeners_.remove(listener);
    }


    /**
     * @param  os  OutputStream to add
     */
    public void addOutputStream(OutputStream os)
    {
        streams_.add(os);
    }

    
    /**
     * @param  os  Outputstream to remove
     */
    public void removeOutputStream(OutputStream os)
    {
        streams_.remove(os);
    }

    
    /**
     * @return Number of listeners of each type
     */
    public String toString()
    {
        return "Listeners = " + listeners_.size() + "\n" + 
               "Streams   = " + streams_.size() + "\n" + 
               "Writers   = " + writers_.size() + "\n";
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
    // Private
    //--------------------------------------------------------------------------

    /**
     * Spin while tail is paused
     */
    protected void checkPaused()
    {
        /* loop de loop while paused */
        while (paused_)
        {
            fireTailPaused();

            while (paused_)
                ThreadUtil.sleep(1);

            fireTailUnpaused();
        }
    }

    /**
     * Sets the file.
     * 
     * @param file The file to set
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
     * @throws FileNotFoundException
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
                os.write(line.getBytes());
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
                w.write(line);
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
     * Fires an event when tail is paused 
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
    //  Interface Runnable
    //--------------------------------------------------------------------------
    
    /**
     * Runnable interface 
     */
    public void run()
    {
        String method = "[run   ] ";
        
        try
        {
            LineNumberReader lnr = (LineNumberReader) reader_;
            int cnt = 0;
            lnr.mark(1000);

            while (lnr.ready())
            {
                cnt++;

                if ((cnt % NUM_LINES_BACKLOG) == 0)
                    lnr.mark(1000);

                lnr.readLine();
            }

            lnr.reset();

            boolean atEnd = false;

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
                            
                            logger_.debug(method + "Re-attached to " + 
                                getFile().getName());
                        }
                        else
                        {
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
            logger_.error(method, e);
        }
    }
}