package toolbox.util.thread;

/**
 * IThreadable.java
 *
 * Interface implemented by all requests that must be delivered using a
 * particular threading strategy.
 */
public interface IThreadable
{

    /**
    * Performs the processing of the request.
    *
    * @return    the result of the request or null if no result.
    */
    public Object run();
}