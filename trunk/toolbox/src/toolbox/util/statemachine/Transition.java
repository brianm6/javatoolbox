
package toolbox.util.statemachine;

import toolbox.util.service.Nameable;

/**
 * A Transition describes an activity that causes a {@link State} change in a 
 * {@link StateMachine}. This is simply a marker interface used to 'tag' classes
 * that represent unique transitions.
 */
public interface Transition extends Nameable
{
}