package toolbox.util.io.filter.test;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Hard wired boolean filter set in the constructor 
 */
public class MockFilter implements FilenameFilter
{
    private boolean accept_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Cretes an MockFilter
     */
    public MockFilter(boolean accept)
    {
        accept_ = accept;
    }
    
    //--------------------------------------------------------------------------
    //  FilenameFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Accepts files that that meet the criteria of filterOne AND filterTwo
     * 
     * @param   dir   Directory file is contained in
     * @param   name  Name of file
     * @return  Boolean 
     */
    public boolean accept(File dir, String name)
    {        
   		return accept_;
    }
}