package toolbox.util.statemachine.impl;

import java.util.List;

import toolbox.util.statemachine.State;

/**
 * Basic implementation of a {@link State}.
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
    // State Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.statemachine.State#getTransitions()
     */
    public List getTransitions()
    {
        throw new IllegalArgumentException("TODO");
    }
    
    
    /**
     * @see toolbox.util.statemachine.State#isTerminal()
     */
    public boolean isTerminal()
    {
        return getTransitions().isEmpty();
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