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
     * Creates a IntSequence.
     * 
     * @param low Lower bound of the sequence (inclusive)
     * @param high Upper bound of the sequence (inclusive)
     */
    public IntSequence(int low, int high, boolean repeating)
    {
        super(repeating);
        
        Validate.isTrue(low <= high, "Invalid bounds: low <= high");
        
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
        
        if (!isRepeating())
        {
            if (gened_.size() == getSize())
                throw new SequenceEndedException("End of sequence");
            
            boolean unique = false;
            
            while (!unique)
            {
                result = 
                    new Integer(getRandomData().nextInt(getLow(), getHigh()));
                
                if (!gened_.contains(result))
                {
                    gened_.add(result);
                    unique = true;
                }
            }
        }
        else
        {
            result = new Integer(getRandomData().nextInt(getLow(), getHigh()));
        }
        
        return result;
    }
    
    
    /**
     * @see toolbox.util.random.RandomSequence#hasMore()
     */
    public boolean hasMore()
    {
        return isRepeating() ? true : gened_.size() < getSize(); 
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
     * Returns the size of the sequence.
     * 
     * @return int
     */
    public int getSize()
    {
        return getHigh() - getLow() + 1;
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
     * Sets the value of high.
     * 
     * @param high The high to set.
     */
    public void setHigh(int high)
    {
        high_ = high;
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
    
    
    /**
     * Sets the value of low.
     * 
     * @param low The low to set.
     */
    public void setLow(int low)
    {
        low_ = low;
    }
}