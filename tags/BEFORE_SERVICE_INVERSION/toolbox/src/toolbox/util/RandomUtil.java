package toolbox.util;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;

/**
 * Utility class for generating commonly used random elements that are not
 * covered by the commons-lang library.
 * 
 * @see org.apache.commons.lang.math.RandomUtils
 * @see toolbox.util.random.RandomSequence
 */
public final class RandomUtil
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction of this static singleton.
     */
    private RandomUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Generates a random integer in the range [floor..ceiling].
     * 
     * @param floor Lower boundary of the range.
     * @param ceiling Upper boundary of the range.
     * @return int
     * @throws IllegalArgumentException if floor is greater than ceiling.
     */
    public static int nextInt(int floor, int ceiling)
    {
        Validate.isTrue(
            floor <= ceiling,
            "Ceiling " + ceiling + " cannot be less than floor " + floor);

        return floor + (RandomUtils.nextInt(ceiling - floor + 1));
    }

    
    /**
     * Generates a random lowercase alpha character in the range [a..z].
     * 
     * @return char
     */
    public static char nextLowerAlpha()
    {
        return RandomStringUtils.randomAlphabetic(1).toLowerCase().charAt(0);
    }

    
    /**
     * Generates a random uppercase alpha character in the range [A..Z].
     * 
     * @return char
     */
    public static char nextUpperAlpha()
    {
        return Character.toUpperCase(nextLowerAlpha());
    }

    
    /**
     * Generates a random alpha character in the range [a..z, A..Z].
     * 
     * @return char
     */
    public static char nextAlpha()
    {
        char c = nextLowerAlpha();
        
        if (RandomUtils.nextBoolean())
            c = Character.toUpperCase(c);

        return c;
    }

    
    /**
     * Generates a random double.
     * 
     * @return double
     */
    public static double nextDouble()
    {
        int decimal = RandomUtils.nextInt();
        int fraction = RandomUtils.nextInt(100);
        return (double) (decimal + fraction / 100);
    }

    
    /**
     * Generates a random double in the range [0.0..ceiling]
     * 
     * @param ceiling Maximum value to generate.
     * @return double
     */
    public static double nextDouble(double ceiling)
    {
        int decimal = RandomUtils.nextInt((int) ceiling);
        int fraction = RandomUtils.nextInt(100);
        return (double) (decimal + fraction / 100);
    }

    
    /**
     * Generates a random byte in the range [0..255].
     * 
     * @return byte
     */
    public static byte nextByte()
    {
        return (byte) RandomUtils.nextInt(256);
    }
    
    
    /**
     * Generates a random string of max length 80 made up of arbitrary 
     * alphanumberic characters.
     * 
     * @return String
     */
    public static String nextString()
    {
        return nextString(RandomUtils.nextInt(81));
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
     * @return Object or null if pickList is empty
     */
    public static Object nextElement(Object[] pickList)
    {
        return (
            pickList.length == 0
                ? null
                : pickList[RandomUtils.nextInt(pickList.length)]);
    }
    

    /**
     * Returns a randomly chosen element from the passed in list.
     * 
     * @param list List to pick random element from.
     * @return Object or null if the list is empty.
     */
    public static Object nextElement(List list)
    {
        return (list.size() == 0 
            ? null 
            : list.get(RandomUtils.nextInt(list.size())));
    }
}