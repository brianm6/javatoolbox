package org.apache.commons.cli;

import java.util.ArrayList;

public class Option implements Cloneable {

    /** constant that specifies the number of argument values has 
        not been specified */
    public static final int UNINITIALIZED = -1;

    /** constant that specifies the number of argument values is infinite */
    public static final int UNLIMITED_VALUES = -2;

    /** opt the name of the option */
    private String opt;

    /** longOpt is the long representation of the option */
    private String longOpt;

    /** hasArg specifies whether this option has an associated argument */
    private boolean hasArg;

    /** argName specifies the name of the argument for this option */
    private String argName = "arg";

    /** description of the option */
    private String description;

    /** required specifies whether this option is required to be present */
    private boolean required;

    /** specifies whether the argument value of this Option is optional */
    private boolean optionalArg;

    /** 
     * numberOfArgs specifies the number of argument values this option 
     * can have 
     */
    private int numberOfArgs = UNINITIALIZED;

    /** the type of this Option */
    private Object type;

    /** the list of argument values **/
    private ArrayList values = new ArrayList();

    /** the character that is the value separator */
    private char valuesep;

    /**
     * Creates an Option using the specified parameters.
     *
     * @param opt short representation of the option
     * @param description describes the function of the option
     *
     * @throws IllegalArgumentException if there are any non valid
     * Option characters in <code>opt</code>.
     */
    public Option(String opt, String description)
           throws IllegalArgumentException
    {
        this(opt, null, false, description);
    }

    /**
     * Creates an Option using the specified parameters.
     *
     * @param opt short representation of the option
     * @param hasArg specifies whether the Option takes an argument or not
     * @param description describes the function of the option
     *
     * @throws IllegalArgumentException if there are any non valid
     * Option characters in <code>opt</code>.
     */
    public Option(String opt, boolean hasArg, String description)
           throws IllegalArgumentException
    {
        this(opt, null, hasArg, description);
    }

    /**
     * <p>Creates an Option using the specified parameters.</p>
     *
     * @param opt short representation of the option
     * @param longOpt the long representation of the option
     * @param hasArg specifies whether the Option takes an argument or not
     * @param description describes the function of the option
     *
     * @throws IllegalArgumentException if there are any non valid
     * Option characters in <code>opt</code>.
     */
    public Option(String opt, String longOpt, boolean hasArg, 
                  String description)
           throws IllegalArgumentException
    {
        // ensure that the option is valid
        OptionValidator.validateOption(opt);

        this.opt = opt;
        this.longOpt = longOpt;

        // if hasArg is set then the number of arguments is 1
        if (hasArg)
        {
            this.numberOfArgs = 1;
        }

        this.hasArg = hasArg;
        this.description = description;
    }

    /**
     * <p>Returns the id of this Option.  This is only set when the
     * Option shortOpt is a single character.  This is used for switch
     * statements.</p>
     *
     * @return the id of this Option
     */
    public int getId()
    {
        return getKey().charAt(0);
    }

    /**
     * <p>Returns the 'unique' Option identifier.</p>
     * 
     * @return the 'unique' Option identifier
     */
    public String getKey()
    {
        // if 'opt' is null, then it is a 'long' option
        if (opt == null)
        {
            return this.longOpt;
        }

        return this.opt;
    }

    public String getOpt()
    {
        return this.opt;
    }

    /**
     * <p>Retrieve the type of this Option.</p>
     * 
     * @return The type of this option
     */
    public Object getType()
    {
        return this.type;
    }

    /**
     * <p>Sets the type of this Option.</p>
     *
     * @param type the type of this Option
     */
    public void setType(Object type)
    {
        this.type = type;
    }

    /** 
     * <p>Retrieve the long name of this Option.</p>
     *
     * @return Long name of this option, or null, if there is no long name
     */
    public String getLongOpt()
    {
        return this.longOpt;
    }

    /**
     * <p>Sets the long name of this Option.</p>
     *
     * @param longOpt the long name of this Option
     */
    public void setLongOpt(String longOpt)
    {
        this.longOpt = longOpt;
    }

    /**
     * <p>Sets whether this Option can have an optional argument.</p>
     *
     * @param optionalArg specifies whether the Option can have
     * an optional argument.
     */
    public void setOptionalArg(boolean optionalArg)
    {
        this.optionalArg = optionalArg;
    }

    /**
     * @return whether this Option can have an optional argument
     */
    public boolean hasOptionalArg()
    {
        return this.optionalArg;
    }

    /** <p>Query to see if this Option has a long name</p>
     *
     * @return boolean flag indicating existence of a long name
     */
    public boolean hasLongOpt()
    {
        return (this.longOpt != null);
    }

    /** <p>Query to see if this Option requires an argument</p>
     *
     * @return boolean flag indicating if an argument is required
     */
    public boolean hasArg()
    {
        return (this.numberOfArgs > 0) || (numberOfArgs == UNLIMITED_VALUES);
    }

    /** <p>Retrieve the self-documenting description of this Option</p>
     *
     * @return The string description of this option
     */
    public String getDescription()
    {
        return this.description;
    }

    /** 
     * <p>Query to see if this Option requires an argument</p>
     *
     * @return boolean flag indicating if an argument is required
     */
    public boolean isRequired()
    {
        return this.required;
    }

    /**
     * <p>Sets whether this Option is mandatory.</p>
     *
     * @param required specifies whether this Option is mandatory
     */
    public void setRequired(boolean required)
    {
        this.required = required;
    }

    /**
     * <p>Sets the display name for the argument value.</p>
     *
     * @param argName the display name for the argument value.
     */
    public void setArgName(String argName)
    {
        this.argName = argName;
    }

    /**
     * <p>Gets the display name for the argument value.</p>
     *
     * @return the display name for the argument value.
     */
    public String getArgName()
    {
        return this.argName;
    }

    /**
     * <p>Returns whether the display name for the argument value
     * has been set.</p>
     *
     * @return if the display name for the argument value has been
     * set.
     */
    public boolean hasArgName()
    {
        return (this.argName != null && this.argName.length() > 0);
    }

    /** 
     * <p>Query to see if this Option can take many values</p>
     *
     * @return boolean flag indicating if multiple values are allowed
     */
    public boolean hasArgs()
    {
        return (this.numberOfArgs > 1) 
                || (this.numberOfArgs == UNLIMITED_VALUES);
    }

    /** 
     * <p>Sets the number of argument values this Option can take.</p>
     *
     * @param num the number of argument values
     */
    public void setArgs(int num)
    {
        this.numberOfArgs = num;
    }

    /**
     * <p>Sets the value separator.  For example if the argument value
     * was a Java property, the value separator would be '='.</p>
     *
     * @param sep The value separator.
     */
    public void setValueSeparator(char sep)
    {
        this.valuesep = sep;
    }

    /**
     * <p>Returns the value separator character.</p>
     *
     * @return the value separator character.
     */
    public char getValueSeparator()
    {
        return this.valuesep;
    }

    /**
     * <p>Return whether this Option has specified a value separator.</p>
     * 
     * @return whether this Option has specified a value separator.</p>
     */
    public boolean hasValueSeparator()
    {
        return (this.valuesep > 0);
    }

    /** 
     * <p>Returns the number of argument values this Option can take.</p>
     *
     * @return num the number of argument values
     */
    public int getArgs()
    {
        return this.numberOfArgs;
    }

    /**
     * <p>Adds the specified value to this Option.</p>
     * 
     * @param value is a/the value of this Option
     */
    void addValue(String value)
    {
        switch (numberOfArgs)
        {
        case UNINITIALIZED:
            throw new RuntimeException("NO_ARGS_ALLOWED");

        default:
            processValue(value);
        }
    }

    /**
     * <p>Processes the value.  If this Option has a value separator
     * the value will have to be parsed into individual tokens.  When
     * n-1 tokens have been processed and there are more value separators
     * in the value, parsing is ceased and the remaining characters are
     * added as a single token.</p>
     *
     * @param value The String to be processed.
     *
     * @since 1.0.1
     */
    private void processValue(String value)
    {
        // this Option has a separator character
        if (hasValueSeparator())
        {
            // get the separator character
            char sep = getValueSeparator();

            // store the index for the value separator
            int index = value.indexOf(sep);

            // while there are more value separators
            while (index != -1)
            {
                // next value to be added 
                if (values.size() == (numberOfArgs - 1))
                {
                    break;
                }


                // store
                add(value.substring(0, index));


                // parse
                value = value.substring(index + 1);


                // get new index
                index = value.indexOf(sep);
            }
        }


        // store the actual value or the last value that has been parsed
        add(value);
    }

    /**
     * <p>Add the value to this Option.  If the number of arguments
     * is greater than zero and there is enough space in the list then
     * add the value.  Otherwise, throw a runtime exception.
     * </p>
     *
     * @param value The value to be added to this Option
     *
     * @since 1.0.1
     */
    private void add(String value)
    {
        if ((numberOfArgs > 0) && (values.size() > (numberOfArgs - 1)))
        {
            throw new RuntimeException("Cannot add value, list full.");
        }


        // store value
        this.values.add(value);
    }

    /**
     * Returns the specified value of this Option or 
     * <code>null</code> if there is no value.
     *
     * @return the value/first value of this Option or 
     * <code>null</code> if there is no value.
     */
    public String getValue()
    {
        return hasNoValues() ? null : (String) this.values.get(0);
    }

    /**
     * Returns the specified value of this Option or 
     * <code>null</code> if there is no value.
     *
     * @param index The index of the value to be returned.
     *
     * @return the specified value of this Option or 
     * <code>null</code> if there is no value.
     *
     * @throws IndexOutOfBoundsException if index is less than 1
     * or greater than the number of the values for this Option.
     */
    public String getValue(int index)
        throws IndexOutOfBoundsException
    {
        return hasNoValues() ? null : (String) this.values.get(index);
    }

    /**
     * Returns the value/first value of this Option or the 
     * <code>defaultValue</code> if there is no value.
     *
     * @param defaultValue The value to be returned if ther
     * is no value.
     *
     * @return the value/first value of this Option or the 
     * <code>defaultValue</code> if there are no values.
     */
    public String getValue(String defaultValue)
    {
        String value = getValue();

        return (value != null) ? value : defaultValue;
    }

    /**
     * Return the values of this Option as a String array 
     * or null if there are no values
     *
     * @return the values of this Option as a String array 
     * or null if there are no values
     */
    public String[] getValues()
    {
        return hasNoValues()
               ? null : (String[]) this.values.toArray(new String[] {  });
    }

    /**
     * @return the values of this Option as a List
     * or null if there are no values
     */
    public java.util.List getValuesList()
    {
        return this.values;
    }

    /**
     * @return a copy of this Option
     */
    public Object clone()
    {
        Option option = new Option(getOpt(), getDescription());

        option.setArgs(getArgs());
        option.setOptionalArg(hasOptionalArg());
        option.setRequired(isRequired());
        option.setLongOpt(getLongOpt());
        option.setType(getType());
        option.setValueSeparator(getValueSeparator());

        return option;
    }

    /** 
     * <p>Dump state, suitable for debugging.</p>
     *
     * @return Stringified form of this object
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer().append("[ option: ");

        buf.append(this.opt);

        if (this.longOpt != null)
        {
            buf.append(" ").append(this.longOpt);
        }

        buf.append(" ");

        if (hasArg)
        {
            buf.append("+ARG");
        }

        buf.append(" :: ").append(this.description);

        if (this.type != null)
        {
            buf.append(" :: ").append(this.type);
        }

        buf.append(" ]");

        return buf.toString();
    }

    /**
     * Returns whether this Option has any values.
     *
     * @return whether this Option has any values.
     */
    private boolean hasNoValues()
    {
        return this.values.size() == 0;
    }
}