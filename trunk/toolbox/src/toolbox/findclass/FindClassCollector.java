package toolbox.findclass;

import java.util.ArrayList;
import java.util.List;

/**
 * Use to gather up search results. Create instance and
 * add as listener FindClass
 */
public class FindClassCollector implements IFindClassListener
{
    /** Storage for search results **/
    private List results_ = new ArrayList();
    
    /**
     * Constructor for FindClassCollector.
     */
    public FindClassCollector()
    {
        super();
    }

    
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

    /**
     * Implemenation of IFindClassListener
     *
     * @param  target  Target being searched
     */
    public void searchingTarget(String target)
    {
    }    
    
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