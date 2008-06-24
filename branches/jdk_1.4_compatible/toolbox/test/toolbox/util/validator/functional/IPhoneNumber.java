package toolbox.util.validator.functional;

/**
 * Phone number domain interface.
 */
public interface IPhoneNumber
{
    /**
     * Returns the text of the phone number.
     * 
     * @return String
     */
    String getNumber();


    /**
     * Sets the text of the phone number.
     * 
     * @param number Phone number.
     */
    void setNumber(String number);
}