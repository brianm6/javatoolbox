package toolbox.util.validator;

import org.apache.commons.collections.Predicate;

/**
 * AbstractValidator is an abstract implementation of a {@link Validator}that
 * makes the validation process more convenient by building and validating the
 * constraint in a single step.
 */
public abstract class AbstractValidator implements Validator
{
    //--------------------------------------------------------------------------
    // Validator Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.validator.Validator#validate(
     *      toolbox.util.validator.ValidatorContext)
     */
    public final void validate(ValidatorContext context)
    {
        // Build the constraint fully...
        build(context);

        // Evaluate the constraint...
        Predicate rule = context.getConstraint();
        boolean b = rule.evaluate(context);

        // Return value not important since the answer to validity is in
        // the context.
    }
}