package toolbox.util.service;

import java.util.EventObject;

/**
 * ServiceEvent encapsulates a service and its associated event related 
 * information.
 */
public class ServiceEvent extends EventObject
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ServiceEvent.
     *
     * @param service Service to which this event is associated.
     */
    public ServiceEvent(Service service)
    {
        super(service);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Convenience accessor for the Service associated with this event.
     * 
     * @return Service
     * @see EventObject#getSource()
     */
    public Service getService() 
    {
        return (Service) getSource();
    }
}
