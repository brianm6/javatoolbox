package toolbox.findclass;

/**
 * Data object specific to the result of a successful class search
 */
public class FindClassResult
{
    /** Search string used to find this result **/
    private String searchString_;
    
    /** Location of the class file **/
    private String classLocation_;
    
    /** Fully qualified name of the found class file **/
    private String classFQN_;
    
    /**
     * Constructor
     * 
     * @param  searchString   Original search string
     * @param  classLocation  Location where class was found (jar/path)
     * @param  classFQN       Fully qualified name of the class found
     */
    public FindClassResult(String searchString, String classLocation, 
        String classFQN)
    {
        searchString_ = searchString;
        classLocation_ = classLocation;
        classFQN_ = classFQN;    
    }

    /**
     * @return Search string 
     */    
    public String getSearchString()
    {
        return searchString_;
    }

    /**
     * @return Class location
     */    
    public String getClassLocation()
    {
        return classLocation_;
    }

    /**
     * @return Fully qualified class name
     */    
    public String getClassFQN()
    {
        return classFQN_;
    }

    /**
     * @return Stringified
     */    
    public String toString()
    {
        return classLocation_ + " => " + classFQN_;
    }
        
}
