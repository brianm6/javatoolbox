package toolbox.tree;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;

import toolbox.util.collections.AsMap;
import toolbox.util.file.FileComparator;

/**
 * Javabean that contains all the configuration options supported by 
 * {@link toolbox.tree.Tree}.
 */
public class TreeConfig {

    // -------------------------------------------------------------------------
    // Constant DirNameRenderers
    // -------------------------------------------------------------------------
    
    public static final IDirNameRenderer DIR_NAME_RENDERER_NAME_ONLY = 
        new NameOnlyDirNameRenderer();
    
    public static final IDirNameRenderer DIR_NAME_RENDERER_ABSOLUTE =
        new AbsoluteDirNameRenderer();
    
    public static final IDirNameRenderer DIR_NAME_RENDERER_RELATIVE =
        new RelativeDirNameRenderer();
    
    //--------------------------------------------------------------------------
    // Constants : Defaults
    //--------------------------------------------------------------------------

    /** 
     * Files are not shown by default. 
     */
    public static final boolean DEFAULT_SHOW_FILES = false;

    /** 
     * File sizes are not shown by default. 
     */
    public static final boolean DEFAULT_SHOW_FILESIZE = false;

    /** 
     * File date/times are not shown by default. 
     */
    public static final boolean DEFAULT_SHOW_FILEDATE = false;

    /** 
     * Default is not to show only the directory names in the tree. 
     */
    public static final IDirNameRenderer DEFAULT_DIR_NAME_RENDERER = 
        DIR_NAME_RENDERER_NAME_ONLY;
    
    /** 
     * Default output goes to System.out. 
     */
    public static final Writer DEFAULT_OUTPUT_WRITER = 
        new OutputStreamWriter(System.out);

    /**
     * Default sort order is none.
     */
    public static final FileComparator DEFAULT_SORTBY = null;

    /**
     * Default regular expression is to match all.
     */
    public static final String DEFAULT_REGEX = null;

    /**
     * Default maximum depth of the tree.
     */
    public static final int DEFAULT_MAX_DEPTH = Integer.MAX_VALUE;
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    /** 
     * Flag to toggle the showing of files. 
     */
    private boolean showFiles; 

    /** 
     * Flag to toggle the showing of a file's size. 
     */
    private boolean showFilesize;
    
    /** 
     * Flag to toggle the showing of a file's timestamp. 
     */
    private boolean showFileDate;
    
    /** 
     * Renderer for the name of the directory in the tree. 
     */
    private IDirNameRenderer dirNameRenderer;
    
    /**
     * Specifies the sort order. Only valid if showFiles is true.
     */
    private FileComparator sortBy;
    
    /**
     * Regular expression for filtering files.
     */
    private String regexFilter;
    
    /**
     * Output writer.
     */
    private Writer outputWriter;
    
    /**
     * Maximum depth of the tree.
     */
    private int maxDepth;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public TreeConfig() {
        setShowFiles(DEFAULT_SHOW_FILES);
        setShowFilesize(DEFAULT_SHOW_FILESIZE);
        setShowFileDate(DEFAULT_SHOW_FILEDATE);
        setDirNameRenderer(DEFAULT_DIR_NAME_RENDERER);
        setSortBy(DEFAULT_SORTBY);
        setRegexFilter(DEFAULT_REGEX);
        setOutputWriter(DEFAULT_OUTPUT_WRITER);
        setMaxDepth(DEFAULT_MAX_DEPTH);
    }
    
    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    public String getRegexFilter() {
        return regexFilter;
    }
    
    public void setRegexFilter(String regex) {
        this.regexFilter = regex;
    }
    
    public boolean isShowFileDate() {
        return showFileDate;
    }
    
    public void setShowFileDate(boolean showFileDate) {
        this.showFileDate = showFileDate;
    }
    
    public boolean isShowFiles() {
        return showFiles;
    }
    
    public void setShowFiles(boolean showFiles) {
        this.showFiles = showFiles;
    }
    
    public boolean isShowFilesize() {
        return showFilesize;
    }
    
    public void setShowFilesize(boolean showFilesize) {
        this.showFilesize = showFilesize;
    }
    
    public IDirNameRenderer getDirNameRenderer() {
        return dirNameRenderer;
    }
    
    public void setDirNameRenderer(IDirNameRenderer renderer) {
        this.dirNameRenderer = renderer;
    }
    
    public FileComparator getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(FileComparator sortBy) {
        this.sortBy = sortBy;
    }

    public Writer getOutputWriter() {
        return outputWriter;
    }
    
    public void setOutputWriter(Writer outputWriter) {
        this.outputWriter = outputWriter;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxLevels) {
        this.maxDepth = maxLevels;
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
    
    // -------------------------------------------------------------------------
    // Interface IDirPathRenderer
    // -------------------------------------------------------------------------
    
    /**
     * Enumeration class that expressses how the name of the directory is
     * rendered in the tree. 
     */
    interface IDirNameRenderer {
        String render(File root, File dir);
    }

    // -------------------------------------------------------------------------
    // Class AbsoluteDirPathRenderer
    // -------------------------------------------------------------------------
    
    private static class AbsoluteDirNameRenderer implements IDirNameRenderer {
        
        public String render(File root, File dir) {
            return dir.getAbsolutePath();
        }
    }
    
    // -------------------------------------------------------------------------
    // Class RelativeDirPathRenderer
    // -------------------------------------------------------------------------
    
    private static class RelativeDirNameRenderer implements IDirNameRenderer {
        
        public String render(File root, File dir) {
            return dir.getAbsolutePath().substring(
                root.getAbsolutePath().length()+1); 
        }
    }

    // -------------------------------------------------------------------------
    // Class NameOnlyDirPathRenderer
    // -------------------------------------------------------------------------
    
    private static class NameOnlyDirNameRenderer implements IDirNameRenderer {
        
        public String render(File root, File dir) {
            return dir.getName();
        }
    }
}
