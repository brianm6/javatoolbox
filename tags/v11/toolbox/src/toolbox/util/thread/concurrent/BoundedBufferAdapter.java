package toolbox.util.thread.concurrent;

import java.util.List;


/**
 * BoundedBufferAdapter.java
 *
 * This class adapts the BoundedBuffer interface to any buffer implementing
 * this List interface.
 */
public class BoundedBufferAdapter
    implements BoundedBuffer
{
    private Mutex mutex_;
    private List buffer_;
    private int capacity_;
    private ConditionVariable notFull_;
    private ConditionVariable notEmpty_;


    /**
    * Constructs a new BoundedBufferAdapter with the specified buffer and
    * capacity.
    *
    * @param    buffer        list implementation of the buffer.
    * @param    capacity    maximum number of elements allowed.
    */
    public BoundedBufferAdapter(List buffer, int capacity)
    {
        buffer_ = buffer;
        capacity_ = capacity;
        mutex_ = new Mutex();
        notFull_ = new ConditionVariable();
        notEmpty_ = new ConditionVariable();
    }


    /**
   * Returns the number of elements in this buffer.
   *
   * @return    the number of elements in this buffer.
   */
    public int count()
    {
        try
        {
            mutex_.lock();

            return buffer_.size();
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Returns the maximum number of elements containable in this buffer.
   *
   * @return    the maximum number of elements containable in this buffer.
   */
    public int capacity()
    {
        return capacity_;
    }


    /**
   * Returns true if this buffer is full.
   *
   * @return    true if this buffer is full.
   */
    public boolean isFull()
    {
        try
        {
            mutex_.lock();

            return buffer_.size() == capacity_;
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Returns true if this buffer is empty.
   *
   * @return    true if this buffer is empty.
   */
    public boolean isEmpty()
    {
        try
        {
            mutex_.lock();

            return buffer_.size() == 0;
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Add x to this buffer if the buffer's capacity has not been reached.
   * Otherwise, block until a slot is available.
   *
   * @param    x        object to add to buffer.
   */
    public void put(Object x)
    {
        try
        {
            mutex_.lock();

            while (buffer_.size() == capacity())
                notFull_.condWait(mutex_);

            buffer_.add(x);
            notEmpty_.condSignal();
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Add x to this buffer if the buffer's capacity has not been reached.
   * Otherwise, block until a slot is available or the timeout period elapses. 
   *
   * @param    x        object to add to buffer.
   * @param    timneout    the maximum time to wait in milliseconds.
   * @exception        InterruptedException if another thread interrupts
   *            a blocked thread.
   * @exception        Timeout if the timeout period elapsed.
   */
    public void put(Object x, long timeout)
             throws InterruptedException, Timeout
    {
        try
        {
            mutex_.lock();

            if (buffer_.size() == capacity())
                notFull_.condWait(mutex_, timeout);

            if (buffer_.size() != capacity())
            {
                buffer_.add(x);
                notEmpty_.condSignal();
            }
            else
                throw new Timeout();
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Return the next element in this buffer or block the calling thread until
   * one is available.
   */
    public Object take()
    {
        try
        {
            mutex_.lock();

            while (buffer_.size() == 0)
                notEmpty_.condWait(mutex_);

            Object x = buffer_.remove(0);
            notFull_.condSignal();

            return x;
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Return the next element in this buffer or block the calling thread until
   * one is available or the timeout period elapses.
   * 
   * @param    timneout    the maximum time to wait in milliseconds.
   * @exception        Timeout if the timeout period elapsed.
   */
    public Object take(long timeout)
                throws InterruptedException, Timeout
    {
        try
        {
            mutex_.lock();

            if (buffer_.size() == 0)
                notEmpty_.condWait(mutex_, timeout);

            if (buffer_.size() > 0)
            {
                Object x = buffer_.remove(0);
                notFull_.condSignal();

                return x;
            }
            else
                throw new Timeout();
        }
        finally
        {
            mutex_.unlock();
        }
    }
}