package toolbox.util.service;

import toolbox.util.ArrayUtil;

/**
 * Event notification implementation for ObservableService implementors. This
 * class is meant to be be embedded in Service implementations that wish to
 * delegate the job of handling registration of listeners and firing of events
 * to a common class.
 */
public class ServiceNotifier implements ObservableService {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Service of origin.
     */
    private ObservableService service_;

    /**
     * Array of listeners interested in events that this service generates.
     */
    private ServiceListener[] listeners_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates an AbstractService with the default value for strict state
     * transitions.
     * 
     * @param service Service instance.
     */
    public ServiceNotifier(ObservableService service) {
        service_ = service;
        listeners_ = new ServiceListener[0];
    }

    // -------------------------------------------------------------------------
    // Service Interface
    // -------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState() {
        return service_.getState();
    }
    
    // --------------------------------------------------------------------------
    // ObservableService Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.util.service.ObservableService#addServiceListener(toolbox.util.service.ServiceListener)
     */
    public void addServiceListener(ServiceListener listener) {
        listeners_ = (ServiceListener[]) ArrayUtil.add(listeners_, listener);
    }


    /*
     * @see toolbox.util.service.ObservableService#removeServiceListener(toolbox.util.service.ServiceListener)
     */
    public void removeServiceListener(ServiceListener listener) {
        listeners_ = (ServiceListener[]) ArrayUtil.remove(listeners_, listener);
    }

    // --------------------------------------------------------------------------
    // Public
    // --------------------------------------------------------------------------

    /**
     * Notifies registered listeners that this service's state has changed.
     * 
     * @throws ServiceException on service related error.
     */
    public void fireServiceStateChanged() throws ServiceException {
        for (
            int i = 0; 
            i < listeners_.length; 
            listeners_[i++].serviceStateChanged(service_));
    }
}