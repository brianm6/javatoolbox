package toolbox.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple (much to be desired) queue implementation.
 */
public class Queue
{
    /** 
     * Backing store for the queue. 
     */
    private List store_ = new ArrayList();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
	 * Creates a Queue.
	 */
    public Queue()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
	 * Adds an object to the back of the queue.
	 * 
	 * @param obj Object to enqueue
	 */
    public synchronized void enqueue(Object obj)
    {
        store_.add(obj);
    }

    
    /**
	 * Removes an object from the front of the queue.
	 * 
	 * @return Object dequeued. Null if the queue is empty
	 */
    public synchronized Object dequeue()
    {
        Object obj = null;

        if (store_.size() > 0)
            obj = store_.remove(0);

        return obj;
    }

    
    /**
	 * Peeks at the object at the front of the queue.
	 * 
	 * @return Object at the front of the queue. Null if the queue is empty.
	 */
    public synchronized Object peek()
    {
        Object obj = null;

        if (!isEmpty())
            obj = store_.get(0);

        return obj;
    }

    
    /**
	 * Empty check.
	 * 
	 * @return True if the queue is empty, false otherwise.
	 */
    public synchronized boolean isEmpty()
    {
        return store_.size() == 0;
    }

    
    /**
	 * Returns size of queue.
	 * 
	 * @return Size of queue
	 */
    public synchronized int size()
    {
        return store_.size();
    }
}