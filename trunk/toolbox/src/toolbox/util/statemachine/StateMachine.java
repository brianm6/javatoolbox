
package toolbox.util.statemachine;

import toolbox.util.service.Nameable;

/**
 * A StateMachine adheres to the behavior of a deterministic finite state 
 * automata.
 */
public interface StateMachine extends Nameable
{
    /**
     * Sets the initial state of this state machine. This given state must
     * already have been added to the state machine.
     * 
     * @param state Initial state.
     */
    void setBeginState(State state);
    
    
    /**
     * Adds the given state to this StateMachine.
     * <p>
     * A state may only be added once to a given state machine.
     * 
     * @param state State to add to the state machine.
     * @throws IllegalArgumentException if the state has already been added.
     */
    void addState(State state);

    
    /**
     * Adds a transition between the two given states to this state machine.
     * <p>
     * A transition must be unique given the pair of states (s1, s2)
     * 
     * @param transition Transition linking the two states.
     * @param fromState State from which the transition starts.
     * @param toState State at which the transition ends.
     * @throws IllegalArgumentException if a transition between the states 
     *         already exists.
     */
    void addTransition(Transition transition, State fromState, State toState);


    /**
     * Executes a transition from the current state using the given stimulus.
     * 
     * @param stimulus Activity inducing the machine to change state.
     * @return The state resulting from the evaluation of the stimulus.
     * @throws IllegalStateException if the stimulus is not a valid
     *         transition from the current state.
     */
    State transition(Transition stimulus) throws IllegalStateException;

    
    /**
     * Returns the current state of this state machine.
     * 
     * @return State
     */
    State getState();


    /**
     * Returns the last known transition that affected this state machine.
     * 
     * @return Transition
     */
    Transition getLastTransition();


    /**
     * Returns the previous state of this state machine.
     * 
     * @return State
     */
    State getPreviousState();
    

    /**
     * Resets the state machine to the begin state.
     */
    void reset();

    
    /**
     * Returns true if the given transition exists from the current state to a 
     * new state, false otherwise.
     */
    boolean canTransition(Transition transition);

    
    /**
     * Adds a listener to this state machine.
     * 
     * @param listener Implementor of StateMachineListener to add as a listener.
     */
    void addStateMachineListener(StateMachineListener listener);


    /**
     * Removes a listener from this state machine.
     * 
     * @param listener Implementor of StateMachineListener to remove.
     */
    void removeStateMachineListener(StateMachineListener listener);


    /**
     * @param transition
     */
    void checkTransition(Transition transition) throws IllegalStateException;
    
}