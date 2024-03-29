package toolbox.util.random;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

/**
 * IntSequence is responsible for generating a sequence of 
 * repeating or non-repeating random integers in a given range.
 * <p>
 * <b>Example</b>
 * <pre class="snippet">
 *   // Generates repeating random ints between 10 and 20
 *   IntSequence s = new IntSequence(10, 20, false);
 *   int i = s.nextInt();
 *   
 *   // Generates non-repeating random ints between -20 and 30
 *   IntSequence snr = new IntSequence(-20, 30, true);
 *   while (snr.hasMore())
 *       int j = snr.nextInt();
 * </pre>
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
     * Keeps track of the generated values so we don't repeat. 
     */
    private List generated_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an IntSequence that generates a repeating sequence of random
     * integers between 0 and {@link Integer#MAX_VALUE} - 1.
     */
    public IntSequence()
    {
        this(0, Integer.MAX_VALUE - 1, false);
    }
    
    
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
        
        Validate.isTrue(low <= high, 
            "Lower bound " 
            + low 
            + " must be less than or equal to upper bound "
            + high 
            + ".");
        
        setLow(low);
        setHigh(high);
        generated_ = new ArrayList();
    }
    
    //--------------------------------------------------------------------------
    // RandomSequence Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.random.RandomSequence#nextValue()
     */
    public Object nextValue() throws SequenceEndedException
    {
        // TODO: Optimize without affecting the distribution
        
        Integer result = null;
        
        if (isNonRepeating())
        {
            if (!hasMore())
                throw new SequenceEndedException("End of sequence " + this);
            
            boolean unique = false;

            // Keep on looping until we find an integer that hasn't been picked
            while (!unique)
            {
                result = nextInternal();
                
                if (!generated_.contains(result))
                {
                    generated_.add(result);
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
        return isNonRepeating() ? generated_.size() < getSize() : true; 
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the next value in the sequence.
     * 
     * @return int
     * @throws SequenceEndedException if the end of the sequence has been 
     *         reached.
     */
    public int nextInt() throws SequenceEndedException
    {
        return ((Integer) nextValue()).intValue();
    }

    
    /**
     * Returns the highest value generated by this sequence.
     * 
     * @return int
     */
    public int getHigh()
    {
        return high_;
    }

    
    /**
     * Returns the lowest value generated by this sequence.
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
     * Returns the size of this sequence.
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
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "[" + getLow() + ".." + getHigh() + "]";
    }
}