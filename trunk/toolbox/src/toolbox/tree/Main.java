package toolbox.tree;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import toolbox.util.ArrayUtil;
import toolbox.util.file.FileComparator;

/**
 * Command line wrapper for {@link Tree} that support passing tree configuration
 * options via command line options.
 */
public class Main {
    
    //--------------------------------------------------------------------------
    // Constants : Sort Options
    //--------------------------------------------------------------------------
    
    /**
     * Do not sort the results.
     */
    public static final String SORT_NONE = "x";

    /**
     * Sort by file name. 
     */
    public static final String SORT_NAME = "n";

    /**
     * Sort by the file size.
     */
    public static final String SORT_SIZE = "s";
    
    /**
     * Sort by the file timestamp.
     */
    public static final String SORT_DATE = "d";
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Launcher for tree.
     *
     * @param args  [-f, -s -os, rootDir]
     * @throws Exception on error.
     */
    public static void main(String args[]) throws Exception {
        // command line options and arguments
        String rootDir = null;
        TreeConfig config = new TreeConfig();
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        
        Option fileOption = new Option(
            "f", 
            "files", 
            false, 
            "Includes files in the output");
            
        Option sizeOption = new Option(
            "s", 
            "size", 
            false, 
            "Includes file sizes in the output");
       
        Option dateOption = new Option(
            "d", 
            "date/time", 
            false, 
            "Includes file date/time in the output");

        Option absolutePathsOption = new Option(
            "a",
            "absolute",
            false,
            "Show absolute paths in output");
        
        Option sortOption = new Option(
            "o", 
            "sort", 
            true, 
            "Sort order");
            
        sortOption.setArgs(1);
        sortOption.setArgName("sort by [n=name|s=size]");
        
        //sortOption.setOptionalArg(true);
        //sortOption.setRequired(false);
        //sortOption.setValueSeparator('=');
        
        Option maxLevelsOption = new Option(
            "l",
            "levels",
            true,
            "Max number of levels to recurse");
        
        maxLevelsOption.setArgs(1);
        maxLevelsOption.setArgName("max levels");
        
        
        Option regexOption = new Option(
            "r", 
            "regexp", 
            true, 
            "Only shows files matching a regular expression");
        
        regexOption.setArgs(1);
        regexOption.setArgName("regular expression");
        
        Option helpOption = new Option(
            "h", 
            "help", 
            false, 
            "Prints usage");
        
        Option helpOption2 = new Option(
            "?", 
            "?", 
            false, 
            "Prints usage");
        
        options.addOption(helpOption2);
        options.addOption(helpOption);
        options.addOption(regexOption);
        options.addOption(fileOption);
        options.addOption(sizeOption);
        options.addOption(dateOption);
        options.addOption(maxLevelsOption);
        options.addOption(absolutePathsOption);
        options.addOption(sortOption);

        CommandLine cmdLine = parser.parse(options, args, true);
    
        for (Iterator i = cmdLine.iterator(); i.hasNext();) {
            Option option = (Option) i.next();
            String opt = option.getOpt();
            
            if (opt.equals(fileOption.getOpt())) {
                config.setShowFiles(true);
            }
            else if (opt.equals(sizeOption.getOpt())) {
                config.setShowFilesize(true);
            }
            else if (opt.equals(dateOption.getOpt())) {
                config.setShowFileDate(true);
            }
            else if (opt.equals(absolutePathsOption.getOpt())) {
                config.setShowPath(true);
            }
            else if (opt.equals(sortOption.getOpt())) {
                Map sortByMap = new HashMap();
                sortByMap.put(SORT_NONE, null);
                sortByMap.put(SORT_NAME, FileComparator.COMPARE_NAME);
                sortByMap.put(SORT_SIZE, FileComparator.COMPARE_SIZE);
                sortByMap.put(SORT_DATE, FileComparator.COMPARE_DATE);
                
                String sortValue = sortOption.getValue();
                
                if (!sortByMap.containsKey(sortValue))
                    throw new IllegalArgumentException(
                        "Sort by option'" + sortValue + "' is invalid.");

                config.setSortBy((FileComparator) sortByMap.get(sortValue));
            }
            else if (opt.equals(maxLevelsOption.getOpt())) {
                config.setMaxLevels(Integer.parseInt(maxLevelsOption.getValue()));
            }
            else if (opt.equals(regexOption.getOpt())) {
                config.setRegexFilter(regexOption.getValue());
            }
            else if (opt.equals(helpOption.getOpt())  ||
                     opt.equals(helpOption2.getOpt())) {
                printUsage(options);
                return;
            }
            else {
                throw new IllegalArgumentException("Option " + opt + " not understood.");
            }
        }
        
        // Root directory argument        
        switch (cmdLine.getArgs().length) {
            
            case 0  : rootDir = System.getProperty("user.dir"); 
                      break;
            
            case 1  : rootDir = cmdLine.getArgs()[0]; 
                      break;
            
            default : System.err.println("ERROR: Invalid arguments " + 
                          ArrayUtil.toString(cmdLine.getArgs()));
                      printUsage(options); 
                      return;
        }
        
        // Create us a tree and let it ride..
        try {
            if (rootDir != null) {
                Tree t = new Tree(new File(rootDir), config);
                t.showTree();
            }
        }
        catch (IllegalArgumentException e) {
            System.err.println("ERROR: " + e.getMessage());
            printUsage(options);
        }
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Prints program usage.
     * 
     * @param options Command line options.
     */
    protected static void printUsage(Options options) {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp(
            "tree [options] <directory>",
            "Shows directory structure in a tree hierarchy.",
            options,
            "");
    }
}