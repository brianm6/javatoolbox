package toolbox.util.formatter;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import org.w3c.tidy.Tidy;

import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Formatter for HTML.
 */
public class HTMLFormatter extends AbstractFormatter
{
    private static final Logger logger_ = Logger.getLogger(HTMLFormatter.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * HTML formatter.
     */
    private Tidy tidy_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a HTMLFormatter.
     */
    public HTMLFormatter()
    {
        super("HTML Formatter");
        tidy_ = new Tidy();
        tidy_.setIndentContent(true);
        tidy_.setWrapAttVals(true);
        tidy_.setBreakBeforeBR(true);
        tidy_.setMakeClean(true);
        tidy_.setWrapScriptlets(true);
        //tidy.setIndentAttributes(true);
        //tidy.setTabsize()
        //tidy.setSmartIndent(true);

        setIndent(2);
        setWrapLength(100);
    }

    //--------------------------------------------------------------------------
    // Formatter Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.formatter.Formatter#format(java.lang.String)
     */
    public String format(String input) throws Exception
    {
        InputStream is = new StringInputStream(input);
        OutputStream os = new StringOutputStream();
        tidy_.parse(is, os);
        return os.toString();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the number of spaces per indentation.
     * 
     * @return int
     */
    public int getIndent()
    {
        return tidy_.getSpaces();
    }


    /**
     * Sets the number of spaces per indentation.
     * 
     * @param indent Number of spaces.
     */
    public void setIndent(int indent)
    {
        tidy_.setSpaces(indent);
        tidy_.setTabsize(indent);
    }


    /**
     * Returns the line width before wrapping.
     * 
     * @return int
     */
    public int getWrapLength()
    {
        return tidy_.getWraplen();
    }


    /**
     * Sets the line width before wrapping.
     * 
     * @param wrapLength Width of line.
     */
    public void setWrapLength(int wrapLength)
    {
        tidy_.setWraplen(wrapLength);
    }
}