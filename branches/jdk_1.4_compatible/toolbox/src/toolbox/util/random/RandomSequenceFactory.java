package toolbox.util.random;

import java.util.List;

/**
 * RandomSequenceFactory is a factory for various types of randomized sequences.
 * 
 * @see toolbox.util.random.RandomSequence
 */
public class RandomSequenceFactory
{
    /**
     * Creates a randomized integer sequence.
     * 
     * @param low Lower bound.
     * @param high Upper bound.
     * @param nonRepeating True if the sequence should not repeat values.
     * @return RandomSequence
     */
    public static RandomSequence create(int low, int high, boolean nonRepeating)
    {
        return new IntSequence(low, high, nonRepeating);
    }
    
    
    /**
     * Creates a randomized sequence from a given list of existing objects.
     *  
     * @param objectList List of objects from which to create the sequence.
     * @param nonRepeating True if the sequence should not repeat values.
     * @return RandomSequence
     */
    public static RandomSequence create(List objectList, boolean nonRepeating)
    {
        return new ObjectSequence(objectList, nonRepeating);
    }
    
    
    /**
     * Creates a randomized sequence from a given array of existing objects.
     *  
     * @param objectList Array of objects from which to create the sequence.
     * @param nonRepeating True if the sequence should not repeat values.
     * @return RandomSequence
     */
    public static RandomSequence create(
        Object[] objectList, boolean nonRepeating)
    {
        return new ObjectSequence(objectList, nonRepeating);
    }
}