package toolbox.tail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Category;

import toolbox.util.ThreadUtil;

/**
 * Tail enables the following of a live stream/reader/file. APIs are exposed
 * to facilitate start/stop/pause/wait functionality in addition to adding
 * or removing multiple listeners based on the lifecycle of the tail. 
 * One or more outputstreams/writers can also be specified as the destination
 * for the output for tail.
 */
public class Tail
{
    /** Logger **/
    private static final Category logger_ = Category.getInstance(Tail.class);
    
    /** Number of line for initial backlog **/
    public static final int NUM_LINES_BACKLOG = 10;

    /** Tail listeners **/
    private List listeners_ = new ArrayList();
    
    /** Stream listenres **/
    private List streams_ = new ArrayList();
    
    /** Writer listeners **/
    private List writers_ = new ArrayList();

    /** Thread that runner is associated with **/
    private Thread thread_;

    /** Reader to tail **/
    private Reader reader_;

    /** 
     * Runnable tail runner 
     */
    class TailRunner implements Runnable
    {
        /** Reader to tail **/
        private Reader reader_;
        
        /** Paused state of the runner **/
        private boolean paused_ = false;


        /**
         * Creates a TailRunner
         * 
         * @param  reader  Reader to tail
         */
        public TailRunner(Reader reader)
        {
            reader_ = reader;
        }

        /**
         * Pauses the tail
         */
        public void pause()
        {
            paused_ = true;
        }

        /** 
         * Unpauses the tail 
         */
        public void unpause()
        {
            paused_ = false;
        }

        /**
         * @return  True if the tail is paused, false otherwise
         */
        public boolean isPaused()
        {
            return paused_;
        }


        /**
         * Runnable interface 
         */
        public void run()
        {
            try
            {
                LineNumberReader lnr = new LineNumberReader(reader_);
                int cnt = 0;
                lnr.mark(1000);

                synchronized (TailRunner.class)
                {
                    while (lnr.ready())
                    {
                        cnt++;

                        if ((cnt % NUM_LINES_BACKLOG) == 0)
                            lnr.mark(1000);

                        lnr.readLine();
                    }
                }

                lnr.reset();

                boolean atEnd = false;

                while (!atEnd)
                {

                    /* loop de loop while paused */
                    while (paused_)
                    {
                        fireTailPaused();

                        while (paused_)
                            ThreadUtil.sleep(1);

                        fireTailUnpaused();
                    }

                    String line = lnr.readLine();

                    if (line != null)
                    {
                        fireNextLine(line + "\n");
                    }
                    else    
                    {
                        //Thread.currentThread().yield();
                        ThreadUtil.sleep(1000);
                    }
                    
//                    else
//                    {
//                        atEnd = true;
//                        fireTailEnded();
//                    }
                }
            }
            catch (Exception e)
            {
                logger_.error("run", e);
            }
        }
    }


    /** Tail dispatches on this runner **/
    private TailRunner runner_;


    /**
     * Constructor for Tail.
     */
    public Tail()
    {
    }

    
    /**
     * Tails the given file
     * 
     * @param  filename  File to tail
     * @throws FileNotFoundException if file not found
     */
    public void tail(String filename) throws FileNotFoundException
    {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(filename);
        tail(fis);
    }

    /**
     * Tails an inputstream
     * 
     * @param  stream  Inputstream to tail
     */
    public void tail(InputStream stream)
    {
        InputStreamReader reader = new InputStreamReader(stream);
        tail(reader);
    }

    /**
     * Tails a reader
     * 
     * @param  reader  Reader to tail
     */
    public void tail(Reader reader)
    {
        reader_ = reader;
        start();
    }

    
    /**
     * Starts tail
     */
    public void start()
    {
        if (thread_ == null || !thread_.isAlive())
        {
            runner_ = new TailRunner(reader_);
            thread_ = new Thread(runner_);
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
        if (thread_.isAlive())
        {
            try
            {
                reader_.close();
                thread_.interrupt();
                thread_.join();
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
        if (thread_.isAlive() && !runner_.isPaused())
            runner_.pause();
    }


    /**
     * Unpauses the tail 
     */
    public void unpause()
    {
        if (thread_.isAlive() && runner_.isPaused())
            runner_.unpause();
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
     * Fires event for availability of the next line of the tail
     * 
     * @param  line  Next line of the tail
     */
    public void fireNextLine(String line)
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
    public void fireTailStopped()
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
    public void fireTailStarted()
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
    public void fireTailEnded()
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
    public void fireTailUnpaused()
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
    public void fireTailPaused()
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

    
    /**
     * @return Number of listeners of each type
     */
    public String toString()
    {
        return "Listeners = " + listeners_.size() + "\n" + 
               "Streams   = " + streams_.size() + "\n" + 
               "Writers   = " + writers_.size() + "\n";
    }
}