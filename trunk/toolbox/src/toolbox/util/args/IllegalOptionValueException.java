package toolbox.util.args;


/**
 * Thrown when an illegal or missing value is given by the user for
 * an option that takes a value. <code>getMessage()</code> returns
 * an error string suitable for reporting the error to the user (in
 * English).
 */
public class IllegalOptionValueException extends OptionException
{
    private Option option;
    private String value;
    
    public IllegalOptionValueException(Option opt, String value)
    {
        super(
            "illegal value '"
                + value
                + "' for option -"
                + opt.shortForm()
                + "/--"
                + opt.longForm());
        option = opt;
        value = value;
    }

    /**
     * @return the name of the option whose value was illegal (e.g. "-u")
     */
    public Option getOption()
    {
        return this.option;
    }

    /**
     * @return the illegal value
     */
    public String getValue()
    {
        return this.value;
    }
}

