package toolbox.util.service;

/**
 * An object that implements the Service interface adheres to basic lifecycle
 * management and (start/stop/resume) and a query interface expose current
 * state.
   <pre>
     
                             init
         +-----------------------------------------+
         |                                         |
         |                  [PAUSED]           [SHUTDOWN]
         |                   ^   |                 ^
         |              pause|   |resume           |  
         |                   |   |                 |shutdown
         v        start      |   v     stop        |
  [INITIALIZED]----------->[RUNNING]---------->[STOPPED]
                               ^                   |
                               |                   |
                               +-------------------+
                                      start
  </pre>                                      
 */              

public interface Service extends Resumable
{
    //--------------------------------------------------------------------------
    // LifeCycle
    //--------------------------------------------------------------------------
    
    /**
     * Initializes the service.
     * 
     * @throws ServiceException if the service encounters problems initializing.
     */
    void initialize() throws ServiceException;

    
    /**
     * Starts the service. Once a service it started, it may either be paused or
     * stopped.
     * 
     * @throws ServiceException if the service encounters problems starting up.
     */
    void start() throws ServiceException;
    
    
    /**
     * Stops the service. One a service is stopped, it may be restarted safely.
     * 
     * @throws ServiceException if the service encounters problems stopping.
     */
    void stop() throws ServiceException;

    //--------------------------------------------------------------------------
    // Monitoring
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if the service is running, false otherwise.
     * 
     * @return boolean
     */
    boolean isRunning();
    
    //--------------------------------------------------------------------------
    // Notification
    //--------------------------------------------------------------------------
    
    /**
     * Adds a listener to the list of observers.
     *  
     * @param listener Listener to add.
     */
    void addServiceListener(ServiceListener listener);

    
    /**
     * Removes a listener from the list of observers.
     *  
     * @param listener Listener to remove.
     */
    void removeServiceListener(ServiceListener listener);
}