package toolbox.util.decompiler;

import org.apache.log4j.Logger;

/**
 * Abstract base class implementation of a Decompiler that contains the 
 * implementation for the common Nameable interface.
 */
public abstract class AbstractDecompiler implements Decompiler
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractDecompiler.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * UI friendly name.
     */
    private String name_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an AbstractDecompiler.
     * 
     * @param name This decompilers name.
     */
    public AbstractDecompiler(String name)
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
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }
}