package toolbox.util.concurrent;

import java.util.ArrayList;

/**
 * Blocking Queue
 */
public class BlockingQueue
{
    private ArrayList _queue = null;
    private Semaphore _semaphore = null;
    private Mutex _mutex = new Mutex();

    public BlockingQueue()
    {
        _semaphore = new Semaphore(0);
        _queue = new ArrayList(50);
    }

    public Object pull() throws InterruptedException
    {
        try
        {
            try
            {
                _semaphore.acquire();
                _mutex.acquire();
                Object obj = _queue.remove(0);

                return obj;
            }
            finally
            {
                _mutex.release();
            }
        }
        catch (InterruptedException e)
        {
            throw e;
        }
    }

    public void push(Object obj) throws InterruptedException
    {
        try
        {
            _mutex.acquire();
            _queue.add(obj);
            _semaphore.release();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            _mutex.release();
        }
    }

    public int size()
    {
        int size = 0;
        try
        {
            _mutex.acquire();
            size = _queue.size();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            _mutex.release();
        }

        return size;
    }
    
    public static void main(String args[]) throws Exception
    {
        BlockingQueue bq = new BlockingQueue();
        //bq.push("one");
        
        System.out.println("pulling empty queue");
        System.out.println("size=" + bq.size());
        System.out.println(bq.pull());
        System.out.println(bq.pull());        
        System.out.println("Done");
        
    }
}