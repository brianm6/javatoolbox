package toolbox.util.random;

/**
 * A RandomSequence is responsible for generating a sequence of random values 
 * that can either be repeating or non-repeating. If all possible values have 
 * been generated for a non-repeating sequence, a SequenceEndedException is
 * thrown to on subsequent calls to nextValue().
 */
interface RandomSequence
{
    /**
     * Returns the next value in the sequence.
     * 
     * @return Object
     * @throws SequenceEndedException if the sequence is non-repeating and has 
     *         no more unique values.
     */
    Object nextValue() throws SequenceEndedException;
    
    
    /**
     * Returns true if this sequence guarantees that generated values will 
     * not repeat, false otherwise.
     * 
     * @return boolean
     */
    boolean isNonRepeating();
    
    
    /**
     * Returns true if the sequence is non-repeating and has at least one more 
     * value that can be generated, false otherwise. Always returns true for
     * repeating sequences.
     * 
     * @return boolean
     */
    boolean hasMore();
}