package toolbox.util.random;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

/**
 * IntSequence is responsible for generating a non-repeating sequence
 * of positive integers.
 */
public class IntSequence extends AbstractSequence implements RandomSequence
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Lower bound of the sequence (inclusive).
     */
    private int low_;
    
    /**
     * Upper bound of the sequence (inclusive).
     */
    private int high_;
    
    /**
     * List of all generated numbers so far so we don't repeat.
     */
    private List gened_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an IntSequence.
     * 
     * @param low Lower bound of the sequence (inclusive).
     * @param high Upper bound of the sequence (inclusive).
     * @param nonRepeating True for non-repeating, false otherwise.
     */
    public IntSequence(int low, int high, boolean nonRepeating)
    {
        super(nonRepeating);
        
        Validate.isTrue(
            low <= high, 
            "Lower bound " + low + " must be less than or equal to upper bound "
            + high + ".");
        
        setLow(low);
        setHigh(high);
        gened_ = new ArrayList();
    }
    
    //--------------------------------------------------------------------------
    // RandomSequence Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.random.RandomSequence#nextValue()
     */
    public Object nextValue() throws SequenceEndedException
    {
        Integer result = null;
        
        if (isNonRepeating())
        {
            if (!hasMore())
                throw new SequenceEndedException("End of sequence");
            
            boolean unique = false;

            // Keep on looping until we find an integer that hasn't been picked
            while (!unique)
            {
                result = nextInternal();
                
                if (!gened_.contains(result))
                {
                    gened_.add(result);
                    unique = true;
                }
            }
        }
        else
        {
            result = nextInternal();
        }
        
        return result;
    }
    
    
    /**
     * @see toolbox.util.random.RandomSequence#hasMore()
     */
    public boolean hasMore()
    {
        return isNonRepeating() ? gened_.size() < getSize() : true; 
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the next value in the sequence.
     * 
     * @return int
     * @throws SequenceEndedException of the end of sequence has been reached.
     */
    public int nextInt() throws SequenceEndedException
    {
        return ((Integer) nextValue()).intValue();
    }

    
    /**
     * Returns the high.
     * 
     * @return int
     */
    public int getHigh()
    {
        return high_;
    }

    
    /**
     * Returns the low.
     * 
     * @return int
     */
    public int getLow()
    {
        return low_;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Internal delegate for creation of the next value in the sequence. 
     * Special cases is a sequence of bounds (x, x) in which case the value
     * should always be x.
     * 
     * @return Integer
     */
    protected Integer nextInternal() 
    {
        if (getHigh() == getLow())
            return new Integer(getHigh());
        else
            return new Integer(getRandomData().nextInt(getLow(), getHigh()));
    }
    
    /**
     * Returns the size of the sequence.
     * 
     * @return int
     */
    protected int getSize()
    {
        return getHigh() - getLow() + 1;
    }
    
    
    /**
     * Sets the value of high.
     * 
     * @param high The high to set.
     */
    protected void setHigh(int high)
    {
        high_ = high;
    }
    
    
    /**
     * Sets the value of low.
     * 
     * @param low The low to set.
     */
    protected void setLow(int low)
    {
        low_ = low;
    }
}