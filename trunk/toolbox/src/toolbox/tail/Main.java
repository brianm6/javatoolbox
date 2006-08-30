package toolbox.tail;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

import toolbox.util.io.NullWriter;
import toolbox.util.service.ServiceException;

/**
 * Tails one or more files.
 */
public class Main extends TailAdapter
{
    private static final Logger logger_ = Logger.getLogger(Main.class);

    private boolean prefixWithFilename = false;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String args[])
    {
        new Main(args);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a Main.
     *
     * @param args Array of files to tail.
     */
    public Main(String args[])
    {
        if (args.length == 0)
        {
            printUsage();
            return;
        }

        String files[] = args;
        int fi = 0;
        
        if ("-f".equals(files[0]))
        {
            prefixWithFilename = true;
            fi = 1;
        }
                        

        for (int i = fi; i < files.length; i++)
        {
            try
            {
                Tail tail = new Tail();
                
                if (!prefixWithFilename)
                    tail.follow(new File(files[i]), new OutputStreamWriter(System.out));
                else
                {
                    tail.follow(new File(files[i]), new NullWriter());
                    tail.addTailListener(
                        new LineBeginsWithFilenameDecorator(
                            new TailToWriter(
                                new OutputStreamWriter(System.out)), 
                            files[i]));
                }
                
                tail.start();
            }
            catch (ServiceException se)
            {
                logger_.error("Main", se);
            }
        }
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Prints program usage to standard output.
     */
    private void printUsage()
    {
        System.out.println("Program : Tails a list of files");
        System.out.println("Usage   : java toolbox.tail.Main [file1 file2 ... file n]");
        System.out.println("Example : java toolbox.tail.Main appserver.log");
    }
    
    // -------------------------------------------------------------------------
    // 
    // -------------------------------------------------------------------------
    
    class LineBeginsWithFilenameDecorator extends TailAdapter
    {
        private TailToWriter delegate;
        private String filename;
        
        public LineBeginsWithFilenameDecorator(TailToWriter delegate, String filename)
        {
            this.delegate = delegate;
            this.filename = filename;
        }
 
        public void nextLine(Tail tail, String line) {
            
            Writer w = delegate.getWriter();
            
            try 
            {
                w.write("[");
                w.write(filename);
                w.write("] ");
            }
            catch (Exception e) 
            {
                System.err.println(e);
            }
            finally
            {
                delegate.nextLine(tail, line);
            }
        }
    }
    
}