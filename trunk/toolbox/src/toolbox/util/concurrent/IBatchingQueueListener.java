package toolbox.util.concurrent;

/**
 * Notification interface for the BatchingQueueReader.
 */
public interface IBatchingQueueListener
{
    /**
     * Next batch of elements have been pulled off of the queue
     * 
     * @param elements Array of objects pulled from the queue
     */
    void nextBatch(Object[] elements);
}