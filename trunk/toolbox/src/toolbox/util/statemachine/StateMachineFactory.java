package toolbox.util.statemachine;

import toolbox.util.statemachine.impl.DefaultState;
import toolbox.util.statemachine.impl.DefaultStateMachine;
import toolbox.util.statemachine.impl.DefaultTransition;

/**
 * StateMachineFactory returns default implemenations of the various state
 * machine classes.
 */
public class StateMachineFactory
{
    /**
     * Returns a default implementation of a State.
     * 
     * @param name Name of the state.
     * @return State
     */
    public static final State createState(String name)
    {
        return new DefaultState(name);
    }

    
    /**
     * Retruns a default implementation of a Transition.
     * 
     * @param name Name of the transition.
     * @return Transition
     */
    public static final Transition createTransition(String name)
    {
        return new DefaultTransition(name);
    }
    
    
    /**
     * Retruns a default implementation of a StateMachine.
     * 
     * @param name Name of the state machine.
     * @return StateMachine
     */
    public static final StateMachine createStateMachine(String name)
    {
        return new DefaultStateMachine(name);
    }
}