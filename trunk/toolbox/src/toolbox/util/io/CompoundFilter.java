package toolbox.util.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A Composite file filter joins two filters so that eithers acceptance 
 * is sufficient criteria for the composite filter to accept.
 */
public class CompoundFilter implements FilenameFilter
{
    /** First filter to create composite **/
    private FilenameFilter firstFilter_;
    
    /** Second filter to create composite **/
    private FilenameFilter secondFilter_;
    
    /**
     * Creates a filter that is the composite of two filters
     * 
     * @param  filterOne   First filter
     * @param  filterTwo   Second filter
     */   
    public CompoundFilter(FilenameFilter filterOne, FilenameFilter filterTwo)
    {
        firstFilter_  = filterOne;
        secondFilter_ = filterTwo;
    }
    
    /**
     * Filter as a composite  
     * 
     * @param    dir   Directory file is contained in
     * @param    name  Name of file
     * @return   True if the file matches at least one of two filter,
     *           false otherwise.
     */
    public boolean accept(File dir,String name)
    {
        return firstFilter_.accept(dir, name) || 
               secondFilter_.accept(dir, name);
    }
}