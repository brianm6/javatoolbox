package toolbox.util.io.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Logical NOT filter
 */
public class NotFilter implements FilenameFilter
{
    private FilenameFilter filter_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a NotFilter
     * 
     * @param  filter  Filter to negate
     */   
    public NotFilter(FilenameFilter filter)
    {
        filter_  = filter;
    }
    
    //--------------------------------------------------------------------------
    //  FilenameFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Negates a filter's accept() method
     * 
     * @param    dir   Directory file is contained in
     * @param    name  Name of file
     * @return   Inverse of filter
     *           
     */
    public boolean accept(File dir,String name)
    {
        return !filter_.accept(dir, name);
    }
}