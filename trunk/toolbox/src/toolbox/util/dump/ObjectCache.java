package toolbox.util.dump;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps information (value) stored for each object (key). Necessary in order 
 * to know wheather this object has been displayed before and by which label it 
 * can be referred to. 
 * 
 * key   = object
 * value = ObjectInfo(object)
 */
public class ObjectCache
{
    /** 
     * Sequence number assigned to objects in the cache 
     */
    private int label_ = 0;
    
    /** 
     * Map of object->objectinfo 
     */
    private Map visitedMap_ = new HashMap();
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Puts an object as a key in the cache. Its value is null. 
     * 
     * @param obj
     */
    public void put(Object obj)
    {
        put(obj, null);    
    }
    
    /**
     * Puts an object as the key and its generated ObjectInfo as the value
     * into the cache. The field is necessary to populate an ObjectInfo.
     * 
     * @param  obj    Object to put in the cache
     * @param  field  The field that the object refers to
     */
    public void put(Object obj, Field field)
    {
        if (obj == null)
            throw new RuntimeException("Object null");
            
        visitedMap_.put(obj, 
            new ObjectInfo(obj, label_ + "", field));
    }
    
    /**
     * Retrieves the ObjectInfo for a given object
     * 
     * @param   obj  Object to get info for
     * @return  ObjectInfo for the passed in object
     */
    public ObjectInfo getInfo(Object obj)
    {
        if (obj == null)
            return null;
        else
        {
            ObjectInfo objInfo = (ObjectInfo) visitedMap_.get(obj);
            return objInfo;
        }
    }
    
    /**
     * Determines if an object is already present in the cache
     * 
     * @param   obj  Object to test for presence
     * @return  True if the cache contains the object, false otherwise
     */
    public boolean contains(Object obj)
    {
        if (obj == null)
            throw new RuntimeException("Object null");
            
        return visitedMap_.containsKey(obj);
    }
    
    /**
     * Determines if an object has already been traversed
     * 
     * @param   obj  Object to test for traversal
     * @return  True if the object has been traverse, false otherwise.
     */
    public boolean hasTraversed(Object obj)
    {
        if (obj == null)
            throw new IllegalArgumentException("Object Null");
            
        ObjectInfo objInfo = null;
        return (((objInfo = getInfo(obj)) != null) && objInfo.hasTraversed());
    }
}