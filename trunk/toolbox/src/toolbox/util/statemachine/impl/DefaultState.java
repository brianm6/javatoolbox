package toolbox.util.statemachine.impl;

import toolbox.util.statemachine.State;

/**
 * Basic implementation of a {@link toolbox.util.statemachine.State}.
 * 
 * @see toolbox.util.statemachine.StateMachineFactory
 */
public class DefaultState implements State
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Unique name of this state.
     */
    private String name_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DefaultState.
     */
    public DefaultState(String name)
    {
        setName(name);
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
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Uses this states name to determine equality.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        
        if (this == obj)
            return true;
        
        if (!getClass().getName().equals(obj.getClass().getName()))
            return false;
        
        State s = (State) obj;
        return getName().equals(s.getName());
    }
}