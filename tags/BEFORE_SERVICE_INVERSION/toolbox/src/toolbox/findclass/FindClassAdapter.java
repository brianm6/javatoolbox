package toolbox.findclass;

/**
 * Adapter class for FindClassListener.
 */
public class FindClassAdapter implements FindClassListener
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FindClassAdapter.
     */
    public FindClassAdapter()
    {
    }

    //--------------------------------------------------------------------------
    // FindClassListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see FindClassListener#classFound(FindClassResult)
     */
    public void classFound(FindClassResult searchResult)
    {
    }

    
    /**
     * @see FindClassListener#searchingTarget(String)
     */
    public void searchingTarget(String target)
    {
    }

    
    /**
     * @see FindClassListener#searchCancelled()
     */
    public void searchCancelled()
    {
    }
    
    
    /**
     * @see toolbox.findclass.FindClassListener#searchCompleted(
     *      java.lang.String)
     */
    public void searchCompleted(String search)
    {
    }
}