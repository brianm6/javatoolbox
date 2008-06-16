package toolbox.plugin.docviewer;

import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceUtil;
import toolbox.util.statemachine.StateMachine;

/**
 * AbstractViewer is a base class implemenation of
 * {@link toolbox.plugin.docviewer.DocumentViewer}.
 */
public abstract class AbstractViewer implements DocumentViewer {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * Displayable name of this document viewer.
     */
    private String name_;

    /**
     * State machine for this viewer's lifecycle.
     */
    private StateMachine machine_;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public AbstractViewer(String name) {
        setName(name);
        machine_ = ServiceUtil.createStateMachine(this);
    }

    // -------------------------------------------------------------------------
    // Service Interface
    // -------------------------------------------------------------------------

    public ServiceState getState() {
        return (ServiceState) machine_.getState();
    }
    
    // -------------------------------------------------------------------------
    // Destroyable Interface
    // -------------------------------------------------------------------------

    public boolean isDestroyed() {
        return getState() == ServiceState.DESTROYED;
    }

    // -------------------------------------------------------------------------
    // Nameable Interface
    // -------------------------------------------------------------------------

    public String getName() {
        return name_;
    }

    public void setName(String name) {
        name_ = name;
    }
}