package toolbox.tail;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.output.NullWriter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import toolbox.util.service.ServiceException;

/**
 * Tails one or more files.
 */
public class Main extends TailAdapter
{
    // TODO: Consolidate line number decorator into 1 per all instances of a 
    //       given tail instead of one per tail instance.
    
    private static final Logger logger_ = Logger.getLogger(Main.class);

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
     * Simple constructor.
     *
     * @param args 
     *      Array of files to tail. Pass in -f flag to prefix each line with
     *      the name of the file being tailed (useful when tailing multiple 
     *      files and need the ability to know which line of output came from
     *      which file).
     */
    public Main(String args[])
    {
        try
        {
            Option showLineNumbersOption = new Option("l", "lineNumbers", false, "Show line numbers");
            Option showFilenameOption = new Option("f", "filename", false, "Show filenames");
            Option helpOption = new Option("h", "help", false, "Show help");
            Option verboseOption = new Option("v", "verbose", false, "Verbose logging");

            Options options = new Options();
            options.addOption(helpOption);
            options.addOption(showLineNumbersOption);        
            options.addOption(showFilenameOption);
            options.addOption(verboseOption);
    
            CommandLineParser parser = new PosixParser();
            CommandLine cmdLine = parser.parse(options, args, true);
    
            boolean useFilenamePrefix = false;
            boolean useLineNumbers = false;
            
            for (Iterator i = cmdLine.iterator(); i.hasNext();)
            {
                Option option = (Option) i.next();
                String opt = option.getOpt();
                
                if (opt.equals(showLineNumbersOption.getOpt()))
                {
                    useLineNumbers = true;
                }
                else if (opt.equals(showFilenameOption.getOpt()))
                {
                    useFilenamePrefix = true;
                }
                else if (opt.equals(verboseOption.getOpt()))
                {
                    Logger l = Logger.getLogger("toolbox.tail");
                    l.setAdditivity(true);
                    l.setLevel(Level.DEBUG);
                }
                else if (opt.equals(helpOption.getOpt()))
                {
                    printUsage(options);
                    return;
                }
                else 
                {
                    printUsage(options);
                    return;
                }
            }
                
            // Make sure class to find is the only arg
            switch (cmdLine.getArgs().length)
            {
                // Start the search...
                case 0:
                    printUsage(options); 
                    break;                
                
                default:
                    
                    String[] filenames = cmdLine.getArgs();
                
                    //LineNumberDecorator lineNumberDecorator = new LineNumberDecorator(null);
                    
                    for (int i = 0; i < filenames.length; i++)
                    {

                        Tail tail = new Tail();
                        tail.follow(new File(filenames[i]), new NullWriter()); // appease the gods
                        TailAdapter sink = new TailToWriter(new OutputStreamWriter(System.out));

                        try
                        {
                            if (useLineNumbers)
                                sink = new LineNumberDecorator(sink);

                            if (useFilenamePrefix)
                                sink = new LineBeginsWithFilenameDecorator(sink, filenames[i]);
                            
                            tail.addTailListener(sink);
                            tail.start();
                        }
                        catch (ServiceException se)
                        {
                            logger_.error("Main", se);
                        }
                    }
                    
                    return;
            }
        }
        catch (Exception e)
        {
            logger_.error("main", e);   
        }        
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Prints program usage to standard output.
     */
    private void printUsage(Options options)
    {
        HelpFormatter f = new HelpFormatter();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        f.printHelp(
            pw, 
            80, 
            "tail " + "[options] <filename> [,<filename>]", 
            "", 
            options, 
            2, 
            4,
            "",
            false);
        
        System.out.println(sw.toString());
    }
    
    // -------------------------------------------------------------------------
    // Class LineBeginsWithFilenameDecorator 
    // -------------------------------------------------------------------------
    
    class LineBeginsWithFilenameDecorator extends TailAdapter
    {
        private TailAdapter delegate;
        private String filename;
        
        public LineBeginsWithFilenameDecorator(
            TailAdapter delegate, 
            String filename)
        {
            this.delegate = delegate;
            this.filename = filename;
        }
 
        public void nextLine(Tail tail, String line) 
        {
            delegate.nextLine(tail, "[" + filename + "] " + line);
        }
    }
    
    // -------------------------------------------------------------------------
    // Class LineNumberDecorator 
    // -------------------------------------------------------------------------
    
    class LineNumberDecorator extends TailAdapter
    {
        private TailAdapter delegate;
        int counter = 1;
        
        public LineNumberDecorator(TailAdapter delegate)
        {
            this.delegate = delegate;
        }
 
        public void nextLine(Tail tail, String line) 
        {
            delegate.nextLine(tail, "[" + (counter++) + "] " + line);
        }
    }
}