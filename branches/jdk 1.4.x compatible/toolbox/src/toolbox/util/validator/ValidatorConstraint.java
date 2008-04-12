package toolbox.util.validator;

import org.apache.commons.collections.Predicate;

/**
 * A constraint is evaluated to determine the validity of an arbitrary entity.
 * A common sequence of events for the implementor of a ValidationConstraint
 * includes:
 * <ul>
 *   <li>Lookup and retrieve participant objects from the context.
 *   <li>Use the participant objects to perform some sort of validation.
 *   <li>If a failure occurs, add the failure to the context.
 *   <li>If a warning occurs, add the warning to the context.
 * </ul>
 */
public interface ValidatorConstraint extends Predicate
{
    /**
     * Validates this constraint and stores any resulting failure or warnings in
     * the given context.
     * 
     * @param context Context from which to retrieve participants that are
     *        involved in the evaluation of this constraint.
     */
    void validate(ValidatorContextIfc context);
}