package toolbox.util.validator;

/**
 * The Validator interface specificies the contract for a generic two step
 * validator. The first step involves building a plan to validate an entity and
 * the second is the execution of the validation.
 * 
 * @see toolbox.util.validator.ValidatorContext
 * @see toolbox.util.validator.ValidatorConstraint
 */
public interface Validator
{
    /**
     * Builds the validation plan and stores it in the context.
     * 
     * @param context Context which contains all participants necessary to build
     *        the validation plan.
     */
    void build(ValidatorContext context);


    /**
     * Executes the validation plan and stores the result in the context. The
     * result of the validation is available via
     * {@link ValidatorContext#isValid()}. Failures can be retrieved by
     * {@link ValidatorContext#getFailures()}and warnings are accessible by
     * {@link ValidatorContext#getWarnings()}.
     * 
     * @param context Context which contains all participants necessary to
     *        execute the validation plan.
     */
    void validate(ValidatorContext context);
}