package toolbox.plugin.xslfo;

/**
 * Factory that is responsible for creating concrete {@link FOProcessor}s.
 * <p>
 * <b>Example</b>
 * 
 * <pre class="snippet">
 * 
 * // Create Apache FOP processor
 * FOProcessor processor = 
 *     FOProcessorFactory.create(FOProcessorFactory.FO_IMPL_APACHE);
 * 
 * // Create PDF
 * processor.renderPDF(in, out);
 * 
 * </pre>
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
     * @param foImpl Implementation to create.
     * @return FOProcessor
     * @see #FO_IMPL_APACHE
     * @see #FO_IMPL_RENDERX
     */
    public static FOProcessor create(String foImpl)
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