package toolbox.util.service;

/**
 * ObservableService is a notification interface for Service implementors.
 * 
 * @see toolbox.util.service.ServiceNotifier
 */
public interface ObservableService
{
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
