package toolbox.tree;

import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.martiansoftware.jsap.stringparsers.StringStringParser;

import toolbox.util.ArrayUtil;
import toolbox.util.DateTimeUtil;
import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.collections.AsMap;
import toolbox.util.file.FileComparator;
import toolbox.util.io.filter.AndFilter;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.FileFilter;
import toolbox.util.io.filter.RegexFilter;

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
 *   <li>Filter filenames by regular expression
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
public class Tree2
{
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
    // Constants : Defaults
    //--------------------------------------------------------------------------

    /** 
     * Files are not shown by default. 
     */
    private static final boolean DEFAULT_SHOWFILES = false;

    /** 
     * File sizes are not shown by default. 
     */
    private static final boolean DEFAULT_SHOWSIZE = false;

    /** 
     * File date/times are not shown by default. 
     */
    private static final boolean DEFAULT_SHOWDATE = false;

    /** 
     * Output is sent to System.out by default. 
     */
    private static final Writer DEFAULT_WRITER = 
        new OutputStreamWriter(System.out);

    /**
     * Sorting is not activate by default.
     */
    private static final String DEFAULT_SORT = SORT_NONE;

    /**
     * Default regular expression is to match all.
     */
    private static final String DEFAULT_REGEX = null;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Output writer. 
     */    
    private PrintWriter writer_;
    
    /** 
     * Filter to identify directories. 
     */
    private FilenameFilter dirFilter_;
    
    /** 
     * Filter to identify files. 
     */
    private FilenameFilter fileFilter_;
    
    /** 
     * Flag to toggle the showing of files. 
     */
    private boolean showFiles_;

    /** 
     * Flag to toggle the showing of a file's size. 
     */
    private boolean showSize_;

    /** 
     * Flag to toggle the showing of a file's timestamp. 
     */
    private boolean showDate_;

    /** 
     * Root directory of the tree. 
     */
    private File rootDir_;

    /**
     * Specifies the sort order. Only valid if showFiles is true.
     */
    private String sortBy_;

    /**
     * Maps from SORT_* option to a Comparator
     */
    private Map sortByMap_;

    /**
     * Regular expression for filtering files.
     */
    private String regex_;
    
    /**
     * Formatter for file sizes, etc.
     */
    private NumberFormat formatter_;

    private static final String SWITCH_FILES = "files";
    private static final String SWITCH_DATE  = "date";
    private static final String SWITCH_HELP  = "help";
    private static final String SWITCH_SIZE  = "size";
    
    private static final String OPTION_REGEX = "regex";
    private static final String OPTION_SORT = "sort";
    private static final String OPTION_DIR = "dir";
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Launcher for tree.
     *
     * @param args  [-f, -s -os, rootDir]
     * @throws Exception on error.
     */
    public static void main(String args[]) throws Exception
    {
        // command line options and arguments
        String rootDir = null;
        boolean showFiles = DEFAULT_SHOWFILES;
        boolean showSize = DEFAULT_SHOWSIZE;
        boolean showDate = DEFAULT_SHOWDATE;
        String sortBy = DEFAULT_SORT;
        String regex = DEFAULT_REGEX;

        ///////////////////////////////////////////////////////////////////////
        
        JSAP jsap = new JSAP();
        
        Switch filesSwitch = 
            new Switch(SWITCH_FILES)
                .setShortFlag('f')
                //.setLongFlag("files")
                .setDefault("false");

        Switch sizeSwitch = 
            new Switch(SWITCH_SIZE)
                .setShortFlag('s')
                //.setLongFlag("size")
                .setDefault("false");
        
        Switch dateSwitch = 
            new Switch(SWITCH_DATE)
                .setShortFlag('d')
                //.setLongFlag("date")
                .setDefault("false");

        Switch helpSwitch = 
            new Switch(SWITCH_HELP)
                .setShortFlag('h')
                //.setLongFlag("help")
                .setDefault("false");

        FlaggedOption regexOption = 
            new FlaggedOption(OPTION_REGEX)
                .setStringParser(new StringStringParser())
                .setRequired(false) 
                .setShortFlag('r') 
                //.setLongFlag("regex")
                .setDefault("");
        
        FlaggedOption sortOption = 
            new FlaggedOption(OPTION_SORT)
                .setStringParser(
                    new EnumeratedStringParser(
                        SORT_NAME + ";" + 
                        SORT_DATE + ";" + 
                        SORT_SIZE))
                .setRequired(false) 
                .setShortFlag('o') 
                //.setLongFlag("sort")
                .setDefault(SORT_NONE);

        UnflaggedOption dirOption =
            new UnflaggedOption(OPTION_DIR)
                .setRequired(false)
                .setStringParser(new StringStringParser());
                
        //
        // Help text
        //
        filesSwitch.setHelp("Includes files");
        sizeSwitch.setHelp("Includes file size");
        dateSwitch.setHelp("Includes file date/time");
        helpSwitch.setHelp("Prints usage");
        regexOption.setHelp("Filter files matching a regular expression");
        sortOption.setHelp("Sorts files by {f = filename, d = date, s = size}");
        dirOption.setHelp("Directory to print a tree for");
        
        jsap.registerParameter(filesSwitch);
        jsap.registerParameter(sizeSwitch);
        jsap.registerParameter(dateSwitch);
        jsap.registerParameter(helpSwitch);
        jsap.registerParameter(regexOption);
        jsap.registerParameter(sortOption);
        jsap.registerParameter(dirOption);
        
        
        JSAPResult config = jsap.parse(args);    

        if (config.getBoolean(SWITCH_HELP, false))
        {
            printUsage(jsap);
            return;
        }
        
        showFiles = config.getBoolean(SWITCH_FILES, DEFAULT_SHOWFILES);
        showSize = config.getBoolean(SWITCH_SIZE, DEFAULT_SHOWSIZE);
        showDate = config.getBoolean(SWITCH_DATE, DEFAULT_SHOWDATE);
        regex = config.getString(OPTION_REGEX, "");
        sortBy = config.getString(OPTION_SORT, DEFAULT_SORT);
        rootDir = config.getString(OPTION_DIR, System.getProperty("user.dir"));
        
        // Create us a tree and let it ride..
        try
        {
            if (rootDir != null)
            {
                Tree2 t = new Tree2(new File(rootDir), 
                                  showFiles, 
                                  showSize,
                                  showDate,
                                  sortBy,
                                  regex);
                t.showTree();
            }
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("ERROR: " + e.getMessage());
            printUsage(jsap);
        }
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Tree that will show files and send the output to System.out
     * with the given root directory.
     * 
     * @param rootDir Root directory.
     */
    public Tree2(File rootDir)
    {
        this(
            rootDir, 
            DEFAULT_SHOWFILES, 
            DEFAULT_SHOWSIZE, 
            DEFAULT_SHOWDATE,
            DEFAULT_SORT,
            DEFAULT_REGEX,
            DEFAULT_WRITER);
    }


    /**
     * Creates a tree that will show files with the given root directory and
     * send output to the given writer.
     * 
     * @param rootDir Root directory.
     * @param writer Output destination.    
     */
    public Tree2(File rootDir, Writer writer)
    {
        this(
            rootDir, 
            DEFAULT_SHOWFILES, 
            DEFAULT_SHOWSIZE, 
            DEFAULT_SHOWDATE, 
            DEFAULT_SORT, 
            DEFAULT_REGEX,
            writer);    
    }


    /**
     * Creates a tree with the given root directory and flag to show files.
     * 
     * @param rootDir Root directory.
     * @param showFiles Set to true if you want file info in the tree, false 
     *        otherwise.
     */
    public Tree2(File rootDir, boolean showFiles)
    {
        this(
            rootDir, 
            showFiles, 
            DEFAULT_SHOWSIZE, 
            DEFAULT_SHOWDATE, 
            DEFAULT_SORT, 
            DEFAULT_REGEX,
            DEFAULT_WRITER);
    }


    /**
     * Creates a tree with the given root directory and flag to show files.
     * 
     * @param rootDir Root directory.
     * @param showFiles If true, includes files (as opposed to just directories)
     *        in the output.
     * @param showSize If true, shows the size of the file.
     */
    public Tree2(File rootDir, boolean showFiles, boolean showSize)
    {
        this(
            rootDir, 
            showFiles, 
            showSize, 
            DEFAULT_SHOWDATE, 
            DEFAULT_SORT,
            DEFAULT_REGEX,
            DEFAULT_WRITER);
    }


    /**
     * Creates a tree with the given root directory and flag to show files.
     * 
     * @param rootDir Root directory.
     * @param showFiles If true, includes files (as opposed to just directories)
     *        in the output.
     * @param showSize If true, shows the size of the file.
     * @param sortBy File attribute to use for sorting.
     */
    public Tree2(
        File rootDir, 
        boolean showFiles, 
        boolean showSize, 
        String sortBy)
    {
        this(
            rootDir, 
            showFiles, 
            showSize, 
            DEFAULT_SHOWDATE, 
            sortBy, 
            DEFAULT_REGEX,
            DEFAULT_WRITER);
    }

    
    /**
     * Creates a tree with the given root directory and flag to show files.
     * 
     * @param rootDir Root directory.
     * @param showFiles If true, includes files (as opposed to just directories)
     *        in the output.
     * @param showSize If true, shows the size of the file.
     * @param showDate If true, shows the date/time of the file.
     * @param sortBy File attribute to use for sorting.
     */
    public Tree2(
        File rootDir, 
        boolean showFiles, 
        boolean showSize, 
        boolean showDate, 
        String sortBy,
        String regex)
    {
        this(
            rootDir, 
            showFiles, 
            showSize, 
            showDate, 
            sortBy,
            regex,
            DEFAULT_WRITER);
    }
   
    
    /**
     * Creates a tree with the given criteria.
     * 
     * @param rootDir Root directory of the tree.
     * @param showFiles Set to true if you want file info in the tree, false 
     *        otherwise.
     * @param showDate Set to true to print out the files timestamp.
     * @param showSize Set to true to print out the size of the file next to the
     *        filename.
     * @param sortBy Set to any of SORT_[NAME|SIZE|NONE] to specify sort order.
     * @param writer Output destination.
     * @throws IllegalArgumentException on invalid root dir.
     */
    public Tree2(File rootDir, 
                boolean showFiles, 
                boolean showSize,
                boolean showDate,
                String sortBy,
                String regex,
                Writer writer)
    {
        rootDir_ = rootDir;

        // Make sure directory is legit        
        if (!rootDir_.exists())
            throw new IllegalArgumentException(
                "Directory " + rootDir + " does not exist.");
                
        if (!rootDir_.isDirectory())
            throw new IllegalArgumentException(
                rootDir + " is not a directory.");

        if (!rootDir_.canRead())
            throw new IllegalArgumentException(
                "Cannot read from " + rootDir_);
        
        showSize_ = showSize;
        showDate_ = showDate;
        writer_ = new PrintWriter(writer, true);
        dirFilter_ = new DirectoryFilter();

        regex_ = regex;
        
        //
        // If a regex is passed and the user forgot to turn on the -files
        // flag, just turn it on automatically.
        //
        boolean useRegex = !StringUtil.isNullOrBlank(regex_);
        showFiles_ = useRegex | showDate_ | showSize_ ? true : showFiles;
        
        if (showFiles_)
            fileFilter_ = new FileFilter();
        
        if (useRegex)
        {
            fileFilter_ = 
                new AndFilter(
                    fileFilter_, 
                    new RegexFilter(regex_, false));  // TODO: expose case sensetivity?
        }

        sortByMap_ = new HashMap();
        sortByMap_.put(SORT_NONE, null);
        
        sortByMap_.put(SORT_NAME, 
            new FileComparator(FileComparator.COMPARE_NAME));
        
        sortByMap_.put(SORT_SIZE,
            new FileComparator(FileComparator.COMPARE_SIZE));
        
        sortByMap_.put(SORT_DATE,
            new FileComparator(FileComparator.COMPARE_DATE));
        
        sortBy_ = sortBy;
        
        if (!sortByMap_.containsKey(sortBy_))
            throw new IllegalArgumentException(
                "Sort by field '" + sortBy + "' is invalid.");
        
        formatter_ = DecimalFormat.getIntegerInstance();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Prints the tree.
     */    
    public void showTree()
    {
        showTree(rootDir_, "");
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Prints program usage.
     * 
     * @param options Command line options.
     */
    protected static void printUsage(JSAP jsap)
    {
        System.out.println(
            "Graphically displays the folder structure of a drive or path.");
            
        System.out.println();
        
        System.out.println("toolbox.tree.Tree " + jsap.getUsage());
            
        System.out.println();
        System.out.println(StringUtil.replace(jsap.getHelp(), "\n\n", "\n"));
    }
    
    
    /**
     * Recurses the directory structure of the given rootDir and generates a
     * hierarchical text representation.
     * 
     * @param rootDir Root directory.
     * @param level Current level of decorated indentation.
     * @return boolean True.
     */
    protected boolean showTree(File rootDir, String level)
    {
        boolean atRoot = (level.length() == 0);
        
        if (atRoot)
            writer_.println(rootDir.getAbsolutePath());
            
        // Get list of directories in root
        File[] dirs = rootDir.listFiles(dirFilter_);
        Arrays.sort(dirs, (Comparator) sortByMap_.get(sortBy_));

        String filler = (dirs.length == 0 ? SPACER : BAR);
        
        // Print files
        if (showFiles_)
        {
            File[] files = rootDir.listFiles(fileFilter_);
            Arrays.sort(files, (Comparator) sortByMap_.get(sortBy_));
            
            int longestName = -1; // Number of spaces occupied by longest fname 
            int largestFile = -1; // Number of spaces occupied by largest fsize
            long dirSize = 0;     // Running total of a directory's size
            
            for (int i = 0; i < files.length; i++)
            {
                writer_.print(level + filler + files[i].getName());
                
                if (showSize_)
                {
                    if (longestName == -1)
                        longestName = FileUtil
                            .getLongestFilename(files).getName().length();
                    
                    if (largestFile == -1)
                        largestFile = formatter_.format(
                            FileUtil.getLargestFile(files).length()).length();
                      
                    writer_.print(
                        StringUtil.repeat(" ", 
                            longestName - files[i].getName().length()));
        
                    String formatted = 
                        formatter_.format(files[i].length());
                              
                    writer_.print(" " + 
                        StringUtil.repeat(" ", largestFile - formatted.length())
                        + formatted);
            
                    // Accumulate directory size
                    dirSize += files[i].length();
                }

                if (showDate_)
                {
                    writer_.print("  ");
                    writer_.print(DateTimeUtil.formatToSecond(
                        new Date(files[i].lastModified())));
                }
            
                writer_.println();
            }
            
            
            // Print out the size of the directory
            if (dirSize > 0 && showSize_)
            {
                String total = formatter_.format(dirSize);
                int tlen = total.length();
                //String dashy = StringUtil.repeat("-", tlen);
                int alotted = longestName + largestFile + 1;
                //String header = StringUtil.repeat(" ", alotted - tlen); 
                
                //writer_.println(level + filler + header + dashy);
                //writer_.println(level + filler + 
                //          header.substring(1) + "." + total + ".");
                
                String s = files.length + " file(s) ";
                
                String gap =
                    StringUtil.repeat(" ", alotted - s.length() - tlen);
                
                writer_.println(level + filler + s + gap + total);
            }
            
            // Extra line after last file in a dir        
            if (dirs.length > 0)
                writer_.println(level + BAR);
        }
        
        // Bow out if nothing todo
        if (ArrayUtil.isNullOrEmpty(dirs))
        {
            if (atRoot)
                writer_.println("No subfolders exist");
            return false;
        }

        int len = dirs.length; 

        // Theres at least one child so go ahead and print a BAR
        if (atRoot)
            writer_.println(BAR);
            
        // Process each directory    
        for (int i = 0; i < len; i++)
        {
            File current = dirs[i];

            writer_.print(level);
            writer_.print(JUNCTION);
            writer_.print(ARM);
            writer_.print(current.getName());
            writer_.println();
            
            // Recurse            
            if (i == len - 1 && len > 1)  
            {
                // At end and more then one dir
                showTree(current, level + SPACER);
            }
            else if (len > 1) 
            {
                // More than one dir
                showTree(current, level + BAR);                
                writer_.println(level + BAR);                   
            }
            else  
            {
                // Not at end                
                showTree(current, level + SPACER);
            }
        }
        
        return true;
    }
        
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return AsMap.of(this).toString();
    }
}