package toolbox.findclass;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to collect the results of a single run of the findclass application
 */
public class FindClassCollector extends FindClassAdapter
{
    /** Storage for search results **/
    private List results_ = new ArrayList();
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public FindClassCollector()
    {
    }

    //--------------------------------------------------------------------------
    //  Overridden from FindClassAdapter
    //--------------------------------------------------------------------------
        
    /**
     * Implemenation of IFindClassListener.
     * Class has been found
     * 
     * @param  result  What was found
     */
    public void classFound(FindClassResult result)
    {
        results_.add(result);            
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns array of find class results 
     * 
     * @return  FindClassResult[]
     */
    public FindClassResult[] getResults()
    {
        return (FindClassResult[]) 
            results_.toArray(new FindClassResult[results_.size()]);
    }
    
    
    /**
     * Clears the contents of the colletor
     * 
     * @return  Number of elements cleared
     */
    public int clear() 
    {
        int i = results_.size();
        results_.clear();
        return i;
    }
}