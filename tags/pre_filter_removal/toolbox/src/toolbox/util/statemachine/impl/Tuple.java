package toolbox.util.statemachine.impl;

import toolbox.util.statemachine.State;
import toolbox.util.statemachine.Transition;

/**
 * Tuple is responsible for storing the association between a transition's 
 * begin state, the transition, and the transitions end state.
 */
public class Tuple
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * State from which the transition originates.
     */
    private State fromState_;
    
    /**
     * Activity that stimulates this transition.
     */
    private Transition activity_;
    
    /**
     * State at which the transition terminates. 
     */
    private State toState_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Tuple.
     * 
     * @param fromState Origin state.
     * @param activity Stimulus.
     * @param toState Destination state.
     */
    public Tuple(State fromState, Transition activity, State toState)
    {
        setFromState(fromState);
        setActivity(activity);
        setToState(toState);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the activity associated with this tuple.
     * 
     * @return Transition
     */
    public Transition getActivity()
    {
        return activity_;
    }


    /**
     * Sets the activity associated with this tuple.
     * 
     * @param activity Transition activity.
     */
    public void setActivity(Transition activity)
    {
        activity_ = activity;
    }


    /**
     * Returns the state of origin. 
     * 
     * @return State
     */
    public State getFromState()
    {
        return fromState_;
    }


    /**
     * Sets the state of origin.
     * 
     * @param fromState State from which the transition originates.
     */
    public void setFromState(State fromState)
    {
        fromState_ = fromState;
    }


    /**
     * Returns the destination state of the transition.
     * 
     * @return State
     */
    public State getToState()
    {
        return toState_;
    }


    /**
     * Sets the destination state of thei transition.
     * 
     * @param toState Destination state.
     */
    public void setToState(State toState)
    {
        toState_ = toState;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Delegates equals() to each individual member of the tuple: fromState, 
     * transition, and toState.
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
        
        Tuple t = (Tuple) obj;
        
        return getFromState().equals(t.getFromState()) &&
               getToState().equals(t.getToState()) &&
               getActivity().equals(t.getActivity());
    }    
}