package toolbox.util.args;

import java.util.Locale;

/**
 * An option that expects a string value
 */
public class StringOption extends Option
{
    public StringOption(char shortForm, String longForm)
    {
        super(shortForm, longForm, true);
    }
    
    protected Object parseValue(String arg, Locale locale)
    {
        return arg;
    }
}