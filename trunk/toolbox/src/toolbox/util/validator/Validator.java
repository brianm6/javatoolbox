package toolbox.util.validator;

/**
 * The Validator interface specificies the interface necessary to execute a
 * validation process. Validation steps include:
 * <ol>
 *   <li>Building an constraint to validate an entity.
 *   <li>Evaluating the constraint to perform the validation.
 * </ol>
 * 
 * @see toolbox.util.validator.ValidatorContext
 * @see toolbox.util.validator.ValidatorConstraint
 */
public interface Validator
{
    /**
     * Builds the validation constraint and stores it in the context via
     * {@link ValidatorContext#setConstraint(Predicate)}.
     * 
     * @param context Context which contains all participants necessary to build
     *        the validation constraint.
     */
    void build(ValidatorContext context);


    /**
     * Executes the validation constraint and gathers the results in the 
     * context. The result of the validation is available via
     * {@link ValidatorContext#isValid()}. Failures can be retrieved by
     * {@link ValidatorContext#getFailures()}and warnings are accessible by
     * {@link ValidatorContext#getWarnings()}.
     * 
     * @param context Context which contains all participants necessary to
     *        evaluate the validation constraint.
     */
    void validate(ValidatorContext context);
}