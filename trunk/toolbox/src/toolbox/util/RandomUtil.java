package toolbox.util;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;

/**
 * Utility class for generating commonly used random elements.
 * 
 * @see org.apache.commons.lang.math.RandomUtils
 */
public final class RandomUtil
{
    // Clover private constructor workaround
    static { new RandomUtil(); }

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
        return RandomUtils.nextInt();
    }

    
    /**
     * Generates a random integer in the range [0..ceiling].
     * 
     * @param ceiling Maximum value of the random number.
     * @return Random int <= ceiling.
     */
    public static int nextInt(int ceiling)
    {
        return RandomUtils.nextInt(ceiling + 1);
    }

    
    /**
     * Generates a random integer in the range [floor..ceiling].
     * 
     * @param floor Lower boundary.
     * @param ceiling Upper boundary.
     * @return Random int.
     * @throws IllegalArgumentException if floor is greater than ceiling.
     */
    public static int nextInt(int floor, int ceiling)
    {
        Validate.isTrue(
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
        return RandomStringUtils.randomAlphabetic(1).toLowerCase().charAt(0);
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
        
        if (nextBoolean())
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
        int fraction = nextInt(99);
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
        int fraction = nextInt(99);
        return (double) (decimal + fraction / 100);
    }

    
    /**
     * Generates a random boolean.
     * 
     * @return Random boolean.
     */
    public static boolean nextBoolean()
    {
        return RandomUtils.nextBoolean();
    }

    
    /**
     * Generates a random byte in the range [0..255].
     * 
     * @return byte
     */
    public static byte nextByte()
    {
        return (byte) nextInt(255);
    }
    
    
    /**
     * Generates a random string of max length 80 made up of arbitrary 
     * alphanumberic characters.
     * 
     * @return String
     */
    public static String nextString()
    {
        return nextString(nextInt(80));
    }


    /**
     * Generates a random string of the given length made up of arbitrary
     * alphanumberic characters.
     * 
     * @param length Length of the random string to generate.
     * @return String
     */
    public static String nextString(int length)
    {
        return RandomStringUtils.randomAlphanumeric(length);
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
     * Returns a randomly chosen element from the passed in list.
     * 
     * @param list List to pick random element from.
     * @return Randomly chosen element from the list. Null if list is empty.
     */
    public static Object nextElement(List list)
    {
        return (list.size() == 0 ? null : list.get(nextInt(list.size() - 1)));
    }
}