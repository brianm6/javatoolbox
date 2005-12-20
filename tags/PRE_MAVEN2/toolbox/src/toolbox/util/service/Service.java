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
public interface Service {
    
    // TODO: Rename to LifeCycle
    
    /**
     * Returns the current state of this service.
     * 
     * @return ServiceState
     */
    public ServiceState getState();
}