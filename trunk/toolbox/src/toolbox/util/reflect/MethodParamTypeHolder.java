package toolbox.util.reflect;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.Vector;

/**
 * MethodParamTypeHolder
 */
public class MethodParamTypeHolder implements IMethodHolder
{
    private int paramCount_ = -1;
    private Vector holders_ = new Vector(5);
    private Vector patterns_ = new Vector(5);

    // CONSTRUCTORS

    /**
     * Creates a new MethodParamTypeHolder object.
     * 
     * @param method1 DOCUMENT ME!
     * @param method2 DOCUMENT ME!
     */
    public MethodParamTypeHolder(SmartMethod method1, SmartMethod method2)
    {
        paramCount_ = method1.getParameterTypes().length;
        holders_.add(new MethodHolder(method1));
        holders_.add(new MethodHolder(method2));
        patterns_.add(method1.getParameterPatterns());
        patterns_.add(method2.getParameterPatterns());
    }

    // METHODHOLDER METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param method DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public IMethodHolder addMethod(SmartMethod method)
    {

        // Check if we have the same number of parameters
        if (paramCount_ != method.getParameterTypes().length || 
            method.getParameterTypes().length == 0)
            
            return new MethodParamCountHolder(method, this, paramCount_);

        // Add the information
        patterns_.add(method.getParameterPatterns());
        holders_.add(new MethodHolder(method));

        return this;
    }

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
        int total = 0;
        TreeMap map = new TreeMap(IntegerComparator.getComparator());

        // Check all the patters until we find the appropiate
        for (int i = 0; i < patterns_.size(); i++)
        {
            total = 0;

            ParamPattern[] testPatterns = 
                (ParamPattern[]) patterns_.elementAt(i);

            for (int j = 0; j < testPatterns.length; j++)
            {
                int factor = testPatterns[j].getMatchingFactor(paramTypes[j]);

                // Stop after we get a not match
                if (factor == ParamPattern.MATCH_NOT)
                {
                    total = factor;
                    break;
                }

                total += factor;
            }

            if (total > 0)
                map.put(new Integer(total), new Integer(i));
        }

        // Check if we have something
        if (map.size() == 0)
            throw new NoSuchMethodException();

        Integer last = (Integer) map.lastKey();
        Integer key = (Integer) map.get(last);
        
        IMethodHolder holderRes = 
            (IMethodHolder) holders_.elementAt(key.intValue());

        return holderRes.getMethod(paramTypes);
    }

    // COMPARATOR
    protected static class IntegerComparator implements Comparator
    {
        protected static final IntegerComparator defComparator_ = 
            new IntegerComparator();

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object obj1, Object obj2)
        {
            return ((Integer) obj1).intValue() - ((Integer) obj2).intValue();
        }

        
        /**
         * Returns the comparator.
         * 
         * @return Comparator.
         */
        protected static Comparator getComparator()
        {
            return defComparator_;
        }
    }
}