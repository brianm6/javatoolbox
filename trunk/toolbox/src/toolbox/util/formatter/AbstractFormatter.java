package toolbox.util.formatter;

import nu.xom.Element;

import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * An abstract implementation of a {@link Formatter} that assumes responsibility
 * for behavior common to most concrete implementations.
 * <p>
 * This base class requires that only the
 * <code>format(InputStream, OutputStream)</code> method be implemented by
 * subclasses. The remaining variations all funnel to this one method.
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
     * Delegates to the stream based implementation.
     *
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
     * Override in subclass to apply preferences.
     *
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
    }


    /**
     * Override in subclass to save preferences.
     *
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
    }
}