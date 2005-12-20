package toolbox.util.statemachine.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.Validate;

import toolbox.util.statemachine.State;
import toolbox.util.statemachine.StateMachine;
import toolbox.util.statemachine.StateMachineListener;
import toolbox.util.statemachine.Transition;

/**
 * Basic implementation of a {@link StateMachine}.
 * 
 * @see toolbox.util.statemachine.StateMachineFactory
 */
public class DefaultStateMachine implements StateMachine {
    
    // TODO: How to handle a state transition where the current and target 
    //       states are the same? What to do if trying to stop a machine that
    //       is already stopped?
    //
    //       1. warning - hey you can't do that but it doesn't generate an
    //                    exception
    //       2. allow flag in state machine to ignore these requests
    //       3. allow flag in state machine to ignore these requests with a warning
    
    // TODO: How to handle automatic state transition for convenience sake?
    //       
    //       For given machine: [SUSPENDED]-->resume-->[RUNNING]-->stop-->[STOPPED]-->destroy-->[DESTROYED]
    //
    //       Given current state = SUSPENDED
    // 
    //       Request transition = DESTROY
    //
    //       What should the state machine do? 
    //
    //       1. Currently, transiton is not supported and exception is thrown
    //       2. Have the machine automatically resume and stop to get to destroy
    //          Is this really the responsibility of the machine or a smart agent
    //          that enhances the operation of the machine? Decorate the machine???
    //       
    
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * List of listeners interested state machine generated events.
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
     * State of this machine before the last transition.
     */
    private State previousState_;
    
    /**
     * Transition that resulted in the change to the current state.
     */
    private Transition lastTransition_;
    
    /**
     * All known registered states.
     */
    private List states_;

    /**
     * Maps (fromState, transition) --> (toState)
     */
    private MultiKeyMap fromStateMap_;
    
    /**
     * Set of (fromState, transition, toState) tuples that models the graph of
     * the state machine.
     */
    private Set tuples_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DefaultStateMachine.
     */
    public DefaultStateMachine() {
        this("");
    }

    
    /**
     * Creates a DefaultStateMachine with the given name.
     * 
     * @param name Name of the state machine.
     */
    public DefaultStateMachine(String name) {
        setName(name);
        listeners_    = new ArrayList(1);
        states_       = new ArrayList();
        fromStateMap_ = new MultiKeyMap();
        tuples_       = new HashSet();
    }
    
    //--------------------------------------------------------------------------
    // StateMachine Interface
    //--------------------------------------------------------------------------
    
    /**
     * Resets the state machine to the begin state. The previous state and last
     * transition are set to null.
     * 
     * @see toolbox.util.statemachine.StateMachine#reset()
     */
    public void reset() {
        currentState_   = beginState_;
        previousState_  = null;
        lastTransition_ = null;
        fireMachineReset();
    }
    
    
    /*
     * @see toolbox.util.statemachine.StateMachine#setBeginState(toolbox.util.statemachine.State)
     */
    public void setBeginState(State state) {
        // Validate state exists
        Validate.isTrue(states_.contains(state), 
            "State '" 
            + state.getName() 
            + "' does not exist in state machine '" 
            + getName() 
            + "'.");
        
        beginState_ = state;
    }
    
    
    /*
     * @see toolbox.util.statemachine.StateMachine#addState(toolbox.util.statemachine.State)
     */
    public void addState(State state) {
        Validate.isTrue(!states_.contains(state), 
            "State '" 
            + state.getName() 
            + "' already exists in state machine '" 
            + getName() 
            + "'.");
        
        states_.add(state);
    }


    /*
     * @see toolbox.util.statemachine.StateMachine#addTransitiontoolbox.util.statemachine.Transition, toolbox.util.statemachine.State, toolbox.util.statemachine.State)
     */
    public void addTransition(
        Transition transition, 
        State fromState, 
        State toState) {
        
        // Verify fromState exists ---------------------------------------------
        
        Validate.isTrue(states_.contains(fromState), 
            "Adding transition '"
            + transition.getName() 
            + "' failed. State '" 
            + fromState.getName() 
            + "' does not exist in state machine '" 
            + getName() 
            + "'.");

        // Verify toState exists -----------------------------------------------
        
        Validate.isTrue(states_.contains(toState),
            "Adding transition '"
            + transition.getName() 
            + "' failed. State '" 
            + toState.getName() 
            + "' does not exist in state machine '" 
            + getName() 
            + "'.");
        
        // Verify transition between the two states doesn't already exist ------
        
        Validate.isTrue(!fromStateMap_.containsKey(fromState, transition),
            "Transition '"
            + transition.getName() 
            + "' from state '"
            + fromState.getName()
            + "' to state '"
            + toState.getName() 
            + "' already exists.");

        fromStateMap_.put(fromState, transition, toState);
        tuples_.add(new Tuple(fromState, transition, toState));
    }

    
    /*
     * @see toolbox.util.statemachine.StateMachine#transition(toolbox.util.statemachine.Transition)
     */
    public State transition(Transition stimulus) {
        checkTransition(stimulus);

        State targetState = (State) fromStateMap_.get(currentState_, stimulus);
        previousState_ = currentState_;
        currentState_ = targetState;
        lastTransition_ = stimulus;
        fireStateChanged();
        return currentState_;
    }


    /*
     * @see toolbox.util.statemachine.StateMachine#canTransition(toolbox.util.statemachine.Transition)
     */
    public boolean canTransition(Transition transition) {
        return fromStateMap_.containsKey(currentState_, transition);
    }

    
    /*
     * @see toolbox.util.statemachine.StateMachine#checkTransition(toolbox.util.statemachine.Transition)
     */
    public void checkTransition(Transition transition)
        throws IllegalStateException {
        if (!canTransition(transition))
            throw new IllegalStateException(
                "No transitions exist from state '" 
                + currentState_.getName() 
                + "' using transition '"
                + transition
                + "'.");
    }
    
    
    /*
     * @see toolbox.util.statemachine.StateMachine#getStates()
     */
    public List getStates() {
        Transformer fromTrans = 
            TransformerUtils.invokerTransformer("getFromState");
        
        Transformer toTrans = 
            TransformerUtils.invokerTransformer("getToState");
      
        
//        ChainedTransformer stateTrans = 
//            new ChainedTransformer(new Transformer[] {
//                fromTrans, 
//                toTrans
//            });
        
        Set toStates = new HashSet(tuples_);
        CollectionUtils.transform(toStates, toTrans);
        
        Set fromStates = new HashSet(tuples_);
        CollectionUtils.transform(fromStates, fromTrans);
        
        toStates.addAll(fromStates);
        return new ArrayList(toStates);
    }

    
    /*
     * @see toolbox.util.statemachine.StateMachine#getTransitions()
     */
    public List getTransitions() {
        Set transitions = new HashSet(tuples_);
        
        CollectionUtils.transform(
            transitions, 
            TransformerUtils.invokerTransformer("getActivity"));
        
        return new ArrayList(transitions);
    }
    
    
    /*
     * @see toolbox.util.statemachine.StateMachine#getTransitionsFrom(toolbox.util.statemachine.State)
     */
    public List getTransitionsFrom(final State state) {
        
        Transformer t = new Transformer() {
            
            public Object transform(Object input) {
                Transition result = null;
                Tuple tuple = (Tuple) input;
                
                if (tuple.getFromState().equals(state))
                    result = tuple.getActivity();
                
                return result;
            }
        };
        
        Set fromTransitions = new HashSet(tuples_);
        CollectionUtils.transform(fromTransitions, t);
        return new ArrayList(fromTransitions);
    }
    
    
    /*
     * @see toolbox.util.statemachine.StateMachine#getTransitionsTo(toolbox.util.statemachine.State)
     */
    public List getTransitionsTo(final State state) {
        
        Transformer t = new Transformer() {
            
            public Object transform(Object input) {
                Transition result = null;
                Tuple tuple = (Tuple) input;
                
                if (tuple.getToState().equals(state))
                    result = tuple.getActivity();
                
                return result;
            }
        };
        
        Set toTransitions = new HashSet(tuples_);
        CollectionUtils.transform(toTransitions, t);
        return new ArrayList(toTransitions);
    }
    
    
    /*
     * @see toolbox.util.statemachine.StateMachine#getState()
     */
    public State getState() {
        return currentState_;
    }


    /*
     * @see toolbox.util.statemachine.StateMachine#getLastTransition()
     */
    public Transition getLastTransition() {
        return lastTransition_;
    }


    /*
     * @see toolbox.util.statemachine.StateMachine#getPreviousState()
     */
    public State getPreviousState() {
        return previousState_;
    }


    /*
     * @see toolbox.util.statemachine.StateMachine#addStateMachineListener(toolbox.util.statemachine.StateMachineListener)
     */
    public void addStateMachineListener(StateMachineListener listener) {
        listeners_.add(listener);
    }


    /*
     * @see toolbox.util.statemachine.StateMachine#removeStateMachineListener(toolbox.util.statemachine.StateMachineListener)
     */
    public void removeStateMachineListener(StateMachineListener listener) {
        listeners_.remove(listener);
    }
    
    //--------------------------------------------------------------------------
    // Nameable Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName() {
        return name_;
    }

    
    /*
     * @see toolbox.util.service.Nameable#setName(java.lang.String)
     */
    public void setName(String name) {
        name_ = name;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Notifies listeners of a change in state.
     */
    protected void fireStateChanged() {
        for (Iterator iter = listeners_.iterator(); iter.hasNext();) {
            StateMachineListener listener = (StateMachineListener) iter.next();
            listener.stateChanged(this);
        }
    }
    
    
    /**
     * Notifies listeners of a machine reset.
     */
    protected void fireMachineReset() {
        for (Iterator iter = listeners_.iterator(); iter.hasNext();) {
            StateMachineListener listener = (StateMachineListener) iter.next();
            listener.machineReset(this);
        }
    }
}