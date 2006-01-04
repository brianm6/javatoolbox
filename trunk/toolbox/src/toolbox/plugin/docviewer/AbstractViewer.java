package toolbox.plugin.docviewer;

import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceUtil;
import toolbox.util.statemachine.StateMachine;

/**
 * AbstractViewer is a base class implemenation of 
 * {@link toolbox.plugin.docviewer.DocumentViewer}.
 */
public abstract class AbstractViewer implements DocumentViewer
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
   
    /**
     * Displayable name of this document viewer.
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
    
    /*
     * @see toolbox.util.service.Destroyable#isDestroyed()
     */
    public boolean isDestroyed() {
        return getState() == ServiceState.DESTROYED;
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