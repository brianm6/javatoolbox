package toolbox.plugin.docviewer;

/**
 * AbstractViewer is a base class implemenation of DocumentViewer.
 */
public abstract class AbstractViewer implements DocumentViewer
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
   
    /**
     * Viewer name.
     */
    private String name_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AbstractViewer.
     * 
     * @param name Viewer name.
     */
    public AbstractViewer(String name)
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
}