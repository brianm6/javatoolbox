package toolbox.util.decompiler;

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.util.ClassUtil;
import toolbox.util.FileUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.StringUtil;

/**
 * Unit test for all {@link toolbox.util.decompiler.Decompiler} implementations.
 */
public class DecompilerTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(DecompilerTest.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Array of decompilers to test.
     */
    private Decompiler[] decompilers_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(DecompilerTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DecompilerTest.
     * 
     * @throws Exception on error.
     */
    public DecompilerTest() throws Exception
    {
        decompilers_ = DecompilerFactory.createAll();
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Class to test for String decompile(File).
     * 
     * @throws Exception on error.
     */
    public void testDecompileFile() throws Exception
    {
        logger_.info("Running testDecompileFile...");
        
        for (int i = 0; i < decompilers_.length; i++)
        {
            String tmpClass = null;
            Decompiler d = decompilers_[i];
            
            try
            {
                InputStream is = 
                    ResourceUtil.getResource("java/lang/Object.class");
                
                tmpClass = FileUtil.createTempFilename() + ".class";
                
                FileUtil.setFileContents(
                    tmpClass, IOUtils.toByteArray(is), false);
                
                String source = d.decompile(new File(tmpClass));
                logger_.debug(StringUtil.banner("// " + d + "\n" + source));
            }
            catch (IllegalArgumentException iae)
            {
                logger_.debug("Decompiler " + d.getClass().getName() + 
                    " does not support this method.");                
            }
            finally
            {
                FileUtil.deleteQuietly(tmpClass);
            }
        }
    }

    
    /**
     * Tests decompile(className, classPath).
     * 
     * @throws Exception on error.
     */
    public void testDecompileClassnameClasspath() throws Exception
    {
        logger_.info("Running testDecompileClassnameClasspath...");
        
        for (int i = 0; i < decompilers_.length; i++)
        {
            try
            {
                String source = 
                    decompilers_[i].decompile(
                        "java.lang.Object", ClassUtil.getClasspath());
                
                logger_.debug(StringUtil.banner(source));
            }
            catch (IllegalArgumentException iae)
            {
                logger_.debug("Decompiler " + 
                    decompilers_[i].getClass().getName() + 
                    " does not support this method.");                
            }
        }
    }
}