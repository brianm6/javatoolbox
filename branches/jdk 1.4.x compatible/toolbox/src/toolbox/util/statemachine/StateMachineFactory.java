package toolbox.util.statemachine;

import toolbox.util.statemachine.impl.DefaultState;
import toolbox.util.statemachine.impl.DefaultStateMachine;
import toolbox.util.statemachine.impl.DefaultTransition;

/**
 * StateMachineFactory is able to create default implementations of
 * {@link StateMachine}, {@link State}, and {@link Transition}.
 */
public class StateMachineFactory {
    
    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Prevent construction of this static singleton.
     */
    private StateMachineFactory(){
    }

    // --------------------------------------------------------------------------
    // Public
    // --------------------------------------------------------------------------

    /**
     * Returns a default implementation of a State.
     * 
     * @param name Name to assign to the state.
     * @return State
     */
    public static final State createState(String name){
        return new DefaultState(name);
    }


    /**
     * Returns a default implementation of a Transition.
     * 
     * @param name Name to assign to the transition.
     * @return Transition
     */
    public static final Transition createTransition(String name){
        return new DefaultTransition(name);
    }


    /**
     * Returns a default implementation of a StateMachine.
     * 
     * @param name Name to assign to the StateMachine.
     * @return StateMachine
     */
    public static final StateMachine createStateMachine(String name){
        return new DefaultStateMachine(name);
    }
}