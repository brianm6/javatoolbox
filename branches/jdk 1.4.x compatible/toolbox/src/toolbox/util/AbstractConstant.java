package toolbox.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Abstract base class for constants. Automatically supports "safe"
 * serialization so that hydration in VMs other that the original will result
 * in a single instance per instance of the constant.
 * <p> 
 * <b>Example:</b>
 * <pre class="snippet">
 * public class MyConstant extends AbstractConstant
 * {
 *     public static final MyConstant ONE = new MyConstant();
 *     public static final MyConstant TWO = new MyConstant();
 * 
 *     private MyConstant() {}
 * }                                                               
 * </pre>
 */
public abstract class AbstractConstant implements Serializable
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * The internal representation of the constant.
     */
    private transient String fieldName_;

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Gets the field name for this instance and writes it to the stream.
     * 
     * @param out Object output stream.
     * @throws IOException on illegal access errors.
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {

        Class clazz = getClass();
        Field[] f = clazz.getDeclaredFields();

        for (int i = 0; i < f.length; i++)
        {
            try
            {
                int mod = f[i].getModifiers();

                if (Modifier.isStatic(mod) && 
                    Modifier.isFinal(mod)  && 
                    Modifier.isPublic(mod))
                {
                    if (this == f[i].get(null))
                    {
                        String fName = f[i].getName();
                        out.writeObject(fName);
                    }
                }
            }
            catch (IllegalAccessException ex)
            {
                throw new IOException(ex.getMessage());
            }
        }
    }


    /**
     * Reads the serialized field name and assigns it to fieldName.
     * 
     * @param in Object input stream.
     * @throws IOException on I/O error.
     */
    private void readObject(ObjectInputStream in) throws IOException
    {
        try
        {
            fieldName_ = (String) in.readObject();
        }
        catch (ClassNotFoundException ex)
        {
            throw new IOException(ex.getMessage());
        }
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Replaces the deserialized instance with the local static instance to 
     * allow correct usage of the == operator.
     * 
     * @throws ObjectStreamException on reading from the stream.
     * @throws InvalidObjectException on failure to resolve an object.
     */
    public Object readResolve() throws ObjectStreamException
    {
        try
        {
            Class clazz = getClass();
            Field f = clazz.getField(fieldName_);
            return f.get(null);
        }
        catch (Exception ex)
        {
            throw new InvalidObjectException("Failed to resolve object");
        }
    }
}