package toolbox.util.statemachine;

/**
 * StateMachineListener provides a notification interface for events generated
 * by a StateMachine.
 * 
 * @see toolbox.util.statemachine.StateMachine
 */
public interface StateMachineListener
{
    /**
     * Notification that the state of the machine has changed.
     *  
     * @param machine Originating state machine.
     */
    public void stateChanged(StateMachine machine);


    /**
     * Notification that the machine has reached a terminal state.
     * 
     * @param machine Originating state machine.
     */
    public void terminalState(StateMachine machine);
}