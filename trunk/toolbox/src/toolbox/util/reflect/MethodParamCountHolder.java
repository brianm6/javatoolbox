package toolbox.util.reflect;

/**
 * MethodParamCountHolder
 */
public class MethodParamCountHolder implements IMethodHolder
{
    protected int paramOffset;
    protected IMethodHolder[] holders;

    // CONSTRUCTORS

    /**
     * Creates a new MethodParamCountHolder object.
     * 
     * @param method1 DOCUMENT ME!
     * @param method2 DOCUMENT ME!
     */
    public MethodParamCountHolder(SmartMethod method1, SmartMethod method2)
    {
        int offset1 = method1.getParameterTypes().length;
        int offset2 = method2.getParameterTypes().length;
        holders = new IMethodHolder[Math.max(offset1, offset2) + 1];
        holders[offset1] = new MethodHolder(method1);
        holders[offset2] = new MethodHolder(method2);
    }

    /**
     * Creates a new MethodParamCountHolder object.
     * 
     * @param method DOCUMENT ME!
     * @param holder DOCUMENT ME!
     * @param count DOCUMENT ME!
     */
    public MethodParamCountHolder(SmartMethod method, IMethodHolder holder, int count)
    {
        int offset = method.getParameterTypes().length;
        holders = new IMethodHolder[Math.max(offset, count) + 1];
        holders[offset] = new MethodHolder(method);
        holders[count] = holder;
    }

    // METHODHOLDER METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param paramTypes DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartMethod getMethod(Class[] paramTypes) throws NoSuchMethodException
    {
        return holders[paramTypes.length].getMethod(paramTypes);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param method DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public IMethodHolder addMethod(SmartMethod method)
    {
        int offset = method.getParameterTypes().length;

        if (offset > holders.length - 1)
        {
            IMethodHolder[] temp = holders;
            holders = new IMethodHolder[offset + 1];
            System.arraycopy(temp, 0, holders, 0, temp.length);
            holders[offset] = new MethodHolder(method);
        }
        else
        {
            IMethodHolder holder = holders[offset];
            holder = holder == null ? new MethodHolder(method) : holder.addMethod(method);
            holders[offset] = holder;
        }

        return this;
    }
}