package toolbox.util.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import toolbox.util.ArrayUtil;
import toolbox.util.statemachine.StateMachine;
import toolbox.util.statemachine.StateMachineFactory;

/**
 * Abstract base class for Service implementors.
 */
public class ServiceNotifier
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    private ServiceNature nature_;
    
    /**
     * Array of listeners interested in events that this service generates.
     */
    private ServiceListener[] listeners_;
     
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an AbstractService with the default value for strict state
     * transitions.
     * 
     * @see #DEFAULT_STRICT
     */
    protected ServiceNotifier(ServiceNature nature)
    {
        nature_ = nature;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#addServiceListener(
     *      toolbox.util.service.ServiceListener)
     */
    public void addServiceListener(ServiceListener listener)
    {
        listeners_ = (ServiceListener[]) ArrayUtil.add(listeners_, listener);
    }

    
    /**
     * @see toolbox.util.service.Service#removeServiceListener(
     *      toolbox.util.service.ServiceListener)
     */
    public void removeServiceListener(ServiceListener listener)
    {
        listeners_ = (ServiceListener[]) ArrayUtil.remove(listeners_, listener);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Notifies registered listeners that this service's state has changed.
     * 
     * @throws ServiceException on service related error.
     */
    protected void fireServiceStateChanged() throws ServiceException
    {
        for (int i = 0; i < listeners_.length; 
            listeners_[i++].serviceStateChanged(nature_);
    }
}