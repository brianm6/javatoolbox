package toolbox.findclass;

/**
 * Adapter class for IFindClassListener.
 */
public class FindClassAdapter implements IFindClassListener
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
    // IFindClassListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see IFindClassListener#classFound(FindClassResult)
     */
    public void classFound(FindClassResult searchResult)
    {
    }

    
    /**
     * @see IFindClassListener#searchingTarget(String)
     */
    public void searchingTarget(String target)
    {
    }

    
    /**
     * @see IFindClassListener#searchCancelled()
     */
    public void searchCancelled()
    {
    }
    
    
    /**
     * @see toolbox.findclass.IFindClassListener#searchCompleted(
     *      java.lang.String)
     */
    public void searchCompleted(String search)
    {
    }
}