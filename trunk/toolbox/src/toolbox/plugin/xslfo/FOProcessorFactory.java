package toolbox.util.xslfo;

/**
 * Factory class that creates implementations of the FOProcessor interface.
 */
public final class FOProcessorFactory
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Apache FOP http://xml.apache.org 
     */
    public static final String FO_IMPL_APACHE = "fop";
    
    /** 
     * RenderX XEP http://www.renderx.com 
     */
    public static final String FO_IMPL_RENDERX  = "xep";
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Private constructor.
     */
    private FOProcessorFactory()
    {
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FOProcessor.
     * 
     * @param foImpl Implementation to create. See FO_* constants.
     * @return FOProcessor
     */
    public static FOProcessor createProcessor(String foImpl)
    {
        FOProcessor processor = null;
        
        if (foImpl.equals(FO_IMPL_APACHE))
            processor = new FOPProcessor();
        else if (foImpl.equals(FO_IMPL_RENDERX))
            processor = new XEPProcessor();
        else
            throw new IllegalArgumentException(
                "FO implementation " + foImpl + " not valid.");
                
        return processor;
    }
}