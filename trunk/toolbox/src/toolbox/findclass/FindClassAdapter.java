package toolbox.findclass;

/**
 * Adapter class for IFindClassListener
 */
public class FindClassAdapter implements IFindClassListener
{

    /**
     * Constructor for FindClassAdapter.
     */
    public FindClassAdapter()
    {
    }

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
}
