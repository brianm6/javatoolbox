package toolbox.util.formatter;

import de.hunsicker.jalopy.Jalopy;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;

/**
 * Formatter for java source code.
 */
public class JavaFormatter extends AbstractFormatter
{
    private static final Logger logger_ = Logger.getLogger(JavaFormatter.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JavaFormatter.
     */
    public JavaFormatter()
    {
        super("Java Formatter");
    }

    //--------------------------------------------------------------------------
    // Formatter Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.formatter.Formatter#format(java.lang.String)
     */
    public String format(String input) throws Exception
    {
        Jalopy jalopy = new Jalopy();

        // specify input and output target
        StringBuffer output = new StringBuffer();
        String fakeInputFile = FileUtil.createTempFilename();
        jalopy.setInput(input, fakeInputFile);
        jalopy.setOutput(output);

        // format and overwrite the given input file
        boolean formatted = jalopy.format();

        if (jalopy.getState() == Jalopy.State.OK)
            logger_.info("Java formatted successfully formatted");
        else if (jalopy.getState() == Jalopy.State.WARN)
            logger_.warn("Java formatted with warnings");
        else if (jalopy.getState() == Jalopy.State.ERROR)
            logger_.error("Java could not be formatted");
        
        return output.toString();
    }
}