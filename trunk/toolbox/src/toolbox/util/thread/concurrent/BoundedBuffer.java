package toolbox.util.thread.concurrent;

/**
 * BoundedBuffer.java
 *
 * Interface implemented by all synchronized lists of finite capacity.
 */
public interface BoundedBuffer
{
    int count();

    int capacity();

    boolean isFull();

    boolean isEmpty();

    void put(Object x);

    void put(Object x, long timeout)
      throws InterruptedException, Timeout;

    Object take();

    Object take(long timeout)
         throws InterruptedException, Timeout;
}