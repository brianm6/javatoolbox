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
import org.apache.commons.lang.StringUtils;

import toolbox.util.ArrayUtil;
import toolbox.util.file.FileComparator;

/**
 * Command line wrapper for {@link Tree} that supports passing tree 
 * configuration options via command line options.
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
            "Includes file sizes in the output.");
       
        Option dateOption = new Option(
            "d", 
            "date", 
            false, 
            "Includes file date/time in the output.");

        Option dirNameRendererOption = new Option(
            "n",
            "dirname",
            true,
            "Controls how directory names are printed in the tree.");
        
        dirNameRendererOption.setArgs(1);
        dirNameRendererOption.setArgName("n=name only | r=relative | a=absolute");
        
        Option sortOption = new Option(
            "o", 
            "sort", 
            true, 
            "Sorts based on file attribute.");
            
        sortOption.setArgs(1);
        sortOption.setArgName("n=name | s=size | d=date");
        
        //sortOption.setOptionalArg(true);
        //sortOption.setRequired(false);
        //sortOption.setValueSeparator('=');
        
        Option maxDepthOption = new Option(
            "m",
            "depth",
            true,
            "Maximum depth of the tree.");
        
        maxDepthOption.setArgs(1);
        maxDepthOption.setArgName("max depth");
        
        
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
        options.addOption(maxDepthOption);
        options.addOption(dirNameRendererOption);
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
            else if (opt.equals(dirNameRendererOption.getOpt())) {
                String rendererValue = dirNameRendererOption.getValue();
                TreeConfig.IDirNameRenderer renderer = config.getDirNameRenderer();
                
                if (rendererValue.equals("n"))
                    renderer = TreeConfig.DIR_NAME_RENDERER_NAME_ONLY;
                else if (rendererValue.equals("r"))
                    renderer = TreeConfig.DIR_NAME_RENDERER_RELATIVE;
                else if (rendererValue.equals("a"))
                    renderer = TreeConfig.DIR_NAME_RENDERER_ABSOLUTE;
                else
                    warnAndAbort(options,
                        "Invalid valid for directory name renderer: " 
                        + rendererValue);
                    
                config.setDirNameRenderer(renderer);
            }
            else if (opt.equals(sortOption.getOpt())) {
                Map sortByMap = new HashMap();
                sortByMap.put(SORT_NONE, null);
                sortByMap.put(SORT_NAME, FileComparator.COMPARE_NAME);
                sortByMap.put(SORT_SIZE, FileComparator.COMPARE_SIZE);
                sortByMap.put(SORT_DATE, FileComparator.COMPARE_DATE);
                String sortValue = sortOption.getValue();
                
                if (!sortByMap.containsKey(sortValue))
                    warnAndAbort(options, "Sort option invalid: " + sortValue);
                
                config.setSortBy((FileComparator) sortByMap.get(sortValue));
            }
            else if (opt.equals(maxDepthOption.getOpt())) {
                try {
                    config.setMaxDepth(Integer.parseInt(maxDepthOption.getValue()));
                }
                catch (NumberFormatException nfe) {
                    warnAndAbort(options, 
                        "Invalid max depth: " + maxDepthOption.getValue());
                }
            }
            else if (opt.equals(regexOption.getOpt())) {
                String regex = regexOption.getValue();
                
                if (StringUtils.isEmpty(regex))
                    warnAndAbort(options, "Invalid regular expression: " + regex);
                
                config.setRegexFilter(regex);
            }
            else if (opt.equals(helpOption.getOpt())  ||
                     opt.equals(helpOption2.getOpt())) {
                printUsage(options);
                return;
            }
            else {
                warnAndAbort(options, "Invalid option: " + opt);
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
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Warns user than an invalid options was used and aborts execution.
     * 
     * @param options Command line options.
     * @param message Error message.
     */
    private static void warnAndAbort(Options options, String message) {
        printUsage(options);
        throw new IllegalArgumentException(message);
    }

    /**
     * Prints program usage.
     * 
     * @param options Command line options.
     */
    private static void printUsage(Options options) {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp(
            "tree [options] <directory>",
            "Shows directory structure in a tree hierarchy.",
            options,
            "");
    }
}