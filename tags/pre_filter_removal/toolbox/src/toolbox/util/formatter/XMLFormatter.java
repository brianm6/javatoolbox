package toolbox.util.formatter;

import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.util.PreferencedUtil;
import toolbox.util.XMLUtil;
import toolbox.util.XOMUtil;

/**
 * XML formatter that uses <a href="http://xml.apache.org/xerces2-j/">Xerces</a> 
 * internally for formatting.
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 *   Formatter f = new XMLFormatter();
 *   String xml = getSomeXML();
 *   String formattedXML = f.format(xml);
 * </pre>
 */
public class XMLFormatter extends AbstractFormatter
{
    private static final Logger logger_ = Logger.getLogger(XMLFormatter.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    /**
     * Root node for XMLFormatter preferences.
     */
    public static final String NODE_XMLFORMATTER = "XMLFormatter";
    
    /**
     * Persisted javabean properties.
     */
    public static final String[] SAVED_PROPS = {
        "indent",
        "lineWidth",
        "omitDeclaration"
    };
    
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
     * Formats the XML.
     * 
     * @see toolbox.util.formatter.Formatter#format(
     *      java.io.InputStream, java.io.OutputStream)
     */
    public void format(InputStream input, OutputStream output) throws Exception
    {
        String in = IOUtils.toString(input);
        
        String out = XMLUtil.format(
            in, getIndent(), getLineWidth(), isOmitDeclaration());
        
        output.write(out.getBytes());
        output.flush();
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
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_XMLFORMATTER, new Element(NODE_XMLFORMATTER));
        
        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_XMLFORMATTER);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);
        XOMUtil.insertOrReplace(prefs, root);
    }
}