package toolbox.util.statemachine;

import toolbox.util.service.Nameable;

/**
 * A State represents the starting or ending point of zero or more
 * {@link Transition}s in a {@link StateMachine}. This is simply a marker
 * interface used to 'tag' classes that represent unique states. Default
 * implementations can be created via the {@link StateMachineFactory}.
 */
public interface State extends Nameable{
}