package toolbox.findclass;

import java.util.ArrayList;
import java.util.List;

/**
 * FindClassCollector collects the results of a FindClass operation.
 */
public class FindClassCollector extends FindClassAdapter
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Storage for search results. 
     */
    private List results_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FindClassCollector.
     */
    public FindClassCollector()
    {
        results_ = new ArrayList();
    }

    //--------------------------------------------------------------------------
    // Overrides FindClassAdapter
    //--------------------------------------------------------------------------
        
    /**
     * @see toolbox.findclass.FindClassListener#classFound(
     *      toolbox.findclass.FindClassResult)
     */
    public void classFound(FindClassResult result)
    {
        results_.add(result);            
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns an array of FindClassResult objects. 
     * 
     * @return FindClassResult[]
     */
    public FindClassResult[] getResults()
    {
        return (FindClassResult[]) 
            results_.toArray(new FindClassResult[results_.size()]);
    }
    
    
    /**
     * Clears the contents of the colletor and returns the number of elements
     * cleared.
     * 
     * @return int
     */
    public int clear() 
    {
        int i = results_.size();
        results_.clear();
        return i;
    }
}