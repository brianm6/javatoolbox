package toolbox.util.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A file filter that logically ANDs two existing filters so that the 
 * acceptance is sufficient to satisfy both filters
 */
public class AndFilter implements FilenameFilter
{
    private FilenameFilter firstFilter_;
    private FilenameFilter secondFilter_;
    
    /**
     * Creates a filter that logically ANDs two filters
     * 
     * @param  filterOne   First filter
     * @param  filterTwo   Second filter
     */   
    public AndFilter(FilenameFilter filterOne, FilenameFilter filterTwo)
    {
        firstFilter_  = filterOne;
        secondFilter_ = filterTwo;
    }
    
    /**
     * Accepts files that that meet the criteria of filterOne AND filterTwo
     * 
     * @param    dir   Directory file is contained in
     * @param    name  Name of file
     * @return   True if the file matches both filters' criteria, 
     *           false otherwise
     */
    public boolean accept(File dir,String name)
    {
        return firstFilter_.accept(dir, name) &&
               secondFilter_.accept(dir, name);
    }
}