package toolbox.findclass;

import java.util.Date;

/**
 * Data object specific to the result of a successful class search.
 */
public class FindClassResult
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Search string used to find this result. 
     */
    private String searchString_;
    
    /** 
     * Location of the class file.
     */
    private String classLocation_;
    
    /** 
     * Fully qualified name of the found class file.
     */
    private String classFQN_;
    
    /** 
     * Size of the class file.
     */
    private long fileSize_;
    
    /** 
     * File timestamp.
     */
    private Date timestamp_;
    
    /**
     * Position at which the the match begins.
     */
    private int matchBegin_;
    
    /**
     * Position at which the match ends.
     */
    private int matchEnd_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a FindClassResult with the given attributes.
     * 
     * @param searchString Original search string
     * @param classLocation Location where class was found (jar/path)
     * @param classFQN Fully qualified name of the class found
     * @param fileSize Size of the class file in bytes
     * @param timestamp Timestamp on classfile
     * @param matchBegin Beginning index into classFQN of the match
     * @param matchEnd Ending index into classFQN of the match
     */
    public FindClassResult(
        String searchString, 
        String classLocation, 
        String classFQN, 
        long fileSize, 
        Date timestamp, 
        int matchBegin,
        int matchEnd)
    {
        searchString_  = searchString;
        classLocation_ = classLocation;
        classFQN_      = classFQN;    
        fileSize_      = fileSize;
        timestamp_     = timestamp;
        
        setMatchBegin(matchBegin);
        setMatchEnd(matchEnd);
    }

    //--------------------------------------------------------------------------
    //  Accessors/Mutators
    //--------------------------------------------------------------------------

    /**
     * Returns the search string.
     * 
     * @return Search string 
     */    
    public String getSearchString()
    {
        return searchString_;
    }

    
    /**
     * Returns the class location.
     * 
     * @return Class location
     */    
    public String getClassLocation()
    {
        return classLocation_;
    }

    
    /**
     * Returns the fully qualified class name.
     * 
     * @return Fully qualified class name
     */    
    public String getClassFQN()
    {
        return classFQN_;
    }

    
    /**
     * Returns the size of the class file.
     * 
     * @return Size of the class file
     */
    public long getFileSize()
    {
        return fileSize_;
    }
    
    
    /**
     * Returns the timestamp of the class file.
     * 
     * @return Timestamp of the class file
     */
    public Date getTimestamp()
    {
        return timestamp_;
    }

    
    /**
     * Returns the index into the FQCN at which the match starts.
     * 
     * @return Zero based index
     */
    public int getMatchBegin()
    {
        return matchBegin_;
    }

    
    /**
     * Returns the index into the FQCN at which the match ends.
     * 
     * @return Zero based index
     */
    public int getMatchEnd()
    {
        return matchEnd_;
    }

    
    /**
     * Sets the starting index at which the match begins.
     * 
     * @param i Zero based starting index
     */
    public void setMatchBegin(int i)
    {
        matchBegin_ = i;
    }

    
    /**
     * Sets the ending index at which the match ends.
     * 
     * @param i Zero based ending index
     */
    public void setMatchEnd(int i)
    {
        matchEnd_ = i;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns string containing the class location and the FQCN.
     * 
     * @return String
     */    
    public String toString()
    {
        return classLocation_ + " => " + classFQN_;
    }
}