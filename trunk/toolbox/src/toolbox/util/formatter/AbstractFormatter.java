package toolbox.util.formatter;

import java.util.Map;

import toolbox.util.service.ServiceException;

/**
 * Abstract formatter that takes care of non-formatter related housekeeping 
 * stuff.
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
    // Initializable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws ServiceException
    {
        // NO-OP. Override to specialize.
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
}