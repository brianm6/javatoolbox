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
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import toolbox.util.ArrayUtil;
import toolbox.util.DateTimeUtil;
import toolbox.util.FileUtil;
import toolbox.util.StreamUtil;
import toolbox.util.StringUtil;
import toolbox.util.collections.AsMap;
import toolbox.util.file.FileComparator;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.FileFilter;

/**
 * Generates a graphical representation of a directory structure using ascii
 * characters. 
 * <p>
 * The listing per directory can include and be sorted on any of the following
 * file attributes.
 *
 * <ul>
 *   <li>File name
 *   <li>File size
 *   <li>File date/time
 * </ul>
 * <p>
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
public class Tree
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

    /**
     * Do not sort.
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
    // Default Constants
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
     * Formatter for file sizes, etc.
     */
    private NumberFormat formatter_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Launcher for tree.
     *
     * @param args  [-f, -s -os, rootDir]
     * @throws Exception on error
     */
    public static void main(String args[]) throws Exception
    {
        // command line options and arguments
        String rootDir = null;
        boolean showFiles = false;
        boolean showSize = false;
        boolean showDate = false;
        String sortBy = DEFAULT_SORT;
                
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        
        Option fileOption = new Option(
            "f", "files", false, "Includes files in the output");
            
        Option sizeOption = new Option(
            "s", "size", false, "Includes file sizes in the output");
       
		Option dateOption = new Option(
			"d", "date/time", false, "Includes file date/time in the output");
        
        Option sortOption = new Option("o", "sort", true, "Sort order");
        sortOption.setArgs(1);
        sortOption.setArgName("sort by [n=name|s=size]");
        //sortOption.setOptionalArg(true);
        //sortOption.setRequired(false);
        //sortOption.setValueSeparator('=');
        
        Option helpOption = new Option("h", "help", false, "Prints usage");
        Option helpOption2 = new Option("?", "?", false, "Prints usage");
        
        options.addOption(helpOption2);
        options.addOption(helpOption);
        options.addOption(fileOption);
        options.addOption(sizeOption);
        options.addOption(dateOption);
        options.addOption(sortOption);
    
        CommandLine cmdLine = parser.parse(options, args, true);
    
        for (Iterator i = cmdLine.iterator(); i.hasNext(); )
        {
            Option option = (Option) i.next();
            String opt = option.getOpt();
            
            if (opt.equals(fileOption.getOpt()))
            {
                showFiles = true;
            }
            else if (opt.equals(sizeOption.getOpt()))
            {
                showSize = true;
            }
			else if (opt.equals(dateOption.getOpt()))
			{
				showDate = true;
			}
			else if (opt.equals(sortOption.getOpt()))
			{
				sortBy = sortOption.getValue(DEFAULT_SORT);
			}
            else if (opt.equals(helpOption.getOpt())  ||
                     opt.equals(helpOption2.getOpt()))
            {
                printUsage(options);
                return;
            }
            else
            {
                throw new IllegalArgumentException(
                    "Option " + opt + " not understood.");
            }
        }
        
        // Root directory argument        
        switch (cmdLine.getArgs().length)
        {
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
        try
        {
            if (rootDir != null)
            {
                Tree t = new Tree(new File(rootDir), 
                                  showFiles, 
                                  showSize,
                        		  showDate,
                                  sortBy);
                t.showTree();
                StreamUtil.close(t.writer_);
            }
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("ERROR: " + e.getMessage());
            printUsage(options);
        }
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Tree that will show files and send the output to System.out
     * with the given root directory.
     * 
     * @param rootDir Root directory
     */
    public Tree(File rootDir)
    {
        this(rootDir, DEFAULT_SHOWFILES, DEFAULT_SHOWSIZE, DEFAULT_SHOWDATE,
             DEFAULT_SORT, DEFAULT_WRITER);
    }


    /**
     * Creates a tree that will show files with the given root directory and
     * send output to the given writer.
     * 
     * @param rootDir Root directory
     * @param writer Output destination    
     */
    public Tree(File rootDir, Writer writer)
    {
        this(rootDir, DEFAULT_SHOWFILES, DEFAULT_SHOWSIZE, DEFAULT_SHOWDATE, 
             DEFAULT_SORT, writer);    
    }


    /**
     * Creates a tree with the given root directory and flag to show files.
     * 
     * @param rootDir Root directory
     * @param showFiles Set to true if you want file info in the tree, false 
     *        otherwise
     */
    public Tree(File rootDir, boolean showFiles)
    {
        this(rootDir, showFiles, DEFAULT_SHOWSIZE, DEFAULT_SHOWDATE, 
             DEFAULT_SORT, DEFAULT_WRITER);
    }


    /**
     * Creates a tree with the given root directory and flag to show files.
     * 
     * @param rootDir Root directory
     * @param showFiles If true, includes files (as opposed to just directories)
     *        in the output 
     * @param showSize If true, shows the size of the file
     */
    public Tree(File rootDir, boolean showFiles, boolean showSize)
    {
        this(rootDir, showFiles, showSize, DEFAULT_SHOWDATE, DEFAULT_SORT, 
             DEFAULT_WRITER);
    }


    /**
     * Creates a tree with the given root directory and flag to show files.
     * 
     * @param rootDir Root directory
     * @param showFiles If true, includes files (as opposed to just directories)
     *        in the output 
     * @param showSize If true, shows the size of the file
     * @param sortBy File attribute to use for sorting
     */
    public Tree(File rootDir, boolean showFiles, boolean showSize, String sortBy)
    {
        this(rootDir, showFiles, showSize, DEFAULT_SHOWDATE, sortBy, 
             DEFAULT_WRITER);
    }

    
	/**
	 * Creates a tree with the given root directory and flag to show files.
	 * 
	 * @param rootDir Root directory
	 * @param showFiles If true, includes files (as opposed to just directories)
	 *        in the output 
	 * @param showSize If true, shows the size of the file
	 * @param showDate If true, shows the date/time of the file
	 * @param sortBy File attribute to use for sorting
	 */
	public Tree(File rootDir, boolean showFiles, boolean showSize, 
				boolean showDate, String sortBy)
	{
		this(rootDir, showFiles, showSize, showDate, sortBy, DEFAULT_WRITER);
	}
   
	
    /**
     * Creates a tree with the given criteria.
     * 
     * @param rootDir Root directory of the tree
     * @param showFiles Set to true if you want file info in the tree, false 
     *        otherwise
     * @param showSize Set to true to print out the size of the file next to the
     *        filename
     * @param sortBy Set to any of SORT_[NAME|SIZE|NONE] to specify sort order.
     * @param writer Output destination
     */
    public Tree(File rootDir, 
                boolean showFiles, 
                boolean showSize,
				boolean showDate,
                String sortBy,
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
        
        showFiles_ = showFiles;
        showSize_ = showSize;
        showDate_ = showDate;
        writer_ = new PrintWriter(writer, true);
        dirFilter_ = new DirectoryFilter();
        
        if (showFiles_)
            fileFilter_ = new FileFilter();

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
     * @param options Command line options
     */
    protected static void printUsage(Options options)
    {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp(
            "tree [options] <directory>",
            "Shows directory structure in a tree hierarchy.",
            options,
            "");
    }
    
    
    /**
     * Recurses the directory structure of the given rootDir and generates
     * a hierarchical text representation.
     * 
     * @param rootDir Root diretory
     * @param level Current level of decorated indentation
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
                //writer_.println(level + filler + header.substring(1) + "." + total + ".");
                
                String s = files.length + " file(s) ";
                String gap = StringUtil.repeat(" ", alotted - s.length() - tlen);
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
        for (int i=0; i<len; i++)
        {
            File current = dirs[i];

            writer_.print(level);
            writer_.print(JUNCTION);
            writer_.print(ARM);
            writer_.print(current.getName());
            writer_.println();
            
            // Recurse            
            if (i == len-1 && len > 1)  
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