package toolbox.util.validator;

/**
 * AbstractValidatorConstraint is an abstract implementation of
 * {@link ValidatorConstraint} that assumes responsiblity of implementing the 
 * failfast feature during the validation process.
 */
public abstract class AbstractValidatorConstraint implements ValidatorConstraint
{
    //--------------------------------------------------------------------------
    // Predicate Interface
    //--------------------------------------------------------------------------

    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    public final boolean evaluate(Object object)
    {
        ValidatorContextIfc context = (ValidatorContextIfc) object;
        validate(context);

        // Terminate evaluation of the expression if failfast is turned on and
        // we also have an invalid context...
        if (context.isFailFast() && !context.isValid())
            return false;
        else
            return true;
    }
}