package toolbox.util.formatter;

import nu.xom.Element;

import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Abstract formatter that takes care of non-formatter related housekeeping 
 * stuff.
 * <br>
 *  This base class requires that only the 
 * <code>format(InputStream, OutputStream)</code> be implemented by subclasses.
 * <br>
 * The other various signatures are implemented here and all funnel to the
 * <code>format(InputStream, OutputStream)</code> implementation.
 */
public abstract class AbstractFormatter implements Formatter
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Friendly name of this formatter.
     */
    private String name_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AbstractFormatter.
     */
    public AbstractFormatter()
    {
    }

    
    /**
     * Creates a AbstractFormatter with the given name.
     * 
     * @param name Name of this formatter.
     */
    public AbstractFormatter(String name)
    {
        setName(name);
    }

    //--------------------------------------------------------------------------
    // Nameable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName()
    {
        return name_;
    }


    /**
     * @see toolbox.util.service.Nameable#setName(java.lang.String)
     */
    public void setName(String name)
    {
        name_ = name;
    }
    
    //--------------------------------------------------------------------------
    // Formatter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Delegates to method signature that takes an InputStream and OutputStream.
     * 
     * @see toolbox.util.formatter.Formatter#format(
     *      java.io.InputStream, java.io.OutputStream)
     * @see toolbox.util.formatter.Formatter#format(java.lang.String)
     */
    public String format(String input) throws Exception
    {
        StringInputStream sis = new StringInputStream(input);
        StringOutputStream sos = new StringOutputStream();
        format(sis, sos);
        return sos.toString();
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
    }
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
    }
}