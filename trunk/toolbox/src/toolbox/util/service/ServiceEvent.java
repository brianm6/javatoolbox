package toolbox.util.service;

import java.util.EventObject;

/**
 * ServiceEvent is responsible for _____.
 */
public class ServiceEvent extends EventObject
{
    /**
     * Creates a ServiceEvent.
     */
    public ServiceEvent(Service service)
    {
        super(service);
    }
    
    public Service getService() 
    {
        return (Service) getSource();
    }
}
