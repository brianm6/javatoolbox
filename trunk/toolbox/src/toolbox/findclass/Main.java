package toolbox.findclass;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

/**
 * Utility that finds all occurences of a given class in the CLASSPATH, current
 * directory, and archives (recursively).
 * 
 * @see toolbox.findclass.FindClass
 */
public class Main extends FindClassAdapter
{ 
    private static final Logger logger_ = Logger.getLogger(Main.class);

    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Writer that output is sent to.
     */
    private PrintWriter writer_;
    
    /**
     * Number of classes found that match the search criteria.
     */    
    private int numFound_;
    
    /**
     * Case sensetivity search flag.
     */
    private boolean caseSensetive_;
    
    /**
     * Flag to show the list of search targets. 
     */
    private boolean showTargets_;
    
    /**
     * Search string expressed as a regular expression.
     */    
    private String classToFind_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * FindClass entry point.
     * 
     * @param args Optional switches + name of class to find.
     */
    public static void main(String args[])
    {
        try
        {
            CommandLineParser parser = new PosixParser();
            Options options = new Options();

            // Valid options            
            Option caseOption = new Option("c", "caseSensetive", false, "Case sensetive search");
            Option targetsOption = new Option("t", "targets", false, "Lists the search targets");
            Option helpOption = new Option("h", "help", false, "Print usage");
            Option helpOption2 = new Option("?", "/?", false, "Print Usage");
            
            options.addOption(helpOption2);
            options.addOption(helpOption);
            options.addOption(caseOption);        
            options.addOption(targetsOption);
    
            // Parse options
            CommandLine cmdLine = parser.parse(options, args, true);
    
            // Send output to System.out
            Main mainClass = new Main(new PrintWriter(System.out, true));
            
            // Handle options
            for (Iterator i = cmdLine.iterator(); i.hasNext();)
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
                else if (opt.equals(helpOption.getOpt()) ||
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
        catch (Exception e)
        {
            logger_.error("main", e);   
        }
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates main with the given writer for output.
     * 
     * @param  writer  Writer that output will be written to.
     */
    public Main(PrintWriter writer)
    {
        setWriter(writer);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Starts the search.
     * 
     * @throws RESyntaxException on invalid regular expression.
     * @throws IOException on I/O error.
     */
    public void search() throws RESyntaxException, IOException
    {
        FindClass finder = new FindClass();
        finder.addSearchListener(this);

        if (showTargets_)
        {        
            writer_.println("Search targets");
            writer_.println("==============================");
            
            for (Iterator i = finder.getSearchTargets().iterator(); 
                i.hasNext(); writer_.println(i.next()));
                
            writer_.println("==============================");                
        }        
        
        finder.findClass(classToFind_, !caseSensetive_);
        
        if (numFound_ == 0)
            writer_.println("No matches found.");
    }

    
    /**
     * Mutator for case sensetive flag.
     * 
     * @param b Case sensetive flag.
     */    
    public void setCaseSensetive(boolean b)
    {
        caseSensetive_ = b;
    }

    
    /**
     * Mutator for the show targets flag.
     * 
     * @param b Show targets flag.
     */    
    public void setShowTargets(boolean b)
    {
        showTargets_ = b;
    }

    
    /**
     * Mutator for the class to find.
     * 
     * @param find Class to find.
     */
    public void setClassToFind(String find)
    {
        classToFind_ = find;
    }

    
    /**
     * Mutator for the output of the program.
     *
     * @param writer Writer to send output to.
     */
    public void setWriter(PrintWriter writer)
    {
        writer_ = writer;
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Prints program usage.
     */
    private void printUsage()
    {
        String usage = 
        "NAME\n"
        + "    findClass - finds classes in directories, jars, and the CLASSPATH\n"
        + "\n"
        + "    Search order:\n"
        + "    1. Jars and directories in the CLASSPATH\n"
        + "    2. Current directory\n"
        + "    3. Jars that exist in the current directory and subdirectories (recursive)\n"
        + "       \n"
        + "    The regular expression is evaluated against the fully qualified class name.\n"
        + "                   \n"
        + "SYNOPSIS\n"
        + "\n"
        + "    findclass [-h] [-c] [-t] <regular expression>  \n"
        + "\n"
        + "OPTIONS\n"
        + "\n"
        + "    -h  --help           Displays this help message\n"
        + "    -c  --caseSensetive  Case sensetive regular expression\n"
        + "    -t  --targets        List search targets\n"
        + "\n"
        + "EXAMPLES\n"
        + "\n"
        + "    Example 1: Search for classes matching java.lang.Object\n"
        + "\n"
        + "        " + getClass().getName() + " java.lang.Object\n"
        + "\n"
        + "    Example 2: Search for classes ending in Proxy (match case)\n"
        + "\n"
        + "        " + getClass().getName() + " -c Proxy$\n"
        + "\n"
        + "    Example 3: Search for all classes in package org.gnu and list search targets\n"
        + "       \n"
        + "        " + getClass().getName() + " -t org.gnu\n";

        writer_.print(usage);
    }
    
    //--------------------------------------------------------------------------
    // Overrides FindClassAdapter
    //--------------------------------------------------------------------------
 
    /**
     * Prints out the class that was found to the writer.
     * 
     * @param searchResult Results of class that was found.
     */   
    public void classFound(FindClassResult searchResult)
    {
        numFound_++;
        
        writer_.println(
            searchResult.getClassLocation() + " => " + 
            searchResult.getClassFQN());   
    }
}