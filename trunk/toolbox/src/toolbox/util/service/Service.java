package toolbox.util.service;

/**
 * An object that implements the Service interface adheres to basic lifecycle
 * states and the deterministic transitions between states according to well
 * defined events.
 * <p> 
 * <pre class="snippet">
 *                    Service State Finite State Machine
 *                    ==================================
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
    
    
    /**
     * Enables/disables enforcement of strict state transitions.
     * 
     * @param b True for strict state transitions, false otherwise.
     */
    void setStrict(boolean b);
    
    
    /**
     * Returns true if strict state transitions are enabled, false othewise.
     * 
     * @return boolean
     */
    boolean isStrict();
}