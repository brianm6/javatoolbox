package toolbox.util.reflect;

/**
 * MethodHolder
 */
public class MethodHolder implements IMethodHolder
{
    protected SmartMethod method;

    // CONSTRUCTORS

    /**
     * Creates a new MethodHolder object.
     * 
     * @param method DOCUMENT ME!
     */
    public MethodHolder(SmartMethod method)
    {
        this.method = method;
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
        return method;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param newMethod DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public IMethodHolder addMethod(SmartMethod newMethod)
    {
        return method.getParameterTypes().length == newMethod.getParameterTypes().length
            ? (IMethodHolder) new MethodParamTypeHolder(method, newMethod)
            : (IMethodHolder) new MethodParamCountHolder(method, newMethod);
    }
}