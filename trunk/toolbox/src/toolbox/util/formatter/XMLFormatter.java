package toolbox.util.formatter;

import org.apache.log4j.Logger;

import toolbox.util.XMLUtil;

/**
 * Formatter for XML.
 */
public class XMLFormatter extends AbstractFormatter
{
    private static final Logger logger_ = Logger.getLogger(XMLFormatter.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Number of spaces to represent an indent.
     */
    private int indent_;
    
    /**
     * Line width before wrapping.
     */
    private int lineWidth_;
    
    /**
     * Flag to omit the xml declaration.
     */
    private boolean omitDeclaration_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a XMLFormatter.
     */
    public XMLFormatter()
    {
        super("XML Formatter");
        setIndent(2);
        setLineWidth(100);
        setOmitDeclaration(true);
    }

    //--------------------------------------------------------------------------
    // Formatter Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.formatter.Formatter#format(java.lang.String)
     */
    public String format(String input) throws Exception
    {
        return XMLUtil.format(
            input, getIndent(), getLineWidth(), isOmitDeclaration());
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
        return indent_;
    }


    /**
     * Sets the number of spaces per indentation.
     * 
     * @param indent Number of spaces.
     */
    public void setIndent(int indent)
    {
        indent_ = indent;
    }


    /**
     * Returns the line width before wrapping.
     * 
     * @return int
     */
    public int getLineWidth()
    {
        return lineWidth_;
    }


    /**
     * Sets the line width before wrapping.
     * 
     * @param lineWidth Width of line.
     */
    public void setLineWidth(int lineWidth)
    {
        lineWidth_ = lineWidth;
    }


    /**
     * Returns true if the XML declaration is omitted, false otherwise.
     * 
     * @return boolean.
     */
    public boolean isOmitDeclaration()
    {
        return omitDeclaration_;
    }


    /**
     * Sets the omission of the XML declaration.
     * 
     * @param omitDeclaration True to exclude, false otherwise.
     */
    public void setOmitDeclaration(boolean omitDeclaration)
    {
        omitDeclaration_ = omitDeclaration;
    }
}