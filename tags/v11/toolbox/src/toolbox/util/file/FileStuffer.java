package toolbox.util.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import toolbox.util.DateTimeUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.RandomUtil;
import toolbox.util.ThreadUtil;

/**
 * File stuffer continously sends output to a file at a configurable delay
 * interval. Good to use to show a file growing slowly in real time.
 */
public class FileStuffer implements Runnable
{
    /** The output file **/
    private File    file_;
    
    /** The delay between each write **/
    private int     delay_;
    
    /** The thread that the stuffer runs on **/
    private Thread  thread_;
    
    /** Flag to stop the stuffer **/
    private boolean stop_;


    /**
     * FileStuffer entrypoint
     * <pre>
     * 
     * arg[0] = file name
     * arg[1] = delay in milliseconds
     * 
     * </pre>
     */
    public static void main(String args[])
    {
        if (args.length != 2)
            printUsage();
        else    
        {        
            FileStuffer fs = new FileStuffer(new File(args[0]), 
               Integer.parseInt(args[1]));    
            fs.start();
        }
    }

     
    /**
     * Prints program usage
     */ 
    protected static void printUsage()
    {
        System.out.println("FileStuffer writes data to a file at given intervals");
        System.out.println("Usage: FileStuffer <output file> <delay in millis>");
    } 
        
    /**
     * Creates a FileStuffer
     * 
     * @param  file   File to send output to
     * @param  delay  Delay between each write
     */
    public FileStuffer(File file, int delay)
    {
        setFile(file);
        setDelay(delay);
    }


    /**
     * Starts the stuffer
     */    
    public void start()
    {
        thread_ = new Thread(this);
        thread_.start();
    }


    /** 
     * Stops the stuffer 
     */    
    public void stop()
    {
        stop_ = true;
        
        try
        {
            thread_.join(10000);
        }
        catch(InterruptedException ie)
        {
            System.err.println(ie);
            System.err.println(ExceptionUtil.getStackTrace(ie));        
        }
    }

    
    /**
     * Writes out to the file at specified intervals
     */
    public void run()
    {
        PrintWriter pw = null;
        
        try
        {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(getFile())));
            int c = 1;
            
            while (!stop_)
            {
                pw.println("[" + c++ + "]" + DateTimeUtil.format(new Date()));
                
//                if(RandomUtil.nextBoolean())
                    pw.println("");
                
                pw.flush();
                ThreadUtil.sleep(delay_);
            }                
            
        }
        catch(Exception e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        finally
        {
            if (pw != null)
                pw.close();
        }
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
     * Sets the file.
     * 
     * @param file The file to set
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
     * @param delay The delay to set
     */
    public void setDelay(int delay)
    {
        delay_ = delay;
    }
}