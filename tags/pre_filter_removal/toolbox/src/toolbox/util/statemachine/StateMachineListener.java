package toolbox.util.statemachine;

import toolbox.util.statemachine.impl.DefaultStateMachine;

/**
 * StateMachineListener provides a notification interface for events generated
 * by a {@link StateMachine}.
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


    /**
     * Notifiction that the machine has been reset back to its begin state.
     * 
     * @param machine Originating state machine.
     */
    public void machineReset(DefaultStateMachine machine);
}