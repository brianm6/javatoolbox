package toolbox.findclass;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

/**
 * Utility that finds all occurences of a given class in the CLASSPATH, 
 * current directory, and archives (recursively).
 */
public class Main extends FindClassAdapter
{ 
    private static final Logger logger_ = 
        Logger.getLogger(Main.class);

    /**
     * Writer that output is sent to
     */
    private PrintWriter writer_;
    
    /**
     * Number of classes found that match the search criteria
     */    
    private int numFound_;
    
    /**
     * Case sensetivity search flag
     */
    private boolean caseSensetive_;
    
    /**
     * Flag to show the list of search targets 
     */
    private boolean showTargets_;
    
    /**
     * Search string expressed as a regular expression
     */    
    private String classToFind_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * FindClass entry point
     * 
     * @param args Optional switches + name of class to find
     */
    public static void main(String args[])
    {
        try
        {
            CommandLineParser parser = new PosixParser();
            Options options = new Options();

            // Valid options            
            Option caseOption = 
                new Option("c","caseSensetive", false, "Case sensetive search");
                
            Option targetsOption = 
                new Option("t", "targets", false, "Lists the search targets");
                
            Option helpOption = new Option("h", "help", false, "Print usage");
            Option helpOption2 = new Option("?", "/?", false, "Print Usage");
            
            options.addOption(helpOption2);
            options.addOption(helpOption);
            options.addOption(caseOption);        
            options.addOption(targetsOption);
    
            // Parse options
            CommandLine cmdLine = parser.parse(options, args, true);
    
            // Set system.out to default output stream
            Main mainClass = new Main(new OutputStreamWriter(System.out));
            
            // Handle options
            for (Iterator i = cmdLine.iterator(); i.hasNext(); )
            {
                Option option = (Option) i.next();
                String opt = option.getOpt();
                
                if (opt.equals(caseOption.getOpt()))
                {
                    mainClass.setCaseSensetive(true);
                }
                else if (opt.equals(targetsOption.getOpt()))
                {
                    mainClass.setShowTargets(true);
                }
                else if (opt.equals(helpOption.getOpt())  ||
                         opt.equals(helpOption2.getOpt()))
                {
                    mainClass.printUsage();
                    return;
                }
            }
                
            // Make sure class to find is the only arg
            switch (cmdLine.getArgs().length)
            {
                // Start the search...
                case 1:
                    mainClass.setClassToFind(cmdLine.getArgs()[0]);
                    mainClass.search();
                    break;                
                
                // Invalid
                case  0: 
                default: 
                    mainClass.printUsage(); 
                    return;
            }
        }
        catch (RESyntaxException re)
        {
            logger_.error("main", re);   
        }
        catch (ParseException pe)
        {
            logger_.error("main", pe);
        }
        catch (IOException ioe)
        {
            logger_.error("main", ioe);               
        }
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates main with the given writer for output
     * 
     * @param  writer  Writer that output will be written to
     */
    public Main(Writer writer)
    {
        setWriter(writer);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Starts the search
     * 
     * @throws RESyntaxException on invalid regular expression
     * @throws IOException on I/O error
     */
    public void search() throws RESyntaxException, IOException
    {
        FindClass finder = new FindClass();
        finder.addSearchListener(this);

        if (showTargets_)
        {        
            writer_.println("Search targets");
            writer_.println("==============================");
            
            for(Iterator i = finder.getSearchTargets().iterator(); 
                i.hasNext(); writer_.println(i.next()));
                
            writer_.println("==============================");                
        }        
        
        finder.findClass(classToFind_, !caseSensetive_);
        
        if (numFound_ == 0)
            writer_.println("No matches found.");
            
        writer_.flush();
    }

    /**
     * Mutator for case sensetive flag
     * 
     * @param b Case sensetive flag
     */    
    public void setCaseSensetive(boolean b)
    {
        caseSensetive_ = b;
    }

    /**
     * Mutator for the show targets flag
     * 
     * @param b Show targets flag
     */    
    public void setShowTargets(boolean b)
    {
        showTargets_ = b;
    }

    /**
     * Mutator for the class to find
     * 
     * @param find Class to find
     */
    public void setClassToFind(String find)
    {
        classToFind_ = find;
    }

    /**
     * Mutator for the output of the program
     *
     * @param writer Writer to send output to
     */
    public void setWriter(Writer writer)
    {
        writer_ = new PrintWriter(writer);
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Prints program usage
     */
    private void printUsage()
    {
        writer_.println("FindClass searches for all occurrences of a class");
        writer_.println("in your classpath and archives visible from the");
        writer_.println("current directory.");
        writer_.println();
        
        writer_.println("Usage  : java toolbox.findclass.Main [-c -t -h] " +
                           "<classToFind>");
                           
        writer_.println("Options: -c, --caseSensetive => Case sensetive search");
        writer_.println("         -t, --targets       => Lists search targets");
        writer_.println("         -h, --help          => Print help");
        writer_.println("         <classToFind>       => Name of class to find. Can be a regular expression or "); 
        writer_.println("                                substring occurring anywhere in the FQN of a class.");
        
        writer_.flush();
    }
 
    //--------------------------------------------------------------------------
    // Overrides FindClassAdapter
    //--------------------------------------------------------------------------
 
    /**
     * Implementation of IFindClassListener
     * 
     * @param searchResult Results of class that was found.
     */   
    public void classFound(FindClassResult searchResult)
    {
        numFound_++;
        
        writer_.println(
            searchResult.getClassLocation() + " => " + 
            searchResult.getClassFQN());   
            
        writer_.flush();
    }
}