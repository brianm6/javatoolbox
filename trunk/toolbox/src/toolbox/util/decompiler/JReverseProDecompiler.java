package toolbox.util.decompiler;

import java.io.File;

import jreversepro.reflect.JClassInfo;
import jreversepro.revengine.JSerializer;

import org.apache.log4j.Logger;

/**
 * Decompiler adapter for the <a href="http://jrevpro.sf.net">JReversePro</a>
 * decompiler. 
 * 
 * @see toolbox.util.decompiler.DecompilerFactory 
 */
public class JReverseProDecompiler extends AbstractDecompiler
{
    private static final Logger logger_ = 
        Logger.getLogger(JReverseProDecompiler.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
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
        super("JReversePro");
        decompiler_ = new JSerializer();
    }
    
    //--------------------------------------------------------------------------
    // Decompiler Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.decompiler.Decompiler#decompile(java.io.File)
     */
    public String decompile(File classFile) throws DecompilerException 
    {
        String javaSource = null;
        
        try
        {
            JClassInfo classInfo = decompiler_.loadClass(classFile);
            classInfo.reverseEngineer(false);
            javaSource = classInfo.getStringifiedClass(false);
        }
        catch (Exception e)
        {
            throw new DecompilerException(e);
        }
        
        return javaSource;
    }
    
    
    /**
     * Not supported.
     * 
     * @see toolbox.util.decompiler.Decompiler#decompile(
     *      java.lang.String, java.lang.String)
     */
    public String decompile(String className, String classPath)
        throws DecompilerException
    {
        throw new UnsupportedOperationException("Not supported");
    }
}