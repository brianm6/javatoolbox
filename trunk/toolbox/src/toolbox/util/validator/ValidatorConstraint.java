package toolbox.util.validator;

import org.apache.commons.collections.Predicate;

/**
 * Interface that defines the contract for the interface of a constraint.
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
    void validate(ValidatorContext context);
}