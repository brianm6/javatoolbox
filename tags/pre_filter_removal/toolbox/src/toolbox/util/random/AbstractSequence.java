package toolbox.util.random;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

/**
 * Abstract base class for sequences.
 */
public abstract class AbstractSequence implements RandomSequence
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Non-repeating sequence flag.
     */    
    private boolean nonRepeating_;

    /**
     * Random data generator.
     */
    private RandomData randomData_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AbstractSequence.
     * 
     * @param nonRepeating Set to true to create a non-repeating sequence or 
     *        false to create a repeating sequence.
     */
    public AbstractSequence(boolean nonRepeating)
    {
        setNonRepeating(nonRepeating);
        setRandomData(new RandomDataImpl());
    }

    //--------------------------------------------------------------------------
    // RandomSequence Interface (partial)
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.random.RandomSequence#isNonRepeating()
     */
    public boolean isNonRepeating()
    {
        return nonRepeating_;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Sets the value for the non-repeating flag.
     * 
     * @param nonRepeating True for non-repeating, false otherwise.
     */
    protected void setNonRepeating(boolean nonRepeating)
    {
        nonRepeating_ = nonRepeating;
    }

    
    /**
     * Returns the randomData.
     * 
     * @return RandomData
     */
    protected RandomData getRandomData()
    {
        return randomData_;
    }
    
    
    /**
     * Sets the value of randomData.
     * 
     * @param randomData The randomData to set.
     */
    protected void setRandomData(RandomData randomData)
    {
        randomData_ = randomData;
    }
}