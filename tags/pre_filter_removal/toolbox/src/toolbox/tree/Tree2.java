package toolbox.tree;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.martiansoftware.jsap.stringparsers.IntegerStringParser;
import com.martiansoftware.jsap.stringparsers.StringStringParser;

import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

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
    // Tree Ascii Constants
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
    // Sort Option Constants
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
    // Defaults Constants
    //--------------------------------------------------------------------------

    /** 
     * Files are not shown by default. 
     */
    private static final boolean DEFAULT_SHOWFILES = false;

    /**
     * Full paths are not shown by default.
     */
    private static final boolean DEFAULT_SHOWFULLPATH = false;
    
    /** 
     * File sizes are not shown by default. 
     */
    private static final boolean DEFAULT_SHOWSIZE = false;

    /** 
     * File date/times are not shown by default. 
     */
    private static final boolean DEFAULT_SHOWDATE = false;

    /**
     * Help is turned off by default.
     */
    private static final boolean DEFAULT_SHOWHELP = false;
    
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
    private static final String DEFAULT_REGEX = "";
    
    /**
     * Default depth is Integer.MAX_VALUE.
     */
    private static final int DEFAULT_MAXDEPTH = Integer.MAX_VALUE;

    /**
     * Default starting directory is the users current directory.
     */
    private static final String DEFAULT_DIR = System.getProperty("user.dir");

    //--------------------------------------------------------------------------
    // Command Line Constants
    //--------------------------------------------------------------------------
    
    private static final String SWITCH_FILES    = "files";
    private static final String SWITCH_DATE     = "date";
    private static final String SWITCH_HELP     = "help";
    private static final String SWITCH_SIZE     = "size";
    private static final String SWITCH_FULLPATH = "fullpath";
    
    private static final String OPTION_REGEX    = "regex";
    private static final String OPTION_SORT     = "sort";
    private static final String OPTION_DIR      = "dir";
    private static final String OPTION_MAXDEPTH = "maxDepth";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Output writer. 
     */    
    private PrintWriter writer_;
    
    /** 
     * Filter to identify files. 
     */
    private IOFileFilter fileFilter_;
    
    /** 
     * Flag to toggle the showing of files. 
     */
    private boolean showFiles_;

    /**
     * Flag to toggle the showing of full paths for files and directories.
     */
    private boolean showFullPath_;

    /** 
     * Flag to toggle the showing of a file's size. 
     */
    private boolean showSize_;
    
    /** 
     * Flag to toggle the showing of a file's timestamp. 
     */
    private boolean showDate_;

    /**
     * Maximum depth to recurse into the directory structure.
     */
    private int maxDepth_;
    
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
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Launcher for tree.
     *
     * @param args  [-f, -s -os, -m, -p rootDir]
     * @throws Exception on error.
     */
    public static void main(String args[]) throws Exception
    {
        JSAP jsap = new JSAP();
        
        Switch filesSwitch = 
            new Switch(SWITCH_FILES)
                .setShortFlag('f')
                //.setLongFlag("files")
                .setDefault(DEFAULT_SHOWFILES + "");

        Switch fullPathSwitch = 
            new Switch(SWITCH_FULLPATH)
                .setShortFlag('p')
                //.setLongFlag("fullpath")
                .setDefault(DEFAULT_SHOWFULLPATH + "");
        
        Switch sizeSwitch = 
            new Switch(SWITCH_SIZE)
                .setShortFlag('s')
                //.setLongFlag("size")
                .setDefault(DEFAULT_SHOWSIZE + "");
        
        Switch dateSwitch = 
            new Switch(SWITCH_DATE)
                .setShortFlag('d')
                //.setLongFlag("date")
                .setDefault(DEFAULT_SHOWDATE + "");

        Switch helpSwitch = 
            new Switch(SWITCH_HELP)
                .setShortFlag('h')
                //.setLongFlag("help")
                .setDefault(DEFAULT_SHOWHELP + "");

        FlaggedOption regexOption = 
            new FlaggedOption(OPTION_REGEX)
                .setStringParser(new StringStringParser())
                .setRequired(false) 
                .setShortFlag('r') 
                //.setLongFlag("regex")
                .setDefault(DEFAULT_REGEX);
        
        FlaggedOption sortOption = 
            new FlaggedOption(OPTION_SORT)
                .setStringParser(
                    new EnumeratedStringParser(
                        SORT_NAME + ";" + 
                        SORT_DATE + ";" + 
                        SORT_SIZE))
                .setRequired(false) 
                .setShortFlag('o'); 
                //.setLongFlag("sort")
                //.setDefault(SORT_NONE);

        FlaggedOption maxDepthOption = 
            new FlaggedOption(OPTION_MAXDEPTH)
                .setStringParser(new IntegerStringParser())
                .setRequired(false) 
                .setShortFlag('m') 
                //.setLongFlag("regex")
                .setDefault(DEFAULT_MAXDEPTH+"");
        
        UnflaggedOption dirOption =
            new UnflaggedOption(OPTION_DIR)
                .setRequired(false)
                .setStringParser(new StringStringParser())
                .setDefault(DEFAULT_DIR);
                
        // Help text
        filesSwitch.setHelp("Includes file names in the output.");
        fullPathSwitch.setHelp("Displays the full path name for files and directories.");
        sizeSwitch.setHelp("Includes file size in the output.");
        dateSwitch.setHelp("Includes file date and time in the output.");
        helpSwitch.setHelp("Prints tree usage with examples.");
        regexOption.setHelp("Filters files matching a regular expression.");
        sortOption.setHelp("Sorts files by {f = filename, d = date, s = size}.");
        dirOption.setHelp("Root directory of the tree.");
        maxDepthOption.setHelp("Max number of directories to recurse into starting at 1.");
        
        jsap.registerParameter(filesSwitch);
        jsap.registerParameter(fullPathSwitch);
        jsap.registerParameter(sizeSwitch);
        jsap.registerParameter(dateSwitch);
        jsap.registerParameter(maxDepthOption);
        jsap.registerParameter(helpSwitch);
        jsap.registerParameter(regexOption);
        jsap.registerParameter(sortOption);
        jsap.registerParameter(dirOption);
        
        JSAPResult config = jsap.parse(args);   

        if (!config.success() || config.getBoolean(SWITCH_HELP))
        {
            for (Iterator i = config.getErrorMessageIterator(); i.hasNext(); )
                System.err.println(i.next());
            
            printUsage(jsap);
            return;
        }
        
        // Create us a tree and let it ride..
        try
        {
            if (config.getString(OPTION_DIR) != null)
            {
                Tree2 tree = new Tree2(
                    new File(config.getString(OPTION_DIR)), 
                    config.getBoolean(SWITCH_FILES), 
                    config.getBoolean(SWITCH_SIZE),
                    config.getBoolean(SWITCH_DATE),
                    config.getBoolean(SWITCH_FULLPATH),
                    config.getString(OPTION_SORT),
                    config.getInt(OPTION_MAXDEPTH), 
                    config.getString(OPTION_REGEX));
                
                tree.showTree();
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
        this(rootDir, DEFAULT_WRITER);
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
            DEFAULT_SHOWFULLPATH,
            DEFAULT_SORT, 
            DEFAULT_REGEX,
            DEFAULT_MAXDEPTH,
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
        this(rootDir, showFiles, DEFAULT_SHOWSIZE); 
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
        this(rootDir, showFiles, showSize, DEFAULT_SORT);
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
            DEFAULT_SHOWFULLPATH,
            sortBy,
            DEFAULT_MAXDEPTH,
            DEFAULT_REGEX);
    }

    
    /**
     * Creates a tree with the given root directory and flag to show files.
     * 
     * @param rootDir Root directory.
     * @param showFiles If true, includes files (as opposed to just directories)
     *        in the output.
     * @param showSize If true, shows the size of the file.
     * @param showDate If true, shows the date/time of the file.
     * @param showFullPath If true, shows full pathnames for files and dirs.
     * @param sortBy File attribute to use for sorting.
     * @param maxDepth Max number of directories to recurse into.
     * @param regex File filter expressed as a regular expression. 
     */
    public Tree2(
        File rootDir, 
        boolean showFiles, 
        boolean showSize, 
        boolean showDate, 
        boolean showFullPath, 
        String sortBy,
        int maxDepth,
        String regex)
    {
        this(
            rootDir, 
            showFiles, 
            showSize, 
            showDate,
            showFullPath,
            sortBy,
            regex,
            maxDepth,
            DEFAULT_WRITER);
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
     * @param showFullPath Set to true to show full path names.
     * @param sortBy Set to any of SORT_[NAME|SIZE|NONE] to specify sort order.
     * @param regex File filter expressed as a regular expression. 
     * @param writer Output destination.
     * @throws IllegalArgumentException on invalid root dir.
     */
    public Tree2(File rootDir, 
                boolean showFiles, 
                boolean showSize,
                boolean showDate,
                boolean showFullPath,
                String sortBy,
                String regex,
                int maxDepth,
                Writer writer)
    {
        rootDir_ = rootDir;

        // Make sure directory is legit        
        Validate.isTrue(
            rootDir_.exists(),
            "Directory " + rootDir + " does not exist.");
                
        Validate.isTrue(
            rootDir_.isDirectory(), 
            rootDir + " is not a directory.");

        Validate.isTrue(
            rootDir_.canRead(),
            "Cannot read from directory " + rootDir_);
        
        Validate.isTrue(
            maxDepth >= 1,
            "Max depth must be greater than or equal to one.");
        
        showFullPath_ = showFullPath;
        showSize_     = showSize;
        showDate_     = showDate;
        writer_       = new PrintWriter(writer, true);  // turn on autoflush
        regex_        = regex;
        maxDepth_     = maxDepth;
        
        //
        // If a regex is passed and the user forgot to turn on the -files
        // flag, just turn it on automatically.
        //
        boolean useRegex = !StringUtils.isBlank(regex_);
        showFiles_ = useRegex | showDate_ | showSize_ ? true : showFiles;
        
        if (showFiles_)
            fileFilter_ = new NotFileFilter(DirectoryFileFilter.INSTANCE);
        
        // TODO: expose case sensetivity?
        if (useRegex)
            fileFilter_ = new AndFileFilter(
                fileFilter_, 
                new RegexFileFilter(regex_, false));  

        sortByMap_ = new HashMap();
        sortByMap_.put(SORT_NONE, new NullComparator());
        sortByMap_.put(SORT_NAME, FileComparator.COMPARE_NAME);
        sortByMap_.put(SORT_SIZE, FileComparator.COMPARE_SIZE);
        sortByMap_.put(SORT_DATE, FileComparator.COMPARE_DATE);
        sortBy_ = sortBy == null ? DEFAULT_SORT : sortBy;
        
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
     * @param jsap Java simple argument parser.
     */
    protected static void printUsage(JSAP jsap)
    {
        System.out.println(
            "Graphically displays the folder structure of a drive or path.");
            
        System.out.println();
        
        System.out.println("toolbox.tree.Tree " + jsap.getUsage());
            
        System.out.println();
        System.out.println(StringUtils.replace(jsap.getHelp(), "\n\n", "\n"));
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
        if (getCurrentDepth(level) > maxDepth_)
        	return true;
        
        boolean atRoot = (level.length() == 0);
        
        if (atRoot)
            writer_.println(rootDir.getAbsolutePath());
            
        // Get list of directories in root
        File[] dirs = rootDir.listFiles(
            (FileFilter) DirectoryFileFilter.INSTANCE);
        
        if (dirs == null)
            dirs = new File[0];
        
        Arrays.sort(dirs, (Comparator) sortByMap_.get(sortBy_));
        String filler = (dirs.length == 0 ? SPACER : BAR);
        
        // Print files
        if (showFiles_)
        {
            File[] files = rootDir.listFiles( (FileFilter) fileFilter_);
            Arrays.sort(files, (Comparator) sortByMap_.get(sortBy_));
            
            // Number of spaces occupied by longest filename. A value of -1 
            // means that it hasn't been lazily computed yet.
            int longestName = -1;  
            
            // Number of spaces occupied by largest file size. A value of -1
            // means that it hasn't been lazily computed yet.
            int largestFile = -1; 
            
            // Running total size of all the files in a directory.
            long dirSize = 0;     
            
            for (int i = 0; i < files.length; i++)
            {
                writer_.print(level + filler + formatFilename(files[i])); 
                
                if (showSize_)
                {
                    if (longestName == -1)
                        longestName = FileUtil
                            .getLongestFilename(files).getName().length();
                    
                    if (largestFile == -1)
                        largestFile = formatter_.format(
                            FileUtil.getLargestFile(files).length()).length();
                      
                    writer_.print(
                        StringUtils.repeat(" ", 
                            longestName - files[i].getName().length()));
        
                    String formatted = 
                        formatter_.format(files[i].length());
                              
                    writer_.print(" " + 
                        StringUtils.repeat(" ", largestFile - formatted.length())
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
                //String dashy = StringUtils.repeat("-", tlen);
                int alotted = longestName + largestFile + 1;
                //String header = StringUtils.repeat(" ", alotted - tlen); 
                
                //writer_.println(level + filler + header + dashy);
                //writer_.println(level + filler + 
                //          header.substring(1) + "." + total + ".");
                
                String s = files.length + " file(s) ";
                
                String gap =
                    StringUtils.repeat(" ", alotted - s.length() - tlen);
                
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
            writer_.print(formatFilename(current));
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
    
    
    /**
     * Formats the file name based on the showFullPath flag.
     * 
	 * @param current File or directory to format.
	 * @return String
	 */
	protected String formatFilename(File current) 
    {
		return showFullPath_ ? current.getAbsolutePath() : current.getName();
	}

    
	/**
     * Returns the current depth of the tree.
     * 
     * @param level String used for tree indentation.
     * @return int
     */
    protected int getCurrentDepth(String level) 
    {
    	return (level.length() / SPACER.length()) + 1;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Dumps state of the tree to a string.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return AsMap.of(this).toString();
    }
}