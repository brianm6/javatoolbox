package toolbox.util.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;

import toolbox.util.DateTimeUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.service.Startable;

/**
 * File stuffer continously sends output to a file at a configurable delay
 * interval. Good to use to show a file growing slowly in real time.
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 * // Create file to be stuffed
 * File stuffedFile = new File("growing.txt");
 * FileStuffer fs = new FileStuffer(stuffedFile, 500);
 * 
 * // Starts stuffer asynchronously
 * fs.start();
 * 
 * // Stops stuffer 
 * fs.stop();
 * </pre>
 */
public class FileStuffer implements Startable
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * The output file.
     */
    private File file_;
    
    /** 
     * The delay in millis between each write.
     */
    private int delay_;
    
    /** 
     * The thread that the stuffer runs on.
     */
    private Thread thread_;
    
    /** 
     * Flag to stop the stuffer.
     */
    private boolean stop_;
    
    /** 
     * Flag to open/close file instead of append/flush.
     */
    private boolean openClose_;

    /** 
     * File stuffer.
     */
    private IStuffProvider stuffer_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * FileStuffer entrypoint.
     * <pre>
     * 
     * arg[0] = file name
     * arg[1] = delay in milliseconds
     * 
     * </pre>
     * 
     * @param args Filename, delay in millis.
     */
    public static void main(String args[])
    {
        if (args.length != 2)
        {
            printUsage();
        }
        else    
        {        
            FileStuffer fs = new FileStuffer(
                new File(args[0]), Integer.parseInt(args[1])); 
                  
            fs.start();
        }
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FileStuffer.
     * 
     * @param file File to send output to.
     * @param delay Delay between each write.
     */
    public FileStuffer(File file, int delay)
    {
        this(file, delay, false);
    }


    /**
     * Creates a FileStuffer.
     * 
     * @param file File to send output to.
     * @param delay Delay between each write.
     * @param openClose Flag to open/close file between each successive write 
     *        instead of the default behavior to just applend/flush.
     */
    public FileStuffer(File file, int delay, boolean openClose)
    {
        this(file, delay, new DefaultStuffProvider(), openClose);
    }


    /**
     * Creates a FileStuffer.
     * 
     * @param file File to send output to.
     * @param delay Delay between each write.
     * @param stuffer Provides contents to stuff file with.
     * @param openClose Flag to open/close file between each successive write 
     *        instead of the default behavior to just applend/flush.
     */
    public FileStuffer(File file, int delay, IStuffProvider stuffer,
        boolean openClose)
    {
        setFile(file);
        setDelay(delay);
        stuffer_   = stuffer;        
        openClose_ = openClose;
    }

    //--------------------------------------------------------------------------
    // Runnable Interface
    //--------------------------------------------------------------------------
    
    class Runner implements Runnable
    {
        /**
         * Writes out to the file at specified intervals.
         */
        public void run()
        {
            PrintWriter pw = null;
            
            try
            {
                if (!openClose_)
                {
                    pw = new PrintWriter(new BufferedWriter(
                            new FileWriter(getFile())));
                            
                    while (!stop_)
                    {
                        pw.println(stuffer_.getStuff());
                        pw.flush();
                        ThreadUtil.sleep(delay_);
                    }                
                }
                else
                {
                    while (!stop_)
                    {
                        pw = new PrintWriter(new FileWriter(getFile(), true));
                        pw.println(stuffer_.getStuff());
                        pw.close();
                        ThreadUtil.sleep(delay_);
                    }   
                }
            }
            catch (Exception e)
            {
                throw new IllegalArgumentException(e.getMessage());
            }
            finally
            {
                IOUtils.closeQuietly(pw);
            }
        }
    }

    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Starts the stuffer.
     * 
     * @see toolbox.util.service.Startable#start()
     */
    public void start() 
    {
        thread_ = new Thread(new Runner(), "FileStuffer " + file_.getName());
        thread_.start();
    }


    /** 
     * Stops the stuffer.
     */    
    public void stop()
    {
        stop_ = true;
        
        try
        {
            thread_.join(10000);
        }
        catch (InterruptedException ie)
        {
            System.err.println(ie);
            System.err.println(ExceptionUtil.getStackTrace(ie));        
        }
        finally
        {
            thread_ = null;
        }
    }
    
    
    /**
     * Returns true if this stuffer is running, false otherwise.
     * 
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return thread_ != null;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
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
     * Sets the file.
     * 
     * @param file The file to set.
     */
    public void setFile(File file)
    {
        file_ = file;
    }


    /**
     * Returns the delay.
     * 
     * @return int
     */
    public int getDelay()
    {
        return delay_;
    }


    /**
     * Sets the delay.
     * 
     * @param delay The delay to set.
     */
    public void setDelay(int delay)
    {
        delay_ = delay;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
         
    /**
     * Prints program usage.
     */ 
    protected static void printUsage()
    {
        System.out.println(
            "FileStuffer writes data to a file at given intervals");
            
        System.out.println(
            "Usage: FileStuffer <output file> <delay in millis>");
    } 

    //--------------------------------------------------------------------------
    // Interfaces
    //--------------------------------------------------------------------------
    
    /**
     * Interface that gets stuff.
     */
    public interface IStuffProvider
    {
        /**
         * @return Stuff
         */
        Object getStuff();
    }
    
    //--------------------------------------------------------------------------
    // DefaultStuffProvider
    //--------------------------------------------------------------------------
 
    /**
     * Default implementation of IStuffProvider interface.
     */
    static class DefaultStuffProvider implements IStuffProvider
    {
        /**
         * Current count.
         */
        private int cnt_ = 0;
        
        /**
         * Returns a simple numbered line of text with the date and a random
         * integer so its easily recognizable as being unique.
         * 
         * @return Line number plus time.
         */
        public Object getStuff()
        {
            return "[" + cnt_++ + "]" + 
                DateTimeUtil.formatToSecond(new Date()) + " " +
                RandomUtils.nextInt(50000);
        }
    }   
}