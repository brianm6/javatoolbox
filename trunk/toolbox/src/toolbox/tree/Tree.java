package toolbox.tree;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.lang.StringUtils;

import toolbox.util.ArrayUtil;
import toolbox.util.DateTimeUtil;
import toolbox.util.FileUtil;
import toolbox.util.collections.AsMap;
import toolbox.util.file.FileComparator;
import toolbox.util.io.filter.RegexFileFilter;

/**
 * Generates a graphical representation of a directory structure using ascii
 * characters. 
 * <p>
 * The listing per directory can:
 * <ul>
 *   <li>Include
 *     <ul>
 *       <li>File name
 *       <li>File size
 *       <li>File date/time
 *     </ul>
 *   <li>Be filtered by regular expression on the file name.
 *   <li>Sorted by
 *     <ul>
 *       <li>File name
 *       <li>File size
 *       <li>File date/time
 *     </ul>
 *   </li>
 * </ul>
 * <p>
 * 
 * Example: tree  (with no arguments the current working directory is the root
 *                 and no file information is included)
 * <pre>
 *
 *   apache
 *   |
 *   +---org
 *   |   +---apache
 *   |       +---log4j
 *   |       |   +---config
 *   |       |   |
 *   |       |   +---helpers
 *   |       |   |
 *   |       |   +---net
 *   |       |   |
 *   |       |   +---nt
 *   |       |   |
 *   |       |   +---or
 *   |       |   |
 *   |       |   +---spi
 *   |       |   |
 *   |       |   +---varia
 *   |       |   |
 *   |       |   +---xml
 *   |       |
 *   |       +---regexp
 *   |
 *   +---META-INF
 * 
 * </pre>
 */
public class Tree {
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Spaces indentation per tree branch. 
     */
    private static final String SPACER = "    ";
    
    /** 
     * Tree branch with a continuation. 
     */
    private static final String BAR = "|   ";
    
    /** 
     * Junction in the tree. 
     */
    private static final String JUNCTION = "+";
    
    /** 
     * Tree arm. 
     */
    private static final String ARM = "---";

    /**
     * Formatter for file sizes, etc.
     */
    private static final NumberFormat FILESIZE_FORMATTER = 
        DecimalFormat.getIntegerInstance();
    
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
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Tree confiration options.
     */
    private TreeConfig config;
    
    /** 
     * Filter to identify files. 
     */
    private IOFileFilter fileFilter;

    /** 
     * Root directory of the tree. 
     */
    private File rootDir;

    /**
     * List of directories in canonical form that have already been traversed.
     * Used to make sure that the same path is not traversed again via a link 
     * on the file system. 
     * 
     * @see String
     */
    private List traversed;
    
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
    // Constructors
    //--------------------------------------------------------------------------
    
    public Tree(File rootDir) {
        this(rootDir, new TreeConfig());
    }
    
    /**
     * Creates a tree with the given criteria.
     * 
     * @param rootDir Root directory of the tree.
     * @param showFiles Set to true if you want file info in the tree, false 
     *        otherwise.
     * @param showSize Set to true to print out the size of the file next to the
     *        filename.
     * @param showDate Set to true to print out the files timestamp.
     * @param showAbolutePath Set to true to print out the files abs
     * @param sortBy Set to any of SORT_[NAME|SIZE|NONE] to specify sort order.
     * @param regex File filter expressed as a regular expression.
     * @param writer Output destination.
     * @throws IllegalArgumentException on invalid root dir.
     */
    public Tree(File rootDir, TreeConfig treeConfig) {
        
    	this.traversed = new ArrayList();
        this.rootDir = rootDir;
        this.config = treeConfig;

        // Make sure directory is legit        
        if (!this.rootDir.exists())
            throw new IllegalArgumentException(
                "Directory " + rootDir + " does not exist.");
                
        if (!this.rootDir.isDirectory())
            throw new IllegalArgumentException(
                rootDir + " is not a directory.");

        if (!this.rootDir.canRead())
            throw new IllegalArgumentException(
                "Cannot read from " + this.rootDir);
        
        //
        // If a regex is passed and the user forgot to turn on the -files
        // flag, just turn it on automatically.
        //
        boolean useRegex = !StringUtils.isBlank(config.getRegexFilter());
        
        config.setShowFiles(
            useRegex || 
            config.isShowFileDate() || 
            config.isShowFilesize() 
                ? true 
                : config.isShowFiles());
        
        if (config.isShowFiles())
            fileFilter = new NotFileFilter(DirectoryFileFilter.INSTANCE);
        
        if (useRegex) {
            // TODO: expose case sensetivity?
            fileFilter = new AndFileFilter(
                fileFilter, 
                new RegexFileFilter(
                    config.getRegexFilter(), 
                    false, 
                    RegexFileFilter.APPLY_TO_FILENAME));  
        }
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Prints the tree.
     */    
    public void showTree() {
        showTree(rootDir, "");
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
    
    protected boolean hasTraversed(File dir) {
    	String canonicalPath =  getCanonicalPath(dir);
    	if (canonicalPath != null && traversed.contains(canonicalPath))
    		return true;
    	else 
    		return false;
    }

    private String getCanonicalPath(File dir) {
    	String canonicalPath =  null;
    	
		try {
            canonicalPath = dir.getCanonicalPath();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
		
		return canonicalPath;
	}
    
    /**
     * Recurses the directory structure of the given rootDir and generates a
     * hierarchical text representation.
     * 
     * @param rootDir Root directory.
     * @param level Current level of decorated indentation.
     * @return boolean True.
     */
    protected boolean showTree(File rootDir, String level) {
//    	if (hasTraversed(rootDir)) {
//    		writer_.println(level + SPACER + "[LINK] " + getCanonicalPath(rootDir));
//    		return false;
//    	}
//    	else {
//    		
//    	}
    	
        PrintWriter out = new PrintWriter(config.getOutputWriter(), true);
        
        boolean atRoot = (level.length() == 0);
        
        if (atRoot)
            out.println(rootDir.getAbsolutePath());
            
        // Get list of directories in root
        File[] dirs = rootDir.listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
        
        // If there are permission problems, null is returned
        if (dirs == null) {
        	//writer_.println(level + SPACER + "NULL list of files returned for dir " + rootDir);
        	return false; 
        }
        
        Arrays.sort(dirs, (Comparator) config.getSortBy());
        String filler = (dirs.length == 0 ? SPACER : BAR);
        
        // Print files
        if (config.isShowFiles()) {
            File[] files = rootDir.listFiles( (FileFilter) fileFilter);
            Arrays.sort(files, (Comparator) config.getSortBy());
            
            int longestName = -1; // Number of spaces occupied by longest fname 
            int largestFile = -1; // Number of spaces occupied by largest fsize
            long dirSize = 0;     // Running total of a directory's size
            
            for (int i = 0; i < files.length; i++) {
                out.print(level + filler + files[i].getName());
                
                if (config.isShowFilesize()) {
                    if (longestName == -1)
                        longestName = FileUtil.getLongestFilename(files).getName().length();
                    
                    if (largestFile == -1)
                        largestFile = FILESIZE_FORMATTER.format(
                            FileUtil.getLargestFile(files).length()).length();
                      
                    out.print(StringUtils.repeat(" ", longestName - files[i].getName().length()));
                    String formatted = FILESIZE_FORMATTER.format(files[i].length());
                              
                    out.print(
                        " " + 
                        StringUtils.repeat(" ", largestFile - formatted.length())
                        + formatted);
            
                    // Accumulate directory size
                    dirSize += files[i].length();
                }

                if (config.isShowFileDate()) {
                    out.print("  "); 
                    out.print(DateTimeUtil.formatToSecond(new Date(files[i].lastModified())));
                }
            
                out.println();
            }
            
            
            // Print out the size of the directory
            if (dirSize > 0 && config.isShowFilesize()) {
                String total = FILESIZE_FORMATTER.format(dirSize);
                int tlen = total.length();
                //String dashy = StringUtils.repeat("-", tlen);
                int alotted = longestName + largestFile + 1;
                //String header = StringUtils.repeat(" ", alotted - tlen); 
                
                //writer_.println(level + filler + header + dashy);
                //writer_.println(level + filler + 
                //          header.substring(1) + "." + total + ".");
                
                String s = files.length + " file(s) ";
                
                String gap =
                    StringUtils.repeat(" ", alotted - s.length() - tlen);
                
                out.println(level + filler + s + gap + total);
            }
            
            // Extra line after last file in a dir        
            if (dirs.length > 0)
                out.println(level + BAR);
        }
        
        // Bow out if nothing todo
        if (ArrayUtil.isNullOrEmpty(dirs)) {
            if (atRoot)
                out.println("No subfolders exist");
            return false;
        }

        int len = dirs.length; 

        // Theres at least one child so go ahead and print a BAR
        if (atRoot)
            out.println(BAR);
        
        // Process each subdirectory ..    
        for (int i = 0; i < len; i++) {
            File current = dirs[i];

            out.print(level);
            out.print(JUNCTION);
            out.print(ARM);
            
            if (hasTraversed(current)) {
            	out.println("" + current.getName() + " -> " + getCanonicalPath(current) + "");
            }
            else {
            	traversed.add(getCanonicalPath(current));
                out.print(current.getName());
                out.println();
                
                // Recurse            
                if (i == len - 1 && len > 1) {
                    // At end and more then one dir
                    showTree(current, level + SPACER);
                }
                else if (len > 1) {
                    // More than one dir
                    showTree(current, level + BAR);                
                    out.println(level + BAR);                   
                }
                else {
                    // Not at end                
                    showTree(current, level + SPACER);
                }
            }
        }
        
        return true;
    }
        
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return AsMap.of(this).toString();
    }
}