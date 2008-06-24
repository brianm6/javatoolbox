package toolbox.plugin.jsourceview;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;

import toolbox.util.ArrayUtil;
import toolbox.util.io.filter.RegexFileFilter;
import toolbox.util.service.Cancelable;

/** 
 * Scans file system recursively for files containing source code.
 */
public class SourceScanner implements Runnable, Cancelable
{
    // TODO: Change findJavaFiles() to use FileFinder with notifications 
    //       instead of fishing out source files in custom code.
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
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
    private boolean canceled_;
    
    /** 
     * File filter used to identify source files. 
     */
    private static OrFileFilter sourceFilter_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a scanner.
     *
     * @param view Parent view. 
     * @param dir Directory root to scan.
     */
    SourceScanner(JSourceView view, File dir)
    {
        dir_        = dir;
        sourceView_ = view;
        canceled_   = false;
        
        sourceFilter_ = new OrFileFilter();
        sourceFilter_.addFileFilter(new RegexFileFilter("\\.c$", false));
        sourceFilter_.addFileFilter(new RegexFileFilter("\\.cpp$", false));
        sourceFilter_.addFileFilter(new RegexFileFilter("\\.java$", false));
        sourceFilter_.addFileFilter(new RegexFileFilter("\\.h$", false));
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Finds all java files in the given directory. Called recursively so the
     * directory is passed on each invocation.
     * 
     * @param dir Directory to scan for files.
     */
    protected void findJavaFiles(File dir)
    {
        // Short circuit if operation canceled
        if (isCanceled())
            return;
            
        // Process files in current directory
        File[] srcFiles = dir.listFiles((FileFilter) sourceFilter_);
        
        if (!ArrayUtil.isNullOrEmpty(srcFiles))
            for (int i = 0; i < srcFiles.length; i++)
                sourceView_.getWorkQueue().enqueue(
                    srcFiles[i].getAbsolutePath());
        
        // Process dirs in current directory
        File dirs[] = dir.listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
        
        if (!ArrayUtil.isNullOrEmpty(dirs))
        {
            for (int i = 0; i < dirs.length; i++)
            {
                sourceView_.setScanStatus("Scanning " + dirs[i] + " ...");
                findJavaFiles(dirs[i]);
            }    
        }
    }

    //--------------------------------------------------------------------------
    // Cancelable Interface
    //--------------------------------------------------------------------------
    
    /** 
     * Cancels the scanning activity.
     * 
     * @see toolbox.util.service.Cancelable#cancel()
     */
    public void cancel()
    {
        canceled_ = true;
    }
    
    
    /*
     * @see toolbox.util.service.Cancelable#isCanceled()
     */
    public boolean isCanceled()
    {
        return canceled_;
    }
    
    //--------------------------------------------------------------------------
    // Runnable Interface
    //--------------------------------------------------------------------------
            
    /**
     * Starts the scanning activity on a separate thread.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        findJavaFiles(dir_);
        sourceView_.setScanStatus("Done scanning.");
    }
}