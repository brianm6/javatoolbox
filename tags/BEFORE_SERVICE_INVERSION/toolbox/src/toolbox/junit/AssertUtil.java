package toolbox.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;

import junit.framework.AssertionFailedError;
import junitx.framework.Assert;

import org.apache.log4j.Logger;

/**
 * Various assertion methods for JUnit.
 */
public class AssertUtil
{
    private static final Logger logger_ = Logger.getLogger(AssertUtil.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction of this static class.
     */
    private AssertUtil()
    {
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Asserts that a given object is serializable. This is verified by:
     * <ul>
     *  <li>Object implements Serializable
     *  <li>Object is serialized to a byte array successfully.
     *  <li>Object is deserialized from a byte array successfully.
     *  <li>Object before serialization is of the same type as the object after
     *      deserialization.
     * </ul>
     * 
     * @param serializable Object to be tested for serialization.
     * @throws AssertionFailedError on failure.
     * @see #assertSerializable(Object, boolean, boolean)
     */
    public static void assertSerializable(Object serializable) 
    {
        assertSerializable(serializable, false, false, null);
    }

    
    /**
     * Asserts that a given object is serializable. This is verified by:
     * <ul>
     *  <li>Object implements Serializable
     *  <li>Object is serialized to a byte array successfully.
     *  <li>Object is deserialized from a byte array successfully.
     *  <li>Object before serialization is of the same type as the object after
     *      deserialization.
     *  <li>Optionally tests equality by value using a.equals(b). 
     * </ul>
     * 
     * @param serializable Object to be tested for serialization.
     * @param testEquals Set to true to test a.equals(b), false otherwise.
     * @throws AssertionFailedError on failure.
     */
    public static void assertSerializable(
        Object serializable, 
        boolean testEquals) 
    {
        assertSerializable(serializable, testEquals, false, null);
    }
    
    
    /**
     * Asserts that a given object is serializable. This is verified by:
     * <ul>
     *  <li>Object implements Serializable
     *  <li>Object is serialized to a byte array successfully.
     *  <li>Object is deserialized from a byte array successfully.
     *  <li>Object before serialization is of the same type as the object after
     *      deserialization.
     *  <li>Optionally tests equality by value using a.equals(b). 
     *  <li>Optionally tests equality by reference using (a == b).
     *  <li>Optionally tests equality by a given Comparator.
     * </ul>
     * 
     * @param serializable Object to be tested for serialization.
     * @param testEquals Set to true to test a.equals(b), false otherwise.
     * @param testEquality Set to true to test via (a == b), false otherwise.
     * @param testComparator Test equality using a Comparator, null otherwise.
     * @throws AssertionFailedError on failure.
     */
    public static void assertSerializable(
        Object serializable, 
        boolean testEquals, 
        boolean testEquality,
        Comparator testComparator)  
    {
        // Implements serialization check
        Assert.assertTrue(
            "Object does not implements Serializable", 
            serializable instanceof Serializable);
        
        Serializable deserialized = null;
        
        try
        {
            // Serialize to byte stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(serializable);
            oos.close();

            Assert.assertTrue(
                "Serialzed object is zero bytes", 
                baos.size() > 0);
            
            logger_.debug(
                "Serialized length = " 
                //+ expected.getClass().getName() 
                //+ " is " 
                + baos.size() 
                + " bytes");
                    
            // Deserialize from byte stream
            InputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bais);
            deserialized = (Serializable) in.readObject();
            in.close();
        }
        catch (Exception e)
        {
            throw new AssertionFailedError(e.getMessage());
        }
        
        // Verify
        Assert.assertNotNull("Deserialized object is null", deserialized);
        
        Assert.assertEquals(
            "Invalid type", serializable.getClass(), deserialized.getClass());

        if (testEquals)
            Assert.assertEquals(
                "Failed a.equals(b)", serializable, deserialized);
        
        if (testEquality)
            Assert.assertTrue("Failed a == b", serializable == deserialized);
        
        if (testComparator != null)
            Assert.assertEquals("Failed a.compare(b)", 0, 
                testComparator.compare(serializable, deserialized));
    }
}