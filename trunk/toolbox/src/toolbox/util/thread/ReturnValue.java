package toolbox.util.thread;

import toolbox.util.thread.concurrent.EventSemaphore;

/**
 * ReturnValue enables the retrieval of a requests return value. It can be
 * polled or blocked until the return value is available.
 */
public final class ReturnValue
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Pending state.
     */
    public static final int PENDING_STATE = 0;
    
    /**
     * Started state.
     */
    public static final int STARTED_STATE = 1;
    
    /**
     * Finished state.
     */
    public static final int FINISHED_STATE = 2;

    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Current state.
     */
    private int state_;
    
    /**
     * The actual value of the return object, if any.
     */
    private Object value_;
    
    /**
     * Request.
     */
    private IThreadable request_;
    
    /**
     * Listener.
     */
    private Listener listener_;
    
    /**
     * Is the return value available?
     */
    private EventSemaphore available_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ReturnValue.
     */
    public ReturnValue()
    {
        value_ = null;
        state_ = PENDING_STATE;
        available_ = new EventSemaphore();
    }


    /**
     * Creates a ReturnValue with value. 
     *
     * @param value Return value of the request.
     */
    public ReturnValue(Object value)
    {
        value_ = value;
        available_ = null;
        state_ = FINISHED_STATE;
    }


    /**
     * Creates a ReturnValue with a listener. 
     *
     * @param request Corresponding request.
     * @param listener Listener to notify when done.
     */
    public ReturnValue(IThreadable request, Listener listener)
    {
        request_ = request;

        if ((listener_ = listener) != null)
            listener_.pending(request);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if the result value is available for reading.
     *
     * @return True if the result value is available for reading.
     */
    public boolean isAvailable()
    {
        // If this return value is constructed with a value, there is
        // no need to allocate the event semaphore (expensive).
        return available_ == null || available_.posted();
    }


    /**
     * Returns the return value, blocking until it is available.
     *
     * @return Returns the return value, blocking until it is available.
     */
    public Object getValue()
    {
        if (isAvailable())
            return value_;

        // Wait for the value to be available.
        available_.waitFor();

        return value_;
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
     * Assigns the return value. This operation can only be called once.
     *
     * @param value Return value for the request.
     * @throws ValueAlreadyAssignedException if value was already assigned.
     */
    public void setValue(Object value) throws ValueAlreadyAssignedException
    {
        if (isAvailable())
            throw new ValueAlreadyAssignedException();

        state_ = FINISHED_STATE;

        // Signal the availability of the value.
        value_ = value;

        if (available_ != null)
        {
            available_.post();
            available_ = null;
        }

        // Trigger the finished callback handler if present.
        if (listener_ != null)
            listener_.finished(request_, value_);
    }

    //--------------------------------------------------------------------------
    // Package
    //--------------------------------------------------------------------------
    
    /**
     * Gets the current state of the corresponding request.
     *
     * @return Current state of the corresponding request.
     */
    int getState()
    {
        return state_;
    } 

    //--------------------------------------------------------------------------
    // Interfaces
    //--------------------------------------------------------------------------
        
    /**
     * Listener Interface for events related to the ReturnValue.
     */
    public static interface Listener
    {
        /**
         * Signals reception of request.
         *
         * @param request Pending request.
         */
        void pending(IThreadable request);


        /** 
         * Signals initiation of request.
         *
         * @param request Initiated request.
         */
        void started(IThreadable request);


        /**
         * Signals completion of request.
         *
         * @param request Finished request.
         * @param result Request result.
         */
        void finished(IThreadable request, Object result);
    }

    //--------------------------------------------------------------------------
    // ValuealreadyAssignedException
    //--------------------------------------------------------------------------
    
    /**
     * Exception thrown if the value is already assigned.
     */
    public static class ValueAlreadyAssignedException
        extends RuntimeException
    {
    }
}