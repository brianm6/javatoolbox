package toolbox.util.thread;

/**
 * IThreadable.java
 *
 * Interface implemented by all requests that must be delivered using a
 * particular threading strategy. Runnable interface was not used because
 * it did not provide the needed return value.
 */
public interface IThreadable
{

    /**
     * Performs the processing of the request.
     *
     * @return  Result of the request or null if no result.
     */
    public Object run();
}