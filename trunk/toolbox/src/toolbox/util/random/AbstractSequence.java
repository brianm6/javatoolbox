package toolbox.util.random;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

/**
 * AbstractSequence is responsible for _____.
 */
public abstract class AbstractSequence implements RandomSequence
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Flag for repeating values in this sequence.
     */    
    private boolean repeating_;

    /**
     * Random data generator.
     */
    private RandomData randomData_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AbstractSequence.
     */
    public AbstractSequence(boolean repeating)
    {
        setRepeating(repeating);
        setRandomData(new RandomDataImpl());
    }

    //--------------------------------------------------------------------------
    // RandomSequence Interface (partial)
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.random.RandomSequence#isRepeating()
     */
    public boolean isRepeating()
    {
        return repeating_;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Sets the value of repeating.
     * 
     * @param repeating The repeating to set.
     */
    protected void setRepeating(boolean repeating)
    {
        repeating_ = repeating;
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