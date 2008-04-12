package toolbox.util.validator.functional;

/**
 * BabyBellPhoneNumber is an extension of a generic phone number.
 */
public class BabyBellPhoneNumber extends PhoneNumber
{
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Returns the area code portion of this phone number.
     * 
     * @return String
     */
    public String getAreaCode()
    {
        return getNumber().substring(0, 3);
    }
}
