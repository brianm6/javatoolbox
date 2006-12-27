package toolbox.util.validator.functional;

import org.apache.log4j.Logger;

import toolbox.util.validator.AbstractValidatorConstraint;
import toolbox.util.validator.ValidatorContextIfc;

/**
 * Constraint that verifies the area code in a phone number.
 */
public class BabyBellAreaCodeConstraint extends AbstractValidatorConstraint
{
    private static final Logger logger_ = 
        Logger.getLogger(BabyBellAreaCodeConstraint.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public BabyBellAreaCodeConstraint()
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
        // logger.debug("Running BabyBellAreaCodeConstraint::validate()");

        BabyBellPhoneNumber number = (BabyBellPhoneNumber) 
            context.getParticipant(PhoneNumberValidatorContext.KEY_NUMBER);

        if (!number.getAreaCode().equals("972"))
        {
            context.addFailure("Phone number area code is not 972!");
        }

        // context.addWarning("this is a bogus warning");
    }
}