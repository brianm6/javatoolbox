package toolbox.util.decompiler;

import java.io.File;

import jreversepro.revengine.JSerializer;

import org.apache.log4j.Logger;

/**
 * Decompiler bridge to the JReversePro decompiler @ http://jrevpro.sf.net. 
 */
public class JReverseProDecompiler implements toolbox.util.decompiler.Decompiler
{
    private static final Logger logger_ = 
        Logger.getLogger(JReverseProDecompiler.class);
    
    /**
     * JReversePro decompiler.
     */
    private JSerializer decompiler_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JReverseProDecompiler.
     */
    public JReverseProDecompiler()
    {
        decompiler_ = new JSerializer();
    }
    
    //--------------------------------------------------------------------------
    // Decompiler Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.decompiler.Decompiler#getName()
     */
    public String getName()
    {
        return "JReversePro";
    }
    
    
    /**
     * @see toolbox.util.decompiler.Decompiler#decompile(java.io.File)
     */
    public String decompile(File classFile) throws DecompilerException 
    {
        String javaSource = null;
        
        try
        {
            decompiler_.loadClass(classFile);
            javaSource = decompiler_.reverseEngineer(true);
        }
        catch (Exception e)
        {
            throw new DecompilerException(e);
        }
        
        return javaSource;
    }
    
    
    /**
     * @see toolbox.util.decompiler.Decompiler#decompile(
     *      java.lang.String, java.lang.String)
     */
    public String decompile(String className, String classPath)
        throws DecompilerException
    {
        throw new IllegalArgumentException("Not supported");
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