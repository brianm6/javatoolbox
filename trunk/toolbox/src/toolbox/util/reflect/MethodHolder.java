package toolbox.util.reflect;

/**
 * MethodHolder
 */
public class MethodHolder implements IMethodHolder
{
    private SmartMethod method_;

    // CONSTRUCTORS

    /**
     * Creates a new MethodHolder object.
     * 
     * @param method DOCUMENT ME!
     */
    public MethodHolder(SmartMethod method)
    {
        this.method_ = method;
    }

    // METHODHOLDER METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param paramType DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartMethod getMethod(Class[] paramType) throws NoSuchMethodException
    {
        return method_;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param newMethod DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public IMethodHolder addMethod(SmartMethod newMethod)
    {
        return method_.getParameterTypes().length == 
               newMethod.getParameterTypes().length ? 
               (IMethodHolder) new MethodParamTypeHolder(method_, newMethod) : 
               (IMethodHolder) new MethodParamCountHolder(method_, newMethod);
    }
}