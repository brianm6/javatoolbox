package toolbox.findclass;

/**
 * Interface for classes interested in listening to Findclass events.
 */
public interface FindClassListener
{
    /**
     * Notification that a class matching the search criteria has been found.
     * 
     * @param searchResult Results on class that was found
     */
    void classFound(FindClassResult searchResult);
    
    
    /**
     * Notification that a search completed.
     * 
     * @param search Search text
     */
    void searchCompleted(String search);
    
    
    /**
     * Notification that a given target is being searched.
     * 
     * @param target Name of the target directory or archive
     */
    void searchingTarget(String target);
    
    
    /**
     * Notification that a search was cancelled.
     */
    void searchCancelled();
}