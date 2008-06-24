package toolbox.findclass;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

/**
 * FindClassCollector collects the results of a FindClass operation.
 * 
 * @see toolbox.findclass.FindClass
 */
public class FindClassCollector extends FindClassAdapter
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Counting semaphore that keeps track of cancel requests.
     */
    private Semaphore cancels_;
    
    /** 
     * Storage for search results.
     * 
     * @see FindClassResult
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
        cancels_ = new Semaphore(0);
    }

    //--------------------------------------------------------------------------
    // Overrides FindClassAdapter
    //--------------------------------------------------------------------------
        
    /*
     * @see toolbox.findclass.FindClassListener#classFound(toolbox.findclass.FindClassResult)
     */
    public void classFound(FindClassResult result)
    {
        results_.add(result);            
    }

    
    /*
     * @see toolbox.findclass.FindClassAdapter#searchCanceled()
     */
    public void searchCanceled()
    {
        cancels_.release();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @throws InterruptedException
     */
    public void waitForCancel() throws InterruptedException
    {
        cancels_.acquire();
    }
    
    
    /**
     * Returns an array of FindClassResult objects. 
     * 
     * @return FindClassResult[]
     */
    public FindClassResult[] getResults()
    {
        return (FindClassResult[]) results_.toArray(new FindClassResult[results_.size()]);
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