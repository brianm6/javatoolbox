package toolbox.util.io;

import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Composite file filter joins two filters so that eithers acceptance 
 * is sufficient criteria for the composite filter to accept.
 */
public abstract class CompoundFilter implements FilenameFilter
{
    private List filters_ = new ArrayList();
    
    /**
     * Adds a filter
     */
    public void addFilter(FilenameFilter filter)
    {
        filters_.add(filter);        
    }
    
    /**
     * @return Iterator over filters
     */
    protected Iterator iterator()
    {
        return filters_.iterator();
    }
}