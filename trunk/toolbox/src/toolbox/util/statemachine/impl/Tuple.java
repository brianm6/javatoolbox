package toolbox.util.statemachine.impl;

import toolbox.util.statemachine.State;
import toolbox.util.statemachine.Transition;


public class Tuple
{
    private State fromState_;
    private Transition activity_;
    private State toState_;


    /**
     * Creates a Tuple.
     * 
     * @param fromState
     * @param activity
     * @param toState
     */
    public Tuple(State fromState, Transition activity, State toState)
    {
        fromState_ = fromState;
        activity_ = activity;
        toState_ = toState;
    }


    public Transition getActivity()
    {
        return activity_;
    }


    public void setActivity(Transition activity)
    {
        activity_ = activity;
    }


    public State getFromState()
    {
        return fromState_;
    }


    public void setFromState(State fromState)
    {
        fromState_ = fromState;
    }


    public State getToState()
    {
        return toState_;
    }


    public void setToState(State toState)
    {
        toState_ = toState;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
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