package toolbox.util.args;


public class BooleanOption extends Option
{
    public BooleanOption(char shortForm, String longForm)
    {
        super(shortForm, longForm, false);
    }
}
