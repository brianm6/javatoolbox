package toolbox.util.random;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;

/**
 * ObjectSequence is responsible for generating a repeating/non-repeating 
 * sequence of objects from a given list of objects.
 */
public class ObjectSequence extends AbstractSequence 
    implements RandomSequence
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Pool of object from which to pick values.
     */
    private List pool_;

    /**
     * Internal random integer sequence used to generate indices into the pool
     * of objects.
     */
    private IntSequence sequence_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ObjectSequence.
     * 
     * @param objectList List of objects from which to populate the sequence.
     * @param nonRepeating True if the sequence should not repeat values.
     */
    public ObjectSequence(List objectList, boolean nonRepeating)
    {
        super(nonRepeating);
        setPool(objectList);
        sequence_ = new IntSequence(0, getPool().size() - 1, nonRepeating);
    }
    
    
    /**
     * Creates an ObjectSequence.
     * 
     * @param objectList Array of objects from which to populate the sequence.
     * @param nonRepeating True if the sequence should not repeat values.
     */
    public ObjectSequence(Object[] objectList, boolean nonRepeating)
    {
        this(Arrays.asList(objectList), nonRepeating);
    }
    
    //--------------------------------------------------------------------------
    // RandomSequence Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.random.RandomSequence#nextValue()
     */
    public Object nextValue() throws SequenceEndedException
    {
        return getPool().get(sequence_.nextInt());
    }
    
    
    /**
     * @see toolbox.util.random.RandomSequence#hasMore()
     */
    public boolean hasMore()
    {
        return sequence_.hasMore(); 
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the pool.
     * 
     * @return List
     */
    protected List getPool()
    {
        return pool_;
    }
    
    
    /**
     * Sets the value of pool.
     * 
     * @param pool The pool to set.
     */
    protected void setPool(List pool)
    {
        pool_ = pool;
        Validate.isTrue(!pool_.isEmpty(), "List cannot be empty.");
    }
}