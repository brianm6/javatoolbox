package toolbox.util.validator.functional;

import org.apache.log4j.Logger;

import toolbox.util.validator.AbstractValidatorConstraint;
import toolbox.util.validator.ValidatorContextIfc;

/**
 * Constraint that validates the length of a phone number is correct.
 */
public class PhoneNumberLengthConstraint extends AbstractValidatorConstraint
{
    private static final Logger logger_ = 
        Logger.getLogger(PhoneNumberLengthConstraint.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public PhoneNumberLengthConstraint()
    {
    }

    //--------------------------------------------------------------------------
    // ValidatorConstraint Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.validator.ValidatorConstraint#validate(
     *      toolbox.util.validator.ValidatorContextIfc)
     */
    public void validate(ValidatorContextIfc context)
    {
        // logger.info("Running PhoneNumberLengthConstraint::validate()");

        IPhoneNumber number = null;

        if (context instanceof PhoneNumberValidatorContext)
        {
            PhoneNumberValidatorContext phoneContext = 
                (PhoneNumberValidatorContext) context;
            
            number = phoneContext.getNumber();
        }
        else
        {
            number = (IPhoneNumber) 
                context.getParticipant(PhoneNumberValidatorContext.KEY_NUMBER);
        }

        if (!(number.getNumber().length() == 12))
            context.addFailure("Phone number length is invalid.");
    }
}