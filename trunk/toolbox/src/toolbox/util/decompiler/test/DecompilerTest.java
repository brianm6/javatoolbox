package toolbox.util.decompiler.test;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ClassUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.decompiler.Decompiler;
import toolbox.util.decompiler.DecompilerFactory;

/**
 * Unit test for all Decompiler implementors.
 */
public class DecompilerTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(DecompilerTest.class);
    
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
     * @param args None recognized
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
     * @throws Exception on error
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
     * @throws Exception on error
     */
    public void testDecompileFile() throws Exception
    {
        logger_.info("Running testDecompileFile...");
        
        for (int i=0; i<decompilers_.length; i++)
        {
            try
            {
                URL url = ResourceUtil.getClassResourceURL(
                    getClass(), "DecompilerTestA.class");
                
                String source = 
                    decompilers_[i].decompile(new File(url.getFile()));
                
                logger_.debug("\n" + source);
            }
            catch (IllegalArgumentException iae)
            {
                logger_.info("Decompiler " + 
                    decompilers_[i].getClass().getName() + 
                    " does not support this method.");                
            }
        }
    }

    
    /**
     * Tests decompile(className, classPath).
     * 
     * @throws Exception on error
     */
    public void testDecompileClassnameClasspath() throws Exception
    {
        logger_.info("Running testDecompileClassnameClasspath...");
        
        for (int i=0; i<decompilers_.length; i++)
        {
            try
            {
                String source = decompilers_[i].decompile(
                    "java.lang.Object", ClassUtil.getClasspath());
                
                logger_.debug("\n" + source);
            }
            catch (IllegalArgumentException iae)
            {
                logger_.info("Decompiler " + 
                    decompilers_[i].getClass().getName() + 
                    " does not support this method.");                
            }
        }
    }
}