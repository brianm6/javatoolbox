package toolbox.findclass;

import java.io.IOException;

import org.apache.log4j.Category;
import org.apache.regexp.RESyntaxException;
import toolbox.util.ArrayUtil;
import toolbox.util.args.ArgumentParser;
import toolbox.util.args.Option;
import toolbox.util.args.OptionException;

/**
 * Utility that finds all occurences of a given class in the 
 * CLASSPATH, current directory, and archives (recursively)
 */
public class Main extends FindClassAdapter
{ 
    /** Logger **/
    private static final Category logger_ = Category.getInstance(Main.class);

    
    /**
     * FindClass entry point
     * 
     * @param   args   Args
     */
    public static void main(String args[])
    {
        try
        {
            ArgumentParser parser = new ArgumentParser();
            Option caseSensetiveOpt  = parser.addBooleanOption('c', "caseSensetive");
            Option verboseOpt     = parser.addBooleanOption('v', "verbose");
        
            try
            {
                parser.parse(args);
            }
            catch (OptionException e)
            {
                System.err.println(e.getMessage());
                printUsage();
                System.exit(2);
            }
            
            Boolean caseSensetiveValue = 
                (Boolean) parser.getOptionValue(caseSensetiveOpt);        
            
            boolean caseSensetive = false;
            if (caseSensetiveValue != null)    
                caseSensetive = caseSensetiveValue.booleanValue();

            Boolean verboseValue = 
                (Boolean) parser.getOptionValue(verboseOpt);        
            
            boolean verbose = false;
            if (verboseValue != null)    
                verbose = verboseValue.booleanValue();
            
            if (verbose)
                System.setProperty("findclass.debug","true");
            else
                System.getProperties().remove("findclass.debug");

            String[] otherArgs = parser.getRemainingArgs();
            String classToFind="";
                        
            if (otherArgs.length != 1)
            {
                printUsage();
                System.exit(2);
            }
            else
                classToFind = otherArgs[0];
            
            FindClass finder = new FindClass();
            finder.addFindClassListener(new Main());
            finder.findClass(classToFind, !caseSensetive);
        }
        catch (RESyntaxException re)
        {
            logger_.error("main", re);   
        }
        catch (IOException ioe)
        {
            logger_.error("main", ioe);               
        }
    }


    /**
     * Prints program usage
     */
    private static void printUsage()
    {
        System.out.println("FindClass searches for all occurrences of a class");
        System.out.println("in your classpath and archives visible from the");
        System.out.println("current directory.");
        System.out.println();
        
        System.out.println("Usage  : java toolbox.findclass.Main -i " +
                           "<classToFind>");
                           
        System.out.println("Options: -o, --caseSensetive => Case insensetive search");
        System.out.println("         -v, --verbose       => Turn on verbose debug");
        System.out.println("         <classToFind>       => Name of class to find. Can be a regular expression or "); 
        System.out.println("                                substring occurring anywhere in the FQN of a class.");
    }
 
    /**
     * Implemenation of IFindClassListener
     * 
     * @param  searchResult  Results of class that was found.
     */   
    public void classFound(FindClassResult searchResult)
    {
        System.out.println(
            searchResult.getClassLocation() + " => " + 
            searchResult.getClassFQN());   
    }
}
