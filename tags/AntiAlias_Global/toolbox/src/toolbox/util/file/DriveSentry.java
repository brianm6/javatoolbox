package toolbox.util.file;

import java.io.File;
import java.util.Date;

import toolbox.util.ThreadUtil;

/**
 * enclosing_type
 */
public class DriveSentry implements FileStuffer.IStuffProvider
{
    private int delay_ = 5000;

    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */    
    public static void main(String[] args)
    {
        new DriveSentry("c:\\fs.txt");
        new DriveSentry("d:\\fs.txt");
        new DriveSentry("e:\\fs.txt");
        new DriveSentry("f:\\fs.txt");
        new DriveSentry("g:\\fs.txt");
        new DriveSentry("h:\\fs.txt");
        new DriveSentry("i:\\fs.txt");
        new DriveSentry("j:\\fs.txt");
        new DriveSentry("k:\\fs.txt");
        
        
        ThreadUtil.join();
    }

    /**
     * Constructor for DriveSentry.
     * 
     * @param  file  File to dump data to
     */
    public DriveSentry(String file)
    {
        File f = new File(file);
        
        if (f.exists())
            f.delete();
            
        FileStuffer fs = new FileStuffer(f, delay_, this, true);
        fs.start();
        
        System.out.println("Drive sentry started on " + f);
    }

    //--------------------------------------------------------------------------
    // Interface FileStuffer.IStuffProvider
    //--------------------------------------------------------------------------
        
    /**
     * @return Date
     */
    public Object getStuff()
    {
        return new Date();
    }
}