package toolbox.util;

import java.util.Random;

/**
 * Utility class for generating commonly used random elements.
 */
public final class RandomUtil
{
    /** random number generator **/
    private static Random r = new Random(System.currentTimeMillis());
  
    /** All alpha characters **/
    private static final String alphaChars = "abcdefghijklmnopqrstuvwxyz";
    
    /**
     * Prevent construction
     */
    private RandomUtil()
    {
    }


    /**
     * Generates an unsigned random integer in the range [0..MAX_INT]
     * 
     * @return    int
     */
    public static int nextInt()
    {
        return Math.abs(r.nextInt());
    }


    /**
     * Generates a random integer in the range [0..ceiling]
     * 
     * @param   ceiling    Maximum value of the random number
     * @return  int 
     */
    public static int nextInt(int ceiling)
    {
        return (nextInt() % (ceiling+1));
    }


    /**
     * Generates a random integer in the range [floor..ceiling]
     *
     * @param   floor        Lower boundary
     * @param   ceiling      Upper boundary
     * @return  int
     */
    public static int nextInt(int floor, int ceiling)
    {
        Assert.isTrue(floor <= ceiling, 
            "Ceiling " + ceiling + " cannot be less than floor " + floor);
            
        return floor + (nextInt(ceiling - floor));
    }

    
    /**
     * Generates a random lowercase alpha character in the range [a..z]
     * 
     * @return    char
     */
    public static char nextLowerAlpha()
    {
        return alphaChars.charAt(nextInt(alphaChars.length()-1));
    }

    
    /**
     * Generates a random uppercase alpha character in the range [A..Z]
     * 
     * @return  char
     */
    public static char nextUpperAlpha()
    {
        return Character.toUpperCase(nextLowerAlpha());
    }

    
    /**
     * Generates a random alpha character in the range [a..z, A..Z]
     * 
     * @return  char
     */
    public static char nextAlpha()
    {
        char c = nextLowerAlpha();
        if(r.nextBoolean())
            c = Character.toUpperCase(c);
        return c;
    }

    
    /**
     * Generates a random double
     *  
     * @return  double
     */
    public static double nextDouble()
    {
        int decimal = nextInt();
        int fraction = nextInt(100);
        return (double) (decimal + fraction / 100);
    }


    /**
     * Generates a random double with a maximum value
     * 
     * @param  ceiling  Maximum value of double to generate
     * @return A random signed double from 0.0 to ceiling
     */
    public static double nextDouble(double ceiling)
    {
        int decimal = nextInt((int) ceiling - 1);
        int fraction = nextInt(100);
        return (double) (decimal + fraction / 100);
    }
}
