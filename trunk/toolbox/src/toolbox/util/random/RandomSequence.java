package toolbox.util.random;


/**
 * A RandomSequence is responsible for generating a sequence of 
 * non-repreating values. If all possible values have been generated, the
 * sequence has ended and will throw a SequenceEndedException if asked to
 * create additional values.
 */
interface RandomSequence
{
    /**
     * Returns the next value in the sequence.
     * 
     * @return Object
     * @throws SequenceEndedException if the sequence has been exhausted and 
     *         there are no more values left to generate.
     */
    Object nextValue() throws SequenceEndedException;
    
    
    /**
     * Returns true if this sequence may repeat values in the sequence, false
     * otherwise.
     * 
     * @return boolean
     */
    boolean isRepeating();
    
    
    /**
     * Returns true if the sequence is non-repeating and has at least one more 
     * value that can be generated, false otherwise.
     * 
     * @return boolean
     */
    boolean hasMore();
}