package toolbox.util.thread;

import toolbox.util.sync.BoundedBuffer;

/**
 * PrivateQueueStrategy.java
 *
 * This class implements a private-queue strategy that puts requests on a
 * client provided queue.  It is up to the client to pull requests from the
 * queue.
 */
public class PrivateQueueStrategy extends AbstractDispatcherStrategy
{
    private BoundedBuffer privateQueue_;

    /**
    * Creates a private queue strategy using queue.
    *
    * @param    queue        the private queue to publish to.
    */
    public PrivateQueueStrategy(BoundedBuffer queue)
    {
        privateQueue_ = queue;
    }


    /**
    * Publish the request by putting it on the private queue.
    *
    * @param    returnValue    the holder for the return value.
    * @param    request        the request to publish.
    */
    public ReturnValue dispatch(IThreadable request)
    {
        privateQueue_.put(request);

        return new ReturnValue(null);
    }
}