package toolbox.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * SmartConstructor
 */
public class SmartConstructor extends SmartMethod
{

    // CONSTRUCTORS

    /**
     * Creates a new SmartConstructor object.
     * 
     * @param constructor DOCUMENT ME!
     */
    public SmartConstructor(Constructor constructor)
    {
        super(constructor);
    }

    // ACCESSING

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public final Constructor getConstructor()
    {
        return (Constructor) method_;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public Class[] getExceptionTypes()
    {
        return getConstructor().getExceptionTypes();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public Class[] getParameterTypes()
    {
        return getConstructor().getParameterTypes();
    }

    // INSTATIONATION METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws InstantiationException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     * @throws InvocationTargetException DOCUMENT ME!
     */
    public Object newInstance(Object[] parameters)
        throws
            InstantiationException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException
    {
        try
        {
            if (parameters != null)
                for (int i = 0; i < parameters.length; i++)
                    parameters[i] = patterns_[i] == null ? 
                        parameters[i] : patterns_[i].convert(parameters[i]);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }

        return getConstructor().newInstance(parameters);
    }

    // INVOKATION METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws IllegalAccessException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     * @throws InvocationTargetException DOCUMENT ME!
     */
    public Object invoke(Object object, Object[] parameters)
        throws IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException
    {
        try
        {
            return newInstance(parameters);
        }
        catch (InstantiationException ex)
        {
            throw new InvocationTargetException(ex);
        }
    }
}