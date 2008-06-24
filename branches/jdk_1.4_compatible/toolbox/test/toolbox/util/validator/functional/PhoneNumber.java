package toolbox.util.validator.functional;

/**
 * PhoneNumber domain object.
 */
public class PhoneNumber implements IPhoneNumber
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Text of the phone number_.
     */
    private String number_;

    //--------------------------------------------------------------------------
    // IPhoneNumber Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.validator.functional.IPhoneNumber#getNumber()
     */
    public String getNumber()
    {
        return number_;
    }


    /**
     * @see toolbox.util.validator.functional.IPhoneNumber#setNumber(
     *      java.lang.String)
     */
    public void setNumber(String number)
    {
        number_ = number;
    }
}