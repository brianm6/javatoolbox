package toolbox.tail;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

/**
 * Tails one or more files
 */
public class Main extends TailAdapter
{
    private static final Logger logger_ = 
        Logger.getLogger(Main.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint 
     * 
     * @param  args  List of files to tail
     */    
    public static void main(String args[])
    {
        new Main(args);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Arg constructor
     * 
     * @param  args  Array of files to tail
     */
    public Main(String args[])
    {
    
        if (args.length == 0)
        {
            printUsage();
            return;
        }
            
        String files[] = args;

        for (int i = 0; i < files.length; i++)
        {
            try
            {
                Tail tail = new Tail();
                tail.addTailListener(this);
                tail.setTailFile(files[i]);
                tail.start();
            }
            catch (FileNotFoundException fnfe)
            {
                logger_.error("Main", fnfe);
            }
        }
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Program usage 
     */
    private void printUsage()
    {
        System.out.println(
            "Program : Tails a list of files");
        System.out.println(
            "Usage   : java toolbox.tail.Main [file1 file2 ... file8]");
        System.out.println(
            "Example : java toolbox.tail.Main appserver.log");
    }
 
    //--------------------------------------------------------------------------
    // Overridden from TailAdapter
    //--------------------------------------------------------------------------
    
    /**
     * Override and print out the next line 
     * 
     * @param  line  Next line of tail
     */    
    public void nextLine(String line)
    {
        System.out.println(line);
    }
}