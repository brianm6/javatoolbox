package toolbox.util;

/**
 * Math Utility Class
 */
public final class MathUtil 
{
    /**
     * Prevent construction
     */
    private MathUtil() 
    {
    }

    /**
     * Determines of the contents of a string can be expressed as a double
     * 
     * @param   s   String to inspect
     * @return  True if string contains a double, false otherwise
     */
    public static boolean isDouble(String s)
    {
        boolean b = false;
        
        if (StringUtil.isNullEmptyOrBlank(s))
            b = false;
        else
        {
            try
            {
                Double.parseDouble(s);
                b = true;
            }
            catch(NumberFormatException nfe)
            {
                b = false;
            }
        }
        
        return b;
    }


    /**
     * Determines if a given integer is an even number
     * 
     * @param   i   Integer to evaluate
     * @return  True if even, false otherwise
     */
    public static boolean isEven(int i) 
    {
        return ((i%2) == 0);
    }


    /**
     * Determines if a given integer is an odd number
     * 
     * @param   i   Integer to evaluate
     * @return  True if odd, false otherwise
     */
    public static boolean isOdd(int i) 
    {
        return !isEven(i);
    }
}