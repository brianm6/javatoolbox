package toolbox.util.args;

/**
 * Thrown when the parsed command-line contains an option that is not
 * recognised. <code>getMessage()</code> returns
 * an error string suitable for reporting the error to the user (in
 * English).
 */
public class UnknownOptionException extends OptionException
{
    private String optionName = null;
        
    UnknownOptionException(String optionName)
    {
        super("unknown option '" + optionName + "'");
        this.optionName = optionName;
    }

    /**
     * @return the name of the option that was unknown (e.g. "-u")
     */
    public String getOptionName()
    {
        return this.optionName;
    }

}