package toolbox.util.thread;

import toolbox.util.thread.concurrent.EventSemaphore;


/**
 * ReturnValue.java
 *
 * This class enabled the retrieval of a requests return value.  It can be
 * polled or blocked until the return value is available.
 */
public final class ReturnValue
{
    private int state_;
    private Object value_;
    private IThreadable request_;
    private Listener listener_;
    private EventSemaphore available_;

    public static interface Listener
    {

        /**
         * Signals reception of request.
         *
         * @param  request    the pending request.
         */
        void pending(IThreadable request);


        /** 
         * Signals initiation of request.
         *
         * @param  request    the initiated request.
         */
        void started(IThreadable request);


        /**
         * Signals completion of request.
         *
         * @param  request    the finished request.
         * @param  result    the request result.
         */
        void finished(IThreadable request, Object result);
    }

    public static class ValueAlreadyAssignedException
        extends RuntimeException
    {
    }


    // Constants to identify the state of the request.
    public static final int PENDING_STATE = 0;
    public static final int STARTED_STATE = 1;
    public static final int FINISHED_STATE = 2;


    /**
     * Constructs a new unavailable return value.
     */
    public ReturnValue()
    {
        value_ = null;
        state_ = PENDING_STATE;
        available_ = new EventSemaphore();
    }


    /**
     * Constructs a new return value with value. 
     *
     * @param    value         the return value of the request.
     */
    public ReturnValue(Object value)
    {
        value_ = value;
        available_ = null;
        state_ = FINISHED_STATE;
    }


    /**
     * Constructs a new return value with the listneer. 
     *
     * @param    request          the corresponding request.
     * @param    listener      the listener to notify when done.
     */
    public ReturnValue(IThreadable request, Listener listener)
    {
        this();
        request_ = request;

        if ((listener_ = listener) != null)
            listener_.pending(request);
    }


    /**
     * Returns true if the result value is available for reading.
     *
     * @return    true if the result value is available for reading.
     */
    public boolean isAvailable()
    {

        //
        // If this return value is constructed with a value, there is
        // no need to allocate the event semaphore (expensive).
        //
        return available_ == null || available_.posted();
    }


    /**
     * Returns the return value, blocking until it is available.
     *
     * @return    Returns the return value, blocking until it is available.
     */
    public Object getValue()
    {
        if (isAvailable())
            return value_;

        //
        // Wait for the value to be available.
        //
        available_.waitFor();

        return value_;
    }


    /**
     * Gets the current state of the corresponding request.
     *
     * @return    the current state of the corresponding request.
     */
    int getState()
    {
        return state_;
    } 


    /**
     * Indicates the corresponding request is processing.
     */
    public void setStarted()
    {
        if (state_ != STARTED_STATE)
        {
            if (state_ == PENDING_STATE)
            {
                state_ = STARTED_STATE;

                if (listener_ != null)
                    listener_.started(request_);
            }
            else
                throw new IllegalStateException();
        }
    }


    /**
     * Assigns the return value.  This operation can only be called once.
     *
     * @param   value         the return value for the request.
     * @throws  ValueAlreadyAssignedException if value was already assigned.
     */
    public void setValue(Object value)
    {
        if (isAvailable())
            throw new ValueAlreadyAssignedException();

        state_ = FINISHED_STATE;

        //
        // Signal the availability of the value.
        //
        value_ = value;

        if (available_ != null)
        {
            available_.post();
            available_ = null;
        }

        //
        // Trigger the finished callback handler if present.
        //
        if (listener_ != null)
            listener_.finished(request_, value_);
    }
}