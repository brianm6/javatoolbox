package toolbox.util.statemachine.impl;

import toolbox.util.statemachine.Transition;

/**
 * DefaultTransition is a default implementation of the Transition interface.
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
}
