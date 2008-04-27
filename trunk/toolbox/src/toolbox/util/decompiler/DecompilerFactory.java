package toolbox.util.decompiler;

import toolbox.util.ArrayUtil;

/**
 * Factory class for creating a {@link Decompiler}.
 * 
 * @see toolbox.util.decompiler.Decompiler
 */
public final class DecompilerFactory
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Decompiler enumeration for Jode.
     */
    public static final String DECOMPILER_JODE = 
        "toolbox.util.decompiler.JodeDecompiler";

    /**
     * Decompiler enumeration for Jad.
     */
    public static final String DECOMPILER_JAD = 
        "toolbox.util.decompiler.JadDecompiler";
    
    /**
     * Decompiler enumeration for JReversePro.
     */
    public static final String DECOMPILER_JREVERSEPRO = 
        "toolbox.util.decompiler.JReverseProDecompiler";

    /**
     * Decompiler enumeration for the default decompiler.
     */
    public static final String DECOMPILER_DEFAULT = DECOMPILER_JODE; 

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
     * Creates the preferred decompiler. Jad is preferred if found, otherwise
     * Jode is returned. 
     * 
     * @return Instance of the preferred decompiler.
     * @throws DecompilerException if instantiation error occur.
     */
    public static Decompiler createPreferred() throws DecompilerException
    {
        if (JadDecompiler.isFound())
            return create(DECOMPILER_JAD);
        else
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
        
        for (int i = 0; i < decompilers_.length; i++)
            d[i] = create(decompilers_[i]);
            
        return d;
    }
}