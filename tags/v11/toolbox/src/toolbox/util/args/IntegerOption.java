package toolbox.util.args;

import java.util.Locale;

/**
 * An option that expects an integer value
 */
public class IntegerOption extends Option
{
    public IntegerOption(char shortForm, String longForm)
    {
        super(shortForm, longForm, true);
    }
    
    protected Object parseValue(String arg, Locale locale)
        throws IllegalOptionValueException
    {
        try
        {
            return new Integer(arg);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalOptionValueException(this, arg);
        }
    }
}
