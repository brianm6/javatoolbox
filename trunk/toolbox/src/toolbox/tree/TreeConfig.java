package toolbox.tree;

import java.io.OutputStreamWriter;
import java.io.Writer;

import toolbox.util.collections.AsMap;
import toolbox.util.file.FileComparator;

/**
 * Javabean that contains all the configuration options supported by 
 * {@link toolbox.tree.Tree}.
 */
public class TreeConfig {

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
     * Default is not to show absolute paths. 
     */
    public static final boolean DEFAULT_SHOW_FILEPATH = false;
    
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
     * Flag to toggle the showing of a file's absolute path. 
     */
    private boolean showPath;
    
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
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public TreeConfig() {
        setShowFiles(DEFAULT_SHOW_FILES);
        setShowFilesize(DEFAULT_SHOW_FILESIZE);
        setShowFileDate(DEFAULT_SHOW_FILEDATE);
        setShowPath(DEFAULT_SHOW_FILEPATH);
        setSortBy(DEFAULT_SORTBY);
        setRegexFilter(DEFAULT_REGEX);
        setOutputWriter(DEFAULT_OUTPUT_WRITER);
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
    
    public boolean isShowPath() {
        return showPath;
    }
    
    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
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
