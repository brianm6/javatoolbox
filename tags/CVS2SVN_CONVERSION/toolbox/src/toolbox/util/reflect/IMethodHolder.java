package toolbox.util.reflect;

/**
 * IMethodHolder.
 */
public interface IMethodHolder
{
    /**
     * Gets the method given the parameter types.
     * 
     * @param paramType List of parameter types.
     * @return Matching mehod. 
     * @throws NoSuchMethodException if no matching methods found.
     */
    SmartMethod getMethod(Class[] paramType) throws NoSuchMethodException;

    
    /**
     * Adds a method.
     * 
     * @param method Method to add
     * @return IMethodHolder 
     */
    IMethodHolder addMethod(SmartMethod method);
}