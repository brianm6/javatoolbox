package toolbox.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for AbstractConstant.
 * 
 * @see toolbox.util.AbstractConstant
 */
public class AbstractConstantTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractConstantTest.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    private static final String FNAME = "const.ser";
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

    public void testSerialization() throws Exception
    {
        logger_.info("Running testSerialization...");

        save(CONST);
        MockConstant nc = (MockConstant) load();
        assertTrue("Failed to match using ==", CONST == nc);
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    static Object load() throws IOException, ClassNotFoundException
    {
        Object o = null;
        FileInputStream fis = new FileInputStream(FNAME);
        ObjectInputStream ois = new ObjectInputStream(fis);
        o = ois.readObject();
        ois.close();
        return o;
    }


    static void save(Object o) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(FNAME);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(o);
        oos.flush();
        oos.close();
    }
}