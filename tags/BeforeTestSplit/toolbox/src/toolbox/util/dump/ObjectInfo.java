package toolbox.util.dump;

import java.lang.reflect.Field;

/**
 * Information (value) stored for each object (key) in the object cache.
 */
public class ObjectInfo
{
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /** 
     * Object being traversed. 
     */
    private Object object_;
    
    /** 
     * Traversal flag. 
     */
    private boolean traversed_;

    /** 
     * Field that the object as associated with in its parent class. 
     */
    private Field field_;
    
    /**
     * Unique sequence number for referring back to multiple references in the
     * object graph.
     */
    private String seqNum_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ObjectInfo.
     * 
     * @param object Object to store traversal information on.
     * @param seqNum Assigned sequence number.
     */
    public ObjectInfo(Object object, String seqNum)
    {
        this(object, seqNum, null);
    }

    
    /**
     * Creates an ObjectInfo.
     * 
     * @param object Object to store traversal information on.
     * @param seqNum Assigned sequence number.
     * @param field Field associated with the object.
     */
    public ObjectInfo(Object object, String seqNum, Field field)
    {
        object_ = object;
        seqNum_ = seqNum;
        field_  = field;            
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the sequence number for unique identification.
     * 
     * @return Sequence number.
     */
    public String getSequenceNumber()
    {
        return seqNum_;
    }
    
    
    /**
     * Returns true if the object has been traversed previously, false 
     * otherwise.
     * 
     * @return Traversed flag.
     */
    public boolean hasTraversed()
    {
        return traversed_;
    }

    
    /**
     * Sets the flag for whether the object has been traversed already.
     * 
     * @param traversed Traversed flag.
     */
    public void setTraversed(boolean traversed)
    {
        traversed_ = traversed;
    }
    
    
    /**
     * Returns the field if any that was related to this object.
     * 
     * @return Field
     */
    public Field getField()
    {
        return field_;
    }
}