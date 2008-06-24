
package toolbox.util.statemachine;

import java.util.List;

import toolbox.util.service.Nameable;

/**
 * A StateMachine simulates the activity of a deterministic finite state 
 * automata with any number of {@link State}s and the {@link Transition}s 
 * between them. Instances can be created via the {@link StateMachineFactory} 
 * and activity can be monitored by implementing the 
 * {@link StateMachineListener} interface.
 * <p>
 * <b>Example</b>
 * <pre class="snippet">
 *   
 *   // Lets simulate a rice cooker..
 *   StateMachine cooker  = StateMachineFactory.createStateMachine("MyCooker");
 *   State raw            = StateMachineFactory.createState("raw");
 *   State cooked         = StateMachineFactory.createState("cooked");
 *   Transition cookFood  = StateMachineFactory.createTransition("cookFood");
 *   Transition buyFood   = StateMachineFactory.createSTate("buyFood");
 *   CookerListener listener = new StateMachineListener() { ... };  
 * 
 *   cooker.addState(raw);
 *   cooker.addState(cooked);
 *   cooker.addTransition(cookFood, raw, cooked);
 *   cooker.addStateMachineListener(listener);
 *   cooker.setBeginState(raw);
 * 
 *   // Start from the beginning
 *   cooker.reset();  
 * 
 *   // Get cookin'
 *   cooker.transition(cookFood);
 *  
 *   // Ready to eat...
 *   System.out.println("Food should be cooked: " + cooker.getState());
 * 
 *   // Actively check transitions..
 *   if (!cooker.canTransition(buyFood))
 *      System.out.println("Bingo..buy comes before cooking!");
 *  
 *   // Passively check transitions..
 *   try {
 *       cooker.checkTransition(buyFood);     
 *   }
 *   catch (IllegalStateException ise) {
 *       System.out.println("Bingo..buy comes before cooking!");
 *   }
 * 
 *   // Do something with those events...
 *   System.out.println("Meal cooked in " + listener.getCookingTime());
 *   
 * </pre>
 */
public interface StateMachine extends Nameable {
    
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
     * Returns a list of all the Transitions registered with this StateMachine
     * in no particular order.
     *  
     * @return List<Transition>
     */
    List getTransitions();

    
    /**
     * Returns a list of all the Transitions from the given state.
     *  
     * @return List<Transition>
     */
    List getTransitionsFrom(State state);
    
    
    /**
     * Returns a list of all the Transitions to the given state.
     *  
     * @return List<Transition>
     */
    List getTransitionsTo(State state);
    
    
    /**
     * Returns the current state of this state machine.
     * 
     * @return State
     */
    State getState();


    /**
     * Returns a list of all States this state machine.
     * 
     * @return List<State>
     */
    List getStates();

    
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
     * Resets this state machine to the begin state.
     */
    void reset();

    
    /**
     * Returns true if the given transition exists from the current state to a 
     * new state, false otherwise.
     * 
     * @return boolean
     */
    boolean canTransition(Transition transition);

    
    /**
     * Checks if a transition from the current state to the given state is
     * is valid.
     *  
     * @param transition Transition to check.
     * @throws IllegalStateException if the transition is not valid.
     */
    void checkTransition(Transition transition) throws IllegalStateException;
    
    
    /**
     * Adds a listener to this state machine.
     * 
     * @param listener StateMachineListener to add.
     */
    void addStateMachineListener(StateMachineListener listener);


    /**
     * Removes a listener from this state machine.
     * 
     * @param listener StateMachineListener to remove.
     */
    void removeStateMachineListener(StateMachineListener listener);
}