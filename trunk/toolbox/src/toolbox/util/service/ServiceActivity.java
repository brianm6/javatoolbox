package toolbox.util.service;

import java.io.Serializable;

import toolbox.util.AbstractConstant;

/**
 * Activity that is performed to transition from one ServiceState to another.
 * Generalization of AbstractConstant ensures constant friendly serialization.
 * 
 * @see toolbox.util.service.Service
 * @see toolbox.util.service.ServiceState
 */
public class ServiceActivity extends AbstractConstant implements Serializable
{
    //--------------------------------------------------------------------------
    // Activity Constants
    //--------------------------------------------------------------------------
    
    /**
     * Activity to initialize a service.
     */
    public static final ServiceActivity INITIALIZE = 
        new ServiceActivity("initialize");
        
    /**
     * Activity to start a service.
     */
    public static final ServiceActivity START = 
        new ServiceActivity("start");
    
    /**
     * Activity to suspend a service.
     */
    public static final ServiceActivity SUSPEND = 
        new ServiceActivity("suspend");
    
    /**
     * Activity to resume a service.
     */
    public static final ServiceActivity RESUME = 
        new ServiceActivity("resume");
    
    /**
     * Activity to stop a service.
     */
    public static final ServiceActivity STOP = 
            new ServiceActivity("stop");
    
    /**
     * Activity to destroy a service.
     */
    public static final ServiceActivity DESTROY = 
        new ServiceActivity("destroy");
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * String version of the activity.
     */
    private String activity_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction.
     * 
     * @param activity Service activity.
     */
    private ServiceActivity(String activity)
    {
        activity_ = activity;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns activity in string form.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return activity_;
    }
}