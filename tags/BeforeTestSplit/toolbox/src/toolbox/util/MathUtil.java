package toolbox.util;

/**
 * Math Utility Class.
 */
public final class MathUtil 
{
    // Clover private constructor workaround
    static { new MathUtil(); }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Private constructor.
     */
    private MathUtil() 
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Determines if a given integer is an even number.
     * 
     * @param i Integer to evaluate.
     * @return True if even, false otherwise.
     */
    public static boolean isEven(long i) 
    {
        return ((i % 2) == 0);
    }

    
    /**
     * Determines if a given integer is an odd number.
     * 
     * @param i Integer to evaluate.
     * @return True if odd, false otherwise.
     */
    public static boolean isOdd(long i) 
    {
        return !isEven(i);
    }
    
    
    /**
     * Adds a value to each index of an array.
     * 
     * @param array Array to add value to.
     * @param value Value to add to each array index.
     * @return Array with value added to each index.
     */
    public static int[] addToAll(int[] array, int value) 
    {
        for (int i = 0; i < array.length; i++)
            array[i] += value;
            
        return array;
    }
    
    
    /**
     * Calculates the inverse normal cumulative distribution.
     * 
     * @param p Any number between 0 and 1.
     * @return Inverse normal cumulative distribution.
     */
    public static double invNormalCumDist(double p) 
    {
        double c[] = new double[3];
        double d[] = new double[3];

        double arg, t, t2, t3, xnum, xden, qinvp, x, pc;

        c[0] = 2.515517;
        c[1] = .802853;
        c[2] = .010328;

        d[0] = 1.432788;
        d[1] = .189269;
        d[2] = .001308;

        if (p <= .5) 
        {
            arg = -2.0 * Math.log(p);
            t = Math.sqrt(arg);
            t2 = t * t;
            t3 = t2 * t;

            xnum = c[0] + c[1] * t + c[2] * t2;
            xden = 1.0 + d[0] * t + d[1] * t2 + d[2] * t3;
            qinvp = t - xnum / xden;
            x = -qinvp;

            return x;
        }
        else 
        {
            pc = 1.0 - p;
            arg = -2.0 * Math.log(pc);
            t = Math.sqrt(arg);
            t2 = t * t;
            t3 = t2 * t;

            xnum = c[0] + c[1] * t + c[2] * t2;
            xden = 1.0 + d[0] * t + d[1] * t2 + d[2] * t3;
            x = t - xnum / xden;

            return x;
        }
    }

    
    /**
     * Returns sum of array of integers.
     * 
     * @param d Array of integers.
     * @return Sum of elements in d.
     */
    public static int sum(int[] d) 
    {
        if (d.length == 0)
            return 0;
            
        int sum = 0;
        
        for (int i = 0; i < d.length; i++)
            sum += d[i];

        return sum;
    }
}