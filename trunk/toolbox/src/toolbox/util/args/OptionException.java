package toolbox.util.args;

/**
 * Base class for exceptions that may be thrown when options are parsed
 */
public abstract class OptionException extends Exception
{
    OptionException(String msg)
    {
        super(msg);
    }
}

