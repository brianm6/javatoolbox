package toolbox.findclass;

public interface IFindClassListener
{
    /**
     * Class has been found
     * 
     * @param  location    Location of where the class was found
     * @param  classFound  Name of the class that matched the search criteria
     */
    public void classFound(FindClassResult searchResult);
}
