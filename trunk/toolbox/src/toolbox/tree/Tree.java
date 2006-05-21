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
import java.util.List;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.lang.StringUtils;

import toolbox.util.ArrayUtil;
import toolbox.util.DateTimeUtil;
import toolbox.util.FileUtil;
import toolbox.util.collections.AsMap;
import toolbox.util.io.filter.RegexFileFilter;

/**
 * Generates a tree like graphical representation of a directory structure using 
 * ascii characters. 
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
 * For filesystems that support symbolic links, links are traversed only once in
 * the case of circular references.
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
    
    /**
     * Decorator for the writer passed in from treeConfig.
     */
    private PrintWriter out;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    public Tree(File rootDir) {
        this(rootDir, new TreeConfig());
    }
    
    /**
     * Creates a tree with the given configuration.
     * 
     * @param rootDir Root directory of the tree.
     * @param treeConfig Configuration options.
     * @throws IllegalArgumentException on invalid root dir.
     */
    public Tree(File rootDir, TreeConfig treeConfig) {
        
        //System.out.println(StringUtil.banner(treeConfig.toString()));
        
    	this.traversed = new ArrayList();
        this.rootDir = rootDir;
        this.config = treeConfig;
        this.out = new PrintWriter(config.getOutputWriter(), true);
        
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
    // Private
    //--------------------------------------------------------------------------
    
    private boolean hasTraversed(File dir) {
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
    
    private boolean reachedMaxLevel(String levelString) {
        int currentLevel = levelString.length() / SPACER.length();
        return currentLevel >= config.getMaxLevels();
    }
    
    /**
     * Recurses the directory structure of the given rootDir and generates a
     * hierarchical text representation.
     * 
     * @param dir Root directory.
     * @param level Current level of decorated indentation.
     */
    protected void showTree(File dir, String level) {
//    	if (hasTraversed(rootDir)) {
//    		writer_.println(level + SPACER + "[LINK] " + getCanonicalPath(rootDir));
//    		return false;
//    	}
//    	else {
//    		
//    	}

        boolean atRoot = (level.length() == 0);
        boolean reachedMaxLevel = reachedMaxLevel(level);
        
        if (atRoot)
            out.println(dir.getAbsolutePath());
            
        // Get list of directories in root
        File[] dirs = dir.listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
        
        // If there are permission problems, null is returned
        if (dirs == null) {
        	return; 
        }
        
        Arrays.sort(dirs, (Comparator) config.getSortBy());
        String filler = (dirs.length == 0 || reachedMaxLevel ? SPACER : BAR);
        
        // Print files
        if (config.isShowFiles()) {
            File[] files = dir.listFiles( (FileFilter) fileFilter);
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
                int alotted = longestName + largestFile + 1;
                String s = files.length + " file(s) ";
                String gap = StringUtils.repeat(" ", alotted - s.length() - tlen);
                out.println(level + filler + s + gap + total);
            }
            
            // Extra line after last file in a dir        
            if (dirs.length > 0 && !reachedMaxLevel)
                out.println(level + BAR);
        }

        if (reachedMaxLevel)
            return;
        
        // No sub dirs == nothing to do
        if (ArrayUtil.isNullOrEmpty(dirs)) {
            if (atRoot)
                out.println("No subfolders exist");
            return;
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