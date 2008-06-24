package toolbox.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.AbstractConstant}.
 */
public class AbstractConstantTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractConstantTest.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * File that the serilaized data of the constant will be read from/written 
     * to.
     */
    private static final File FILE_SERIALIZED = 
        new File(FileUtil.getTempDir(), 
            AbstractConstant.class.getName() + ".ser");
    
    /**
     * Instance of the constant to serialize.
     */
    private static final MockConstant CONST = MockConstant.TWO;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String args[]) throws Exception
    {
        TestRunner.run(AbstractConstantTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests passivation/activation of a constant to make sure it retains its
     * single instance identity.
     * 
     * @throws Exception on error.
     */
    public void testSerialization() throws Exception
    {
        logger_.info("Running testSerialization...");

        try
        {
            save(CONST);
            MockConstant nc = (MockConstant) load();
            assertTrue("Failed to match using ==", CONST == nc);
        }
        finally
        {
            FileUtil.deleteQuietly(FILE_SERIALIZED);
        }
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    /**
     * Loads an instance of a constant from a serialized file.
     * 
     * @return Loaded instance.
     * @throws IOException on I/O error.
     * @throws ClassNotFoundException on class not found error.
     */
    static Object load() throws IOException, ClassNotFoundException
    {
        Object o = null;
        FileInputStream fis = new FileInputStream(FILE_SERIALIZED);
        ObjectInputStream ois = new ObjectInputStream(fis);
        o = ois.readObject();
        ois.close();
        return o;
    }


    /**
     * Saves an instance of a constant to a file in serialzied form.
     * 
     * @param o Constant to serialize.
     * @throws IOException on I/O error.
     */
    static void save(Object o) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(FILE_SERIALIZED);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(o);
        oos.flush();
        oos.close();
    }
}