package toolbox.util.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

/**
 * A file filter that logically ANDs two existing filters so that the 
 * acceptance is sufficient to satisfy both filters
 */
public class AndFilter implements FilenameFilter
{
    private List filters_ = new ArrayList();
    
    
    public AndFilter()
    {
    }
    
    /**
     * Creates a filter that logically ANDs two filters
     * 
     * @param  filterOne   First filter
     * @param  filterTwo   Second filter
     */   
    public AndFilter(FilenameFilter filterOne, FilenameFilter filterTwo)
    {
        addFilter(filterOne);
        addFilter(filterTwo);
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
        Assert.assertTrue("No filters!", filters_.size() > 0);
        
        Iterator i = filters_.iterator();
        
        while(i.hasNext())
        {
            FilenameFilter f = (FilenameFilter) i.next();
         
            // short circuit on first FALSE   
            if (!f.accept(dir, name))
                return false;
        }

        return true;        
    }
    
    /**
     * Adds a filter
     */
    public void addFilter(FilenameFilter filter)
    {
        filters_.add(filter);        
    }
}