package toolbox.util.decompiler;

import toolbox.util.ArrayUtil;

/**
 * Factory class for creating Decompilers.
 */
public class DecompilerFactory
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Decompiler enumeration for Jode.
     */
    private static final String DECOMPILER_JODE = 
        "toolbox.util.decompiler.JodeDecompiler";

    /**
     * Decompiler enumeration for Jad.
     */
    private static final String DECOMPILER_JAD = 
        "toolbox.util.decompiler.JadDecompiler";
    
    /**
     * Decomiler enumeration for JReversePro.
     */
    private static final String DECOMPILER_JREVERSEPRO = 
        "toolbox.util.decompiler.JReverseProDecompiler";

    /**
     * List of decompiler FQCN.
     */
    private static String[] decompilers_;
    
    static
    {
        decompilers_ = new String[] 
        {
          DECOMPILER_JODE,
          DECOMPILER_JAD,
          DECOMPILER_JREVERSEPRO
        };
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Private constructor.
     */
    private DecompilerFactory()
    {
    }
    
    //--------------------------------------------------------------------------
    // Public Static
    //--------------------------------------------------------------------------
    
    /**
     * Creates a decompiler.
     * 
     * @param decompiler Decompiler FQCN. 
     *        Use DecompilerFactory.DECOMPILER_[JAD|JODE|etc]
     * @return Newly created decompiler instance.
     * @throws DecompilerException if instantiation errors occur.
     */
    public static Decompiler create(String decompiler) 
        throws DecompilerException
    {
        Decompiler d = null;
        
        if (ArrayUtil.contains(decompilers_, decompiler))
        {
            try
            {
                d = (Decompiler) Class.forName(decompiler).newInstance();
            }
            catch (Exception e)
            {
                throw new DecompilerException(e);
            }
        }
        else
        {
            throw new IllegalArgumentException(
                "Decompiler '" + decompiler + "' is not valid.");
        }
        
        return d;
    }

    
    /**
     * Creates the preferred decompiler. Right now, its JODE. 
     * 
     * @return Instance of the preferred decompiler.
     * @throws DecompilerException if instantiation error occur.
     */
    public static Decompiler createPreferred() throws DecompilerException
    {
        /*
         * TODO: This should in fact create the preferred decompiler given the 
         *       current context. For example, use Jad if on the windows 
         *       platform and jad.exe is available or use Jode otherwise.
         * 
         */
        
        return create(DECOMPILER_JODE);
    }
    
    
    /**
     * Returns all known decompilers.
     * 
     * @return Array of all known decompilers.
     * @throws DecompilerException if instantiation errors occur.
     */
    public static Decompiler[] createAll() throws DecompilerException
    {
        Decompiler d[] = new Decompiler[decompilers_.length];
        
        for (int i=0; i<decompilers_.length; i++)
            d[i] = create(decompilers_[i]);
            
        return d;
    }
}