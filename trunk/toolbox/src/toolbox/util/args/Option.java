package toolbox.util.args;

import java.util.Locale;

/**
 * Representation of a command-line option
 */
public abstract class Option
{
    private String shortForm = null;
    private String longForm = null;
    private boolean wantsValue = false;


    
    protected Option(char shortForm, String longForm, boolean wantsValue)
    {
        if (longForm == null)
            throw new IllegalArgumentException("null arg forms not allowed");
        this.shortForm = new String(new char[] { shortForm });
        this.longForm = longForm;
        this.wantsValue = wantsValue;
    }

    public String shortForm()
    {
        return this.shortForm;
    }

    public String longForm()
    {
        return this.longForm;
    }

    /**
     * Tells whether or not this option wants a value
     */
    public boolean wantsValue()
    {
        return this.wantsValue;
    }

    public final Object getValue(String arg, Locale locale)
        throws IllegalOptionValueException
    {
        if (this.wantsValue)
        {
            if (arg == null)
            {
                throw new IllegalOptionValueException(this, "");
            }
            return this.parseValue(arg, locale);
        }
        else
        {
            return Boolean.TRUE;
        }
    }

    /**
     * Override to extract and convert an option value passed on the
     * command-line
     */
    protected Object parseValue(String arg, Locale locale)
        throws IllegalOptionValueException
    {
        return null;
    }
}
