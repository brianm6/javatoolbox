package toolbox.util.service;

/**
 * An object that implements the Service interface adheres to basic lifecycle
 * states and the deterministic transitions between states according to well
 * defined events.
 * 
 * state.
 * <pre>
 *                    Service Finite State Machine
 *                    ============================
 *             
 *                                      
 *  (UNINITIALIZED)        [SUSPENDED]          (DESTROYED)
 *         |                  ^     |                ^
 *         |                  |     |                |
 *    init |          suspend |     | resume         | destroy
 *         |                  |     |                |
 *         |                  |     |                |       
 *         v        start     |     v     stop       |
 *  [INITIALIZED]----------->[RUNNING]---------->[STOPPED]
 *                               ^                   |
 *                               |                   |
 *                               +-------------------+
 *                                    start
 *</pre>                                      
 */              

public interface Service extends Initializable, Startable, Suspendable, 
    Destroyable
{
    //--------------------------------------------------------------------------
    // LifeCycle
    //--------------------------------------------------------------------------
    
    /**
     * Returns the current state of this service.
     * 
     * @return ServiceState
     */
    public ServiceState getState();
    
    
    /**
     * Adds a listener to the list of observers for this service.
     *  
     * @param listener Listener to add.
     */
    void addServiceListener(ServiceListener listener);

    
    /**
     * Removes a listener from the list of observers for this service.
     *  
     * @param listener Listener to remove.
     */
    void removeServiceListener(ServiceListener listener);
}