package toolbox.util.reflect;

/**
 * MethodHolder.
 */
public class MethodHolder implements IMethodHolder
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private SmartMethod method_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new MethodHolder object.
     * 
     * @param method DOCUMENT ME!
     */
    public MethodHolder(SmartMethod method)
    {
        method_ = method;
    }

    //--------------------------------------------------------------------------
    // IMethodHolder Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.reflect.IMethodHolder#getMethod(java.lang.Class[])
     */
    public SmartMethod getMethod(Class[] paramType)
        throws NoSuchMethodException
    {
        return method_;
    }


    /**
     * @see toolbox.util.reflect.IMethodHolder#addMethod(
     *      toolbox.util.reflect.SmartMethod)
     */
    public IMethodHolder addMethod(SmartMethod newMethod)
    {
        return method_.getParameterTypes().length == 
            newMethod.getParameterTypes().length 
            ? (IMethodHolder) new MethodParamTypeHolder(method_, newMethod)
            : (IMethodHolder) new MethodParamCountHolder(method_, newMethod);
    }
}