package toolbox.util.statemachine.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import toolbox.util.statemachine.State;
import toolbox.util.statemachine.StateMachine;
import toolbox.util.statemachine.StateMachineListener;
import toolbox.util.statemachine.Transition;

/**
 * Default implementation of a StateMachine.
 */
public class DefaultStateMachine implements StateMachine
{
    private static final Logger logger_ = 
        Logger.getLogger(DefaultStateMachine.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * List of listeners interested in listening to state transitions.
     */
    private List listeners_;
    
    /**
     * Friendly name to identify this state machine.
     */
    private String name_;
    
    /**
     * Begin state of this state machine.
     */
    private State beginState_;
    
    /**
     * Current state of this state machine.
     */
    private State currentState_;
    
    /**
     * The state of this state machine before the last transition.
     */
    private State previousState_;
    
    /**
     * The transition that resulted in the current state.
     */
    private Transition lastTransition_;
    
    /**
     * All known registered states.
     */
    private List states_;
    
    /**
     * Maps original state -> Collection(transitions)
     */
    //private MultiMap fromStates_;
    
    /**
     * Maps transition -> target state
     */
    //private MultiMap toStates_;
    
    private MultiKeyMap stateMap_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DefaultStateMachine.
     */
    public DefaultStateMachine()
    {
        this("");
    }

    
    /**
     * Creates a DefaultStateMachine with the given name.
     * 
     * @param name Name of the state machine.
     */
    public DefaultStateMachine(String name)
    {
        setName(name);
        listeners_  = new ArrayList(1);
        states_     = new ArrayList();
        //fromStates_ = new MultiHashMap();
        //toStates_   = new MultiHashMap();
        stateMap_   = new MultiKeyMap();
    }
    
    //--------------------------------------------------------------------------
    // StateMachine Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.statemachine.StateMachine#setBeginState(
     *      toolbox.util.statemachine.State)
     */
    public void setBeginState(State state)
    {
        // Validate state exists
        Validate.isTrue(states_.contains(state), 
            "State '" 
            + state.getName() 
            + "' does not exist in state machine '" 
            + getName() 
            + "'.");
    
        beginState_ = state;
    }
    
    
    /**
     * @see toolbox.util.statemachine.StateMachine#addState(
     *      toolbox.util.statemachine.State)
     */
    public void addState(State state)
    {
        Validate.isTrue(!states_.contains(state), 
            "State '" 
            + state.getName() 
            + "' already exists in state machine '" 
            + getName() 
            + "'.");
        
        states_.add(state);
    }


    /**
     * @see toolbox.util.statemachine.StateMachine#addTransition(
     *      toolbox.util.statemachine.Transition,
     *      toolbox.util.statemachine.State, toolbox.util.statemachine.State)
     */
    public void addTransition(
        Transition transition, State fromState, State toState)
    {
        // Verify fromState exists
        
        Validate.isTrue(states_.contains(fromState), 
            "Adding transition '"
            + transition.getName() 
            + "' failed. State '" 
            + fromState.getName() 
            + "' does not exist in state machine '" 
            + getName() 
            + "'.");

        // Verify toState exists
        Validate.isTrue(states_.contains(toState),
            "Adding transition '"
            + transition.getName() 
            + "' failed. State '" 
            + toState.getName() 
            + "' does not exist in state machine '" 
            + getName() 
            + "'.");
        
        // Verify transition between the two states doesn't already exist.
        Validate.isTrue(!stateMap_.containsKey(fromState, transition),
            "Transition '"
            + transition.getName() 
            + "' from state '"
            + fromState.getName()
            + "' to state '"
            + toState.getName() 
            + "' already exists.");
        
        stateMap_.put(fromState, transition, toState);
        //fromStates_.put(fromState, transition);
        //toStates_.put(transition, toState);
    }

    
    /**
     * @see toolbox.util.statemachine.StateMachine#transition(
     *      toolbox.util.statemachine.Transition)
     */
    public State transition(Transition stimulus)
    {
        State targetState = (State) stateMap_.get(currentState_, stimulus); 
       
        // No transitions found for the given stimulus
        Validate.notNull(targetState,
            "No transitions exist from state '" 
            + currentState_.getName() 
            + "' using transition '"
            + stimulus
            + "'.");
 
        // We have a state change!!
        previousState_ = currentState_;
        currentState_ = targetState;
        lastTransition_ = stimulus;
        fireStateChanged();
        return currentState_;
    }

    
    /**
     * @see toolbox.util.statemachine.StateMachine#canTransition(
     *      toolbox.util.statemachine.Transition)
     */
    public boolean canTransition(Transition transition)
    {
        return stateMap_.containsKey(currentState_, transition);
    }
    
    
    /**
     * @see toolbox.util.statemachine.StateMachine#reset()
     */
    public void reset()
    {
        currentState_ = beginState_;
        lastTransition_ = null;
        previousState_ = null;
    }
    
    /**
     * @see toolbox.util.statemachine.StateMachine#getState()
     */
    public State getState()
    {
        return currentState_;
    }


    /**
     * @see toolbox.util.statemachine.StateMachine#getLastTransition()
     */
    public Transition getLastTransition()
    {
        return lastTransition_;
    }


    /**
     * @see toolbox.util.statemachine.StateMachine#getPreviousState()
     */
    public State getPreviousState()
    {
        return previousState_;
    }


    /**
     * @see toolbox.util.statemachine.StateMachine#addStateMachineListener(
     *      toolbox.util.statemachine.StateMachineListener)
     */
    public void addStateMachineListener(StateMachineListener listener)
    {
        listeners_.add(listener);
    }


    /**
     * @see toolbox.util.statemachine.StateMachine#removeStateMachineListener(
     *      toolbox.util.statemachine.StateMachineListener)
     */
    public void removeStateMachineListener(StateMachineListener listener)
    {
        listeners_.remove(listener);
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
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Notifies listeners of change in state.
     */
    protected void fireStateChanged()
    {
        for (Iterator iter = listeners_.iterator(); iter.hasNext();)
        {
            StateMachineListener listener = (StateMachineListener) iter.next();
            listener.stateChanged(this);
        }
    }
}