package toolbox.util;

import java.util.List;
import java.util.Random;

/**
 * Utility class for generating commonly used random elements.
 */
public final class RandomUtil
{
    // Clover private constructor workaround
    static { new RandomUtil(); }

    /**
     * Random number generator.
     */
    private static Random random_ = new Random(System.currentTimeMillis());

    /**
     * All alpha characters.
     */
    private static final String alphaChars_ = "abcdefghijklmnopqrstuvwxyz";

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction.
     */
    private RandomUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Generates an unsigned random integer in the range [0..MAX_INT].
     * 
     * @return Random int.
     */
    public static int nextInt()
    {
        return Math.abs(random_.nextInt());
    }

    
    /**
     * Generates a random integer in the range [0..ceiling].
     * 
     * @param ceiling Maximum value of the random number.
     * @return Random int <= ceiling.
     */
    public static int nextInt(int ceiling)
    {
        return (nextInt() % (ceiling + 1));
    }

    
    /**
     * Generates a random integer in the range [floor..ceiling].
     * 
     * @param floor Lower boundary.
     * @param ceiling Upper boundary.
     * @return Random int.
     */
    public static int nextInt(int floor, int ceiling)
    {
        Assert.isTrue(
            floor <= ceiling,
            "Ceiling " + ceiling + " cannot be less than floor " + floor);

        return floor + (nextInt(ceiling - floor));
    }

    
    /**
     * Generates a random lowercase alpha character in the range [a..z].
     * 
     * @return Random lowercase car.
     */
    public static char nextLowerAlpha()
    {
        return alphaChars_.charAt(nextInt(alphaChars_.length() - 1));
    }

    
    /**
     * Generates a random uppercase alpha character in the range [A..Z].
     * 
     * @return Random uppercase char.
     */
    public static char nextUpperAlpha()
    {
        return Character.toUpperCase(nextLowerAlpha());
    }

    
    /**
     * Generates a random alpha character in the range [a..z, A..Z].
     * 
     * @return Random case-agnostic char.
     */
    public static char nextAlpha()
    {
        char c = nextLowerAlpha();

        if (random_.nextBoolean())
            c = Character.toUpperCase(c);

        return c;
    }

    
    /**
     * Generates a random double.
     * 
     * @return Random double.
     */
    public static double nextDouble()
    {
        int decimal = nextInt();
        int fraction = nextInt(100);
        return (double) (decimal + fraction / 100);
    }

    
    /**
     * Generates a random double with a maximum value.
     * 
     * @param ceiling Maximum value of double to generate.
     * @return A random signed double from 0.0 to ceiling.
     */
    public static double nextDouble(double ceiling)
    {
        int decimal = nextInt((int) ceiling - 1);
        int fraction = nextInt(100);
        return (double) (decimal + fraction / 100);
    }

    
    /**
     * Generates a random boolean.
     * 
     * @return Random boolean.
     */
    public static boolean nextBoolean()
    {
        return (nextInt() % 2 == 0);
    }

    
    /**
     * Returns a randomly chosen element from the passed in array.
     * 
     * @param pickList Array of objects to pick from.
     * @return Randomly chosen element from the pickList. Null if array is
     *         empty.
     */
    public static Object nextElement(Object[] pickList)
    {
        return (
            pickList.length == 0
                ? null
                : pickList[nextInt(pickList.length - 1)]);
    }
    

    /**
     * Returns a randomly chosen element from the passed in list
     * 
     * @param list List to pick random element from.
     * @return Randomly chosen element from the list. Null if list is empty.
     */
    public static Object nextElement(List list)
    {
        return (list.size() == 0 ? null : list.get(nextInt(list.size() - 1)));
    }
}