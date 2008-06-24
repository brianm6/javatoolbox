package toolbox.util.validator.functional;

import org.apache.commons.collections.PredicateUtils;

import toolbox.util.validator.AbstractValidator;
import toolbox.util.validator.ValidatorContextIfc;

/**
 * Generic validator for a phone number. Currently only validates a phone
 * number's length is correct.
 */
public class PhoneNumberValidator extends AbstractValidator
{
    //--------------------------------------------------------------------------
    // Validator Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.validator.Validator#build(
     *      toolbox.util.validator.ValidatorContextIfc)
     */
    public void build(ValidatorContextIfc context)
    {
        // Add the length constraint to the existing constraint.
        context.setConstraint(
            PredicateUtils.andPredicate(
                context.getConstraint(), 
                new PhoneNumberLengthConstraint()));
    }
}