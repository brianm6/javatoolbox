package toolbox.findclass;

import java.util.Date;

/**
 * Data object specific to the result of a successful class search
 */
public class FindClassResult
{
    /** 
     * Search string used to find this result 
     */
    private String searchString_;
    
    /** 
     * Location of the class file
     */
    private String classLocation_;
    
    /** 
     * Fully qualified name of the found class file
     */
    private String classFQN_;
    
    /** 
     * Size of the class file
     */
    private long fileSize_;
    
    /** 
     * File timestamp 
     */
    private Date timestamp_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a FindClassResult
     * 
     * @param  searchString   Original search string
     * @param  classLocation  Location where class was found (jar/path)
     * @param  classFQN       Fully qualified name of the class found
     * @param  fileSize       Size of the class file in bytes
     * @param  timestamp      Timestamp on classfile
     */
    public FindClassResult(String searchString, String classLocation, 
        String classFQN, long fileSize, Date timestamp)
    {
        searchString_ = searchString;
        classLocation_ = classLocation;
        classFQN_ = classFQN;    
        fileSize_ = fileSize;
        timestamp_ = timestamp;
    }

    //--------------------------------------------------------------------------
    //  Accessors/Mutators
    //--------------------------------------------------------------------------

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
     * @return Size of the class file
     */
    public long getFileSize()
    {
        return fileSize_;
    }
    
    /**
     * @return Timestamp of the class file
     */
    public Date getTimestamp()
    {
        return timestamp_;
    }
    
    /**
     * @return Stringified
     */    
    public String toString()
    {
        return classLocation_ + " => " + classFQN_;
    }
}