package toolbox.findclass;

/**
 * Interface for classes interested in listening to Findclass events
 */
public interface IFindClassListener
{
    /**
     * Class has been found
     * 
     * @param  searchResult  Results on class that was found
     */
    public void classFound(FindClassResult searchResult);
    
    /**
     * Notification that a given target is being searched
     * 
     * @param  target  Name of the target directory or archive
     */
    public void searchingTarget(String target);
}
