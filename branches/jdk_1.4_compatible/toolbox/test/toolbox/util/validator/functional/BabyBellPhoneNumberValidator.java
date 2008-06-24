package toolbox.util.validator.functional;

import org.apache.commons.collections.PredicateUtils;

import toolbox.util.validator.ValidatorContextIfc;

/**
 * Specialized validator for baby bell phone numbers.
 */
public class BabyBellPhoneNumberValidator extends PhoneNumberValidator
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
        // Let the superclass build the expression first
        super.build(context);

        // Slap the area code constraint onto the end of the expression
        context.setConstraint(
            PredicateUtils.andPredicate(
                context.getConstraint(), 
                new BabyBellAreaCodeConstraint()));
    }
}