package toolbox.util.validator.functional;

import toolbox.util.validator.ValidatorContext;

/**
 * Validator context for validating a phone number.
 */
public class PhoneNumberValidatorContext extends ValidatorContext
{
    //--------------------------------------------------------------------------
    // Keys
    //--------------------------------------------------------------------------

    /**
     * Key of the phone number participant.
     */
    public static final String KEY_NUMBER = "number";

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a PhonNumberValidatorContext.
     */
    public PhoneNumberValidatorContext()
    {
    }

    
    /**
     * Creates a PhoneNumberValidatorContext.
     * 
     * @param number Phone number.
     */
    public PhoneNumberValidatorContext(IPhoneNumber number)
    {
        setNumber(number);
    }

    // --------------------------------------------------------------------------
    // Public
    // --------------------------------------------------------------------------

    /**
     * Convenience method to get the phone number participant.
     * 
     * @return IPhoneNumber
     */
    public IPhoneNumber getNumber()
    {
        return (IPhoneNumber) getParticipant(KEY_NUMBER);
    }


    /**
     * Convenience method to set the phone number participant.
     * 
     * @param number Phone number to add as a participant.
     */
    public void setNumber(IPhoneNumber number)
    {
        addParticipant(KEY_NUMBER, number);
    }
}