package toolbox.plugin.docviewer;

import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceUtil;
import toolbox.util.statemachine.StateMachine;

/**
 * AbstractViewer is a base class implemenation of DocumentViewer.
 */
public abstract class AbstractViewer implements DocumentViewer
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
   
    /**
     * Viewer name.
     */
    private String name_;
    
    /**
     * State machine for this viewer's lifecycle.
     */
    private StateMachine machine_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AbstractViewer.
     * 
     * @param name Viewer name.
     */
    public AbstractViewer(String name)
    {
        setName(name);
        machine_ = ServiceUtil.createStateMachine(this);
    }

    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // Nameable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName()
    {
        return name_;
    }
    
    
    /**
     * @see toolbox.util.service.Nameable#setName(java.lang.String)
     */
    public void setName(String name)
    {
        name_ = name;
    }
}