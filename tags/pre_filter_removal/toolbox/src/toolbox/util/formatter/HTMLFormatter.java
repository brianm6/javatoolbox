package toolbox.util.formatter;

import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Element;

import org.apache.log4j.Logger;

import org.w3c.tidy.Tidy;

import toolbox.util.PreferencedUtil;
import toolbox.util.XOMUtil;

/**
 * HTML formatter that uses <a href="http://jtidy.sf.net">JTidy</a> internally
 * for formatting.
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 *   Formatter f = new HTMLFormatter();
 *   String html = getSomeHTML();
 *   String formattedHTML = f.format(html);
 * </pre>
 */
public class HTMLFormatter extends AbstractFormatter
{
    private static final Logger logger_ = Logger.getLogger(HTMLFormatter.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    /**
     * Root prefs node for this HTMLFormatter.
     */
    public static final String NODE_HTMLFORMATTER = "HTMLFormatter";
    
    /**
     * Javabean properties that are persisted.
     */
    public static final String[] SAVED_PROPS = {
        "indent",
        "wrapLength"
    };

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * JTidy HTML formatter.
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
     * Formats the HTML.
     * 
     * @see toolbox.util.formatter.Formatter#format(java.io.InputStream, 
     *      java.io.OutputStream)
     */
    public void format(InputStream input, OutputStream output) throws Exception
    {
        tidy_.parse(input, output);
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
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_HTMLFORMATTER, new Element(NODE_HTMLFORMATTER));
        
        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_HTMLFORMATTER);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);
        XOMUtil.insertOrReplace(prefs, root);
    }
}