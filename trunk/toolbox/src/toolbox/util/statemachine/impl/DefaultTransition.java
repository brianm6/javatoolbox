package toolbox.util.statemachine.impl;

import toolbox.util.statemachine.Transition;

/**
 * Basic implementation of state {@link toolbox.util.statemachine.Transition}.
 * 
 * @see toolbox.util.statemachine.StateMachineFactory
 */
public class DefaultTransition implements Transition
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Unique name of this transition.
     */
    private String name_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DefaultTransition.
     */
    public DefaultTransition(String name)
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
     * Uses this transitions name to determine equality.
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
        
        Transition t = (Transition) obj;
        return getName().equals(t.getName());
    }
}