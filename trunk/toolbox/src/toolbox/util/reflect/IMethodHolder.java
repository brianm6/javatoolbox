package toolbox.util.reflect;

/**
 * IMethodHolder
 */
public interface IMethodHolder
{

    /**
     * DOCUMENT ME!
     * 
     * @param paramType DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartMethod getMethod(Class[] paramType) throws NoSuchMethodException;

    /**
     * DOCUMENT ME!
     * 
     * @param method DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public IMethodHolder addMethod(SmartMethod method);
}