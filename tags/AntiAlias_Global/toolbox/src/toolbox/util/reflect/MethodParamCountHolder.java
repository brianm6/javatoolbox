package toolbox.util.reflect;

/**
 * MethodParamCountHolder
 */
public class MethodParamCountHolder implements IMethodHolder
{
    private int paramOffset_;
    private IMethodHolder[] holders_;

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
        holders_ = new IMethodHolder[Math.max(offset1, offset2) + 1];
        holders_[offset1] = new MethodHolder(method1);
        holders_[offset2] = new MethodHolder(method2);
    }

    /**
     * Creates a new MethodParamCountHolder object.
     * 
     * @param method DOCUMENT ME!
     * @param holder DOCUMENT ME!
     * @param count DOCUMENT ME!
     */
    public MethodParamCountHolder(SmartMethod method, IMethodHolder holder, 
        int count)
    {
        int offset = method.getParameterTypes().length;
        holders_ = new IMethodHolder[Math.max(offset, count) + 1];
        holders_[offset] = new MethodHolder(method);
        holders_[count] = holder;
    }

    // METHODHOLDER METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param paramTypes DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartMethod getMethod(Class[] paramTypes) 
        throws NoSuchMethodException
    {
        return holders_[paramTypes.length].getMethod(paramTypes);
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

        if (offset > holders_.length - 1)
        {
            IMethodHolder[] temp = holders_;
            holders_ = new IMethodHolder[offset + 1];
            System.arraycopy(temp, 0, holders_, 0, temp.length);
            holders_[offset] = new MethodHolder(method);
        }
        else
        {
            IMethodHolder holder = holders_[offset];
            
            holder = holder == null  ? new MethodHolder(method) : 
                holder.addMethod(method);
                
            holders_[offset] = holder;
        }

        return this;
    }
}