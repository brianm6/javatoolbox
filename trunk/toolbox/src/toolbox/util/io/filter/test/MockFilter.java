package toolbox.util.io.filter.test;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter thats sole purpose is to return a predetermined value when asked to
 * accept() a file. 
 */
public class MockFilter implements FilenameFilter
{
    /**
     * Value the mock filter to return on calls to accept().
     */
    private boolean accept_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a MockFilter.
     * 
     * @param accept Static accept criteria
     */
    public MockFilter(boolean accept)
    {
        accept_ = accept;
    }
    
    //--------------------------------------------------------------------------
    // FilenameFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Returns accept_.
     * 
     * @param dir Directory file is contained in
     * @param name Name of file
     * @return boolean 
     */
    public boolean accept(File dir, String name)
    {        
           return accept_;
    }
}