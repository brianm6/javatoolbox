package toolbox.tree;

import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.FileFilter;

/**
 * Generates a text representation of a directory tree with the option to
 * include files.
 * <br>
 * Example:
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
     * Spaces indentation per tree branch 
     */
    private static final String SPACER = "    ";
    
    /** 
     * Tree branch with a continuation 
     */
    private static final String BAR = "|   ";
    
    /** 
     * Junction in the tree 
     */
    private static final String JUNCTION = "+";
    
    /** 
     * Tree arm 
     */
    private static final String ARM = "---";
    
    /** 
     * Files are not shown by default 
     */
    private static final boolean DEFAULT_SHOWFILES = false;

    /** 
     * File sizes are not shown by default 
     */
    private static final boolean DEFAULT_SHOWSIZE = false;
    
    /** 
     * Output is sent to System.out by default 
     */
    private static final Writer DEFAULT_WRITER = 
        new OutputStreamWriter(System.out);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Output writer 
     */    
    private PrintWriter writer_;
    
    /** 
     * Filter to identify directories 
     */
    private FilenameFilter dirFilter_;
    
    /** 
     * Filter to identify files 
     */
    private FilenameFilter fileFilter_;
    
    /** 
     * Flag that controls the showing of files 
     */
    private boolean showFiles_;

    /** 
     * Flag that controls the showing of file size 
     */
    private boolean showSize_;
    
    /** 
     * Root directory of the tree 
     */
    private File rootDir_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint
     *
     * @param args  [-f, rootDir]
     * @throws Exception on error
     */
    public static void main(String args[]) throws Exception
    {
        // command line options and arguments
        String rootDir = null;
        boolean showFiles = false;
        boolean showSize = false;
                
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        
        Option fileOption = new Option(
            "f", "files", false, "Includes files in the output");
            
        Option sizeOption = new Option(
            "s", "size", false, "Includes file sizes in the output");
            
        Option helpOption = new Option("h", "help", false, "Prints usage");
        Option helpOption2 = new Option("?", "?", false, "Prints usage");
        
        options.addOption(helpOption2);
        options.addOption(helpOption);
        options.addOption(fileOption);
        options.addOption(sizeOption);        
    
        CommandLine cmdLine = parser.parse(options, args, true);
    
        for (Iterator i = cmdLine.iterator(); i.hasNext(); )
        {
            Option option = (Option) i.next();
            String opt = option.getOpt();
            
            if (opt.equals(fileOption.getOpt()))
            {
                showFiles = true;
            }
            if (opt.equals(sizeOption.getOpt()))
            {
                showSize = true;
            }
            else if (opt.equals(helpOption.getOpt())  ||
                     opt.equals(helpOption2.getOpt()))
            {
                printUsage(options);
                return;
            }
        }
        
        // Root directory argument        
        switch (cmdLine.getArgs().length)
        {
            case 0  :  rootDir = System.getProperty("user.dir"); break;
            case 1  :  rootDir = cmdLine.getArgs()[0]; break;
            default :  System.err.println("ERROR: Invalid arguments");
                       printUsage(options); 
                       return;
        }
        
        // Create us a tree and let it ride..
        try
        {
            if (rootDir != null)
            {
                Tree t = new Tree(new File(rootDir), showFiles, showSize);
                t.showTree();
            }
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("ERROR: " + e.getMessage());
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
        this(rootDir, DEFAULT_SHOWFILES, DEFAULT_SHOWSIZE, DEFAULT_WRITER);
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
        this(rootDir, DEFAULT_SHOWFILES, DEFAULT_SHOWSIZE, writer);    
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
        this(rootDir, showFiles, DEFAULT_SHOWSIZE, DEFAULT_WRITER);
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
        this(rootDir, showFiles,showSize, DEFAULT_WRITER);
    }

    /**
     * Creates a tree with the given criteria.
     * 
     * @param rootDir Root directory of the tree
     * @param showFiles Set to true if you want file info in the tree, false 
     *        otherwise
     * @param showSize Set to true to print out the size of the file next to the
     *        filename
     * @param writer Output destination
     */
    public Tree(File rootDir, boolean showFiles, boolean showSize,Writer writer)
    {
        rootDir_ = rootDir;

        // Make sure directory is legit        
        if (!rootDir_.exists())
            throw new IllegalArgumentException("Directory " + rootDir + 
                " does not exist.");
                
        if (!rootDir_.isDirectory())
            throw new IllegalArgumentException(rootDir + 
                " is not a directory.");

        if (!rootDir_.canRead())
            throw new IllegalArgumentException("Cannot read from " + 
                rootDir_);
        
        showFiles_ = showFiles;
        showSize_ = showSize;
        writer_ = new PrintWriter(writer, true);
        dirFilter_ = new DirectoryFilter();
        
        if (showFiles_)
            fileFilter_ = new FileFilter();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Prints the tree
     */    
    public void showTree()
    {
        showTree(rootDir_, "");
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Prints program usage
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

        // Print files
        if (showFiles_)
        {
            File[] files = rootDir.listFiles(fileFilter_);
            int longestName = -1;
            int largestFile = -1;
            
            for (int i = 0; i < files.length; i++)
            {
                String filler = (dirs.length == 0 ? SPACER : BAR);
                writer_.print(level + filler + files[i].getName());
                
                if (showSize_)
                {
                    if (longestName == -1)
                        longestName = FileUtil
                            .getLongestFilename(files).getName().length();
                    
                    if (largestFile == -1)
                        largestFile = DecimalFormat.getIntegerInstance().format(
                            FileUtil.getLargestFile(files).length()).length();
                      
                    writer_.print(
                        StringUtil.repeat(" ", 
                            longestName - files[i].getName().length()));
        
                    String formatted = 
                        DecimalFormat.getIntegerInstance().format(
                            files[i].length());
                              
                    writer_.print(" " + 
                        StringUtil.repeat(" ", largestFile - formatted.length())
                        + formatted);
                }
                
                writer_.println();
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
    
}