package toolbox.findclass;

import java.util.ArrayList;
import java.util.List;

/**
 * FindClassCollector is used to collect the results of the execution of 
 * FindClass.
 */
public class FindClassCollector extends FindClassAdapter
{
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
     * Returns array of find class results. 
     * 
     * @return FindClassResult[]
     */
    public FindClassResult[] getResults()
    {
        return (FindClassResult[]) 
            results_.toArray(new FindClassResult[results_.size()]);
    }
    
    
    /**
     * Clears the contents of the colletor.
     * 
     * @return Number of elements cleared
     */
    public int clear() 
    {
        int i = results_.size();
        results_.clear();
        return i;
    }
}