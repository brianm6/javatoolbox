package toolbox.util.io.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;

/**
 * Filename filter that behaves as a logical OR for one or more given filters.
 */
public class OrFilter extends CompoundFilter implements FilenameFilter
{
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
     
    /**
     * Creates an OrFilter.
     */
    public OrFilter()
    {
    } 
        
    
    /**
     * Creates a filter that ORs the given filters.
     * 
     * @param filterOne First filter
     * @param filterTwo Second filter
     */   
    public OrFilter(FilenameFilter filterOne, FilenameFilter filterTwo)
    {
        addFilter(filterOne);
        addFilter(filterTwo);
    }

    //--------------------------------------------------------------------------
    //  FilenameFilter Interface
    //--------------------------------------------------------------------------
        
    /**
     * Accepts files based on the logical OR of the added filters.
     * 
     * @param dir Directory file is contained in
     * @param name Name of file
     * @return True if logical OR is true, false otherwise
     */
    public boolean accept(File dir,String name)
    {
        Iterator i = iterator();
        
        while (i.hasNext())
        {
            FilenameFilter f = (FilenameFilter)i.next();
            
            // Short circuit on first TRUE
            if (f.accept(dir, name))     
                return true;
        }
       
        return false;
    }
}