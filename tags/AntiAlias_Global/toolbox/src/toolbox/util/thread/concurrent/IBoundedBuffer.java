package toolbox.util.thread.concurrent;

/**
 * IBoundedBuffer is the interface implemented by all synchronized lists of 
 * finite capacity.
 */
public interface IBoundedBuffer
{
    /**
     * @return  Count
     */
    int count();


    /**
     * @return  Capacity
     */
    int capacity();


    /**
     * @return  True is buffer is full
     */
    boolean isFull();


    /**
     * @return  True if the buffer is empty
     */
    boolean isEmpty();


    /**
     * Puts object in the buffer
     * 
     * @param  x  Object to put
     */
    void put(Object x);


    /**
     * Puts object in the buffer with a given timeout
     * 
     * @param   x        Object to put
     * @param   timeout  Timeout in ms to wait before returning
     * @throws  InterruptedException on interruption
     * @throws  Timeout when timed out
     */
    void put(Object x, long timeout) throws InterruptedException, Timeout;


    /**
     * Takes an object from the buffer
     * 
     * @return  Object taken from the buffer
     */
    Object take();


    /**
     * Takes an object from the buffer waiting at most timeout ms
     * 
     * @param   timeout  Most time to wait if buffer is empty
     * @return  Object taken from the buffer
     * @throws  InterruptedException on interruption
     * @throws  Timeout when timeout out
     */
    Object take(long timeout) throws InterruptedException, Timeout;
}