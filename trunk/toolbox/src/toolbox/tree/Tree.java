package toolbox.tree;

import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import toolbox.util.ArrayUtil;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.FileFilter;

/**
 * Generates a text representation of a directory tree with the option to
 * include files. Example:
 * <pre>
 *
 *   apache
 *   |
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
    private static final boolean DEFAULT_SHOWFILES = false;
    
    private static final Writer  DEFAULT_WRITER = 
        new OutputStreamWriter(System.out);
        
    private static final File DEFAULT_ROOT_DIR = 
        new File(System.getProperty("user.dir"));
    
    private PrintWriter writer_;
    private FilenameFilter dirFilter_;
    private FilenameFilter fileFilter_;
    private boolean showFiles_;
    private File rootDir_;
        
    private static final String SPACER   = "    ";
    private static final String BAR      = "|   ";
    private static final String JUNCTION = "+";
    private static final String ARM      = "---";

    
    /**
     * Entrypoint
     *
     * @param   args  [-f, rootDir]
     * @throws  Exception on error
     */
    public static void main(String args[]) throws Exception
    {
        // command line options and arguments
        String rootDir = null;
        boolean showFiles = false;
                
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        Option  fileOption = new Option("f", "files", false, "List files");
        Option  helpOption = new Option("h", "help", false, "Print usage");
        Option  helpOption2 = new Option("?", "/?", false, "Print Usage");
        
        options.addOption(helpOption2);
        options.addOption(helpOption);
        options.addOption(fileOption);        

        CommandLine cmdLine = parser.parse(options, args, true);
    
        for (Iterator i = cmdLine.iterator(); i.hasNext(); )
        {
            Option option = (Option) i.next();
            String opt = option.getOpt();
            
            if (opt.equals(fileOption.getOpt()))
            {
                showFiles = true;
            }
            else if (opt.equals(helpOption.getOpt())  ||
                     opt.equals(helpOption2.getOpt()))
            {
                printUsage();
            }
        }
        
        // Root directory argument        
        switch (cmdLine.getArgs().length)
        {
            case 0  :  rootDir = System.getProperty("user.dir"); break;
            case 1  :  rootDir = cmdLine.getArgs()[0]; break;
            default :  System.err.println("ERROR: Invalid arguments");
                       printUsage(); 
                       break;
        }
        
        // Create us a tree and let it ride..
        try
        {
            if (rootDir != null)
            {
                Tree t = new Tree(new File(rootDir), showFiles);
                t.showTree();
            }
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a tree with the given root directory
     * 
     * @param  rootDir   Root directory
     */
    public Tree(File rootDir)
    {
        this(rootDir, DEFAULT_SHOWFILES, DEFAULT_WRITER);
    }
    

    /**
     * Creates a tree with the given root directory and output
     * 
     * @param  rootDir  Root directory
     * @param  writer   Output destination    
     */
    public Tree(File rootDir, Writer writer)
    {
        this(rootDir, DEFAULT_SHOWFILES, writer);    
    }


    /**
     * Creates a tree with the given root directory and flag to show files
     * 
     * @param  rootDir    Root directory
     * @param  showFiles  Set to true if you want file info in the tree,
     *                    false otherwise
     */
    public Tree(File rootDir, boolean showFiles)
    {
        this(rootDir, showFiles, DEFAULT_WRITER);
    }

    /**
     * Creates a tree with the given criteria
     * 
     * @param  rootDir    Root directory of the tree
     * @param  showFiles  Set to true if you want file info in the tree,
     *                    false otherwise
     * @param  writer     Output destination
     */
    public Tree(File rootDir, boolean showFiles, Writer writer)
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
        writer_ = new PrintWriter(writer, true);
        dirFilter_ = new DirectoryFilter();
        
        if (showFiles_)
            fileFilter_ = new FileFilter();
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Prints the tree
     */    
    public void showTree()
    {
        showTree(rootDir_, "");
    }
    
    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    /**
     * Prints program usage
     */
    protected static void printUsage()
    {
        System.out.println("Tree shows a directory structure.");
        System.out.println("Usage    : tree [-f] <dir>");
        System.out.println("Options  : -f  => include files");
    }
    
    /**
     * Recurses the directory structure of the given rootDir and generates
     * a hierarchical text representation.
     * 
     * @param  rootDir   Root diretory
     * @param  level     Current level of decorated indentation
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
            
            for (int i = 0; i < files.length; i++)
            {
                String filler = (dirs.length == 0 ? SPACER : BAR);
                writer_.println(level + filler + files[i].getName());
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

        // we know theres at least one child so go ahead and print a BAR
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