package toolbox.jsourceview;

import java.io.File;
import java.io.FilenameFilter;

import toolbox.util.ArrayUtil;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.ExtensionFilter;
import toolbox.util.io.filter.OrFilter;

/** 
 * Scans file system recursively for files containing source code.
 */
class SourceScanner implements Runnable
{
    /**
     * Logical parent of this object.
     */
    private final JSourceView sourceView_;

    /** 
     * Directory to scan recursively for source files. 
     */
    private File dir_;

    /** 
     * Cancel flag. 
     */
    private boolean cancel_;
    
    /** 
     * Filter for list on directories. 
     */
    private FilenameFilter dirFilter_;

    /** 
     * File filter used to identify source files. 
     */
    private static OrFilter sourceFilter_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a scanner.
     * 
     * @param dir Directory root to scan
     */
    SourceScanner(JSourceView view, File dir)
    {
        dir_  = dir;
        this.sourceView_ = view;
        dirFilter_ = new DirectoryFilter();
        cancel_ = false;
        
        sourceFilter_ = new OrFilter();
        sourceFilter_.addFilter(new ExtensionFilter("c"));
        sourceFilter_.addFilter(new ExtensionFilter("cpp"));
        sourceFilter_.addFilter(new ExtensionFilter("java"));
        sourceFilter_.addFilter(new ExtensionFilter("h"));
        
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Finds all java files in the given directory. Called recursively so
     * the directory is passed on each invocation.
     * 
     * @param file Directory to scan for files
     */
    protected void findJavaFiles(File dir)
    {
        // Short circuit if operation canceled
        if (cancel_)
            return;
            
        // Process files in current directory
        File srcFiles[] = dir.listFiles(sourceFilter_);
        
        if (!ArrayUtil.isNullOrEmpty(srcFiles))
            for (int i = 0; i < srcFiles.length; i++)
                sourceView_.getWorkQueue().enqueue(
                    srcFiles[i].getAbsolutePath());
        
        // Process dirs in current directory
        File dirs[] = dir.listFiles(dirFilter_);
        
        if (!ArrayUtil.isNullOrEmpty(dirs))
        {
            for (int i=0; i<dirs.length; i++)
            {
                this.sourceView_.setScanStatus("Scanning " + dirs[i] + " ...");
                findJavaFiles(dirs[i]);
            }    
        }
    }

    
    /** 
     * Cancels the scanning activity.
     */
    protected void cancel()
    {
        cancel_ = true;
    }
    
    //--------------------------------------------------------------------------
    // Runnable Interface
    //--------------------------------------------------------------------------
            
    /**
     * Starts the scanning activity on a separate thread.
     */
    public void run()
    {
        findJavaFiles(dir_);
        sourceView_.setScanStatus("Done scanning.");
    }
}