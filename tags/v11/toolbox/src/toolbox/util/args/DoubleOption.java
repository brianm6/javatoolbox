package toolbox.util.args;

import java.util.Locale;

/**
 * An option that expects a floating-point value
 */
public class DoubleOption extends Option
{
    
    public DoubleOption(char shortForm, String longForm)
    {
        super(shortForm, longForm, true);
    }
    
    
    protected Object parseValue(String arg, Locale locale)
        throws IllegalOptionValueException
    {
        try
        {
            return new Double(arg);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalOptionValueException(this, arg);
        }
    }
}