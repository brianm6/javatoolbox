package toolbox.util.concurrent;

/**
 * Notification interface for the BatchingQueueReader
 */
public interface IBatchingQueueListener
{
    /**
     * Notification that new elements have been pulled off of the queue
     * 
     * @param  elements  Array of objects pulled from the queue
     */
    public void notify(Object[] elements);
}