package toolbox.findclass;

/**
 * Data object specific to the result of a successful class search
 */
public class FindClassResult
{
    private String searchString_;
    private String classLocation_;
    private String classFQN_;
    
    /**
     * Constructor
     * 
     * @param  searchString   Original search string
     * @param  classLocation  Location where class was found (jar/path)
     * @param  classFQN       Fully qualified name of the class found
     */
    public FindClassResult(String searchString, String classLocation, String classFQN)
    {
        searchString_ = searchString;
        classLocation_ = classLocation;
        classFQN_ = classFQN;    
    }
    
    public String getSearchString()
    {
        return searchString_;
    }
    
    public String getClassLocation()
    {
        return classLocation_;
    }
    
    public String getClassFQN()
    {
        return classFQN_;
    }
    
    public String toString()
    {
        return classLocation_ + " => " + classFQN_;
    }
        
}
