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
}
