package toolbox.util.args;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

/**
 * Largely GNU-compatible command-line options parser. Has short (-v) and
 * long-form (--verbose) option support, and also allows options with
 * associated values (-d 2, --debug 2, --debug=2). Option processing
 * can be explicitly terminated by the argument '--'.
 *
 * @author Steve Purcell
 * @version $Revision: 1.4 $
 * 
 * Forked from JArgs
 */
public class ArgumentParser
{
    private String[] remainingArgs = null;
    private Hashtable options = new Hashtable(10);
    private Hashtable values = new Hashtable(10);

    /**
     * Add the specified Option to the list of accepted options
     */
    public Option addOption(Option opt)
    {
        this.options.put("-" + opt.shortForm(), opt);
        this.options.put("--" + opt.longForm(), opt);
        return opt;
    }

    /**
     * Convenience method for adding a string option.
     * @return the new Option
     */
    public final Option addStringOption(char shortForm, String longForm)
    {
        Option opt = new StringOption(shortForm, longForm);
        addOption(opt);
        return opt;
    }

    /**
     * Convenience method for adding an integer option.
     * @return the new Option
     */
    public final Option addIntegerOption(char shortForm, String longForm)
    {
        Option opt = new IntegerOption(shortForm, longForm);
        addOption(opt);
        return opt;
    }

    /**
     * Convenience method for adding a double option.
     * @return the new Option
     */
    public final Option addDoubleOption(char shortForm, String longForm)
    {
        Option opt = new DoubleOption(shortForm, longForm);
        addOption(opt);
        return opt;
    }

    /**
     * Convenience method for adding a boolean option.
     * @return the new Option
     */
    public final Option addBooleanOption(char shortForm, String longForm)
    {
        Option opt = new BooleanOption(shortForm, longForm);
        addOption(opt);
        return opt;
    }

    /**
     * @return the parsed value of the given Option, or null if the
     * option was not set
     */
    public final Object getOptionValue(Option o)
    {
        return values.get(o.longForm());
    }

    public final boolean getBooleanValue(Option opt, boolean def)
    {
        Object obj = getOptionValue(opt);
        
        if(obj == null)
            return def;
        else
            return ((Boolean) obj).booleanValue();
                
    }
    
    /**
     * @return the non-option arguments
     */
    public final String[] getRemainingArgs()
    {
        return this.remainingArgs;
    }

    /**
     * Extract the options and non-option arguments from the given
     * list of command-line arguments.
     */
    public final void parse(String[] argv)
        throws IllegalOptionValueException, UnknownOptionException
    { 
        parse(argv, Locale.getDefault());
    }

    /**
     * Extract the options and non-option arguments from the given
     * list of command-line arguments.
     */
    public final void parse(String[] argv, Locale locale)
        throws IllegalOptionValueException, UnknownOptionException
    {
        Vector otherArgs = new Vector();
        int position = 0;
        
        while (position < argv.length)
        {
            String curArg = argv[position];
            if (curArg.startsWith("-"))
            {
                if (curArg.equals("--"))
                { // end of options
                    position += 1;
                    break;
                }
                
                String valueArg = null;
                
                if (curArg.startsWith("--"))
                { // handle --arg=value
                    int equalsPos = curArg.indexOf("=");
                    
                    if (equalsPos != -1)
                    {
                        valueArg = curArg.substring(equalsPos + 1);
                        curArg = curArg.substring(0, equalsPos);
                    }
                }
                
                Option opt = (Option) this.options.get(curArg);
                
                if (opt == null)
                {
                    throw new UnknownOptionException(curArg);
                }
                
                Object value = null;
                
                if (opt.wantsValue())
                {
                    if (valueArg == null)
                    {
                        position += 1;
                        valueArg = null;
                        
                        if (position < argv.length)
                        {
                            valueArg = argv[position];
                        }
                    }
                    value = opt.getValue(valueArg, locale);
                }
                else
                {
                    value = opt.getValue(null, locale);
                }
                this.values.put(opt.longForm(), value);
                position += 1;
            }
            else
            {
                break;
            }
        }
        
        for (; position < argv.length; ++position)
        {
            otherArgs.addElement(argv[position]);
        }

        this.remainingArgs = new String[otherArgs.size()];
        
        int i = 0;
        
        for (Enumeration e = otherArgs.elements(); e.hasMoreElements(); ++i)
        {
            this.remainingArgs[i] = (String) e.nextElement();
        }
    }
}    