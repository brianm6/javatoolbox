package toolbox.util;
 
/**
 * An Eiffel Assertion-like facility. When an asserted condition fails an
 * AssertExceptions is raised. The features of this class were strongly
 * influenced by the Assert protocol of the JUnit TestCase class.
 * <p>
 * Typically, the design for a utility class such as Assert make liberal use of
 * compiler optimization features afforded through use of <code>final</code>
 * methods and/or class declaration. Use of such techniques have been deferred
 * to subclassers.
 *
 * @see AssertionException
 */
public class Assert
{
    /** 
     * Default assertion error message 
     */
    private static final String DEFAULT_MSG = "";

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction
     */
    private Assert()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Assert that two doubles are equals within some tolerance.
     *
     * @param  doubleA    The first number to compare
     * @param  doubleB    The second number to compare
     * @param  tolerance  Allowed difference between doubleA and doubleB
     * @param  errorMsg   A text message used in the construction of an 
     *                    exception when the two numbers differ beyond tolerance
     * @throws AssertionException raised with errorMsg when the condition fails.
     */
    public static void equals(double doubleA, double doubleB, 
        double tolerance, String errorMsg) throws AssertionException
    {
        if (Math.abs(doubleA - doubleB) > tolerance)
        {
            raiseAssertionException(errorMsg + " (Expected <" + doubleA + 
                "> but was <" + doubleB + ">)");
        }
    }


    /**
     * Assert that two floats are equals within some tolerance.
     *
     * @param  floatA     The first number to compare
     * @param  floatB     The second number to compare
     * @param  tolerance  The allowed diff between floatA and floatB
     * @param  errorMsg   A text message used in the construction of an 
     *                    exception when the two numbers differ beyond 
     *                    tolerance.
     * @throws AssertionException raised with errorMsg when the condition fails.
     */
    public static void equals(float floatA, float floatB, 
        float tolerance, String errorMsg) throws AssertionException
    {
        if (Math.abs(floatA - floatB) > tolerance)
        {
            raiseAssertionException(errorMsg + " (Expected <" + floatA + 
                "> but was <" + floatB + ">)");
        }
    }

    /**
     * Assert that two longs are <code>equal</code>.
     * 
     * @param  longA    First long to compare
     * @param  longB    Second long to compare
     * @param  message  Text message used in the construction of an exception
     *                  when not equal.
     * @throws AssertionException raised with message when not equal.
     */
    public static void equals(long longA, long longB, String message)
        throws AssertionException
    {
        if (longA != longB)
        {
            raiseAssertionException(message + " (Expected <" + longA + 
                "> but was <" + longB + ">)");
        }
    }

    /**
     * Assert that two objects are equal.
     *
     * @param   objectA  The first object to compare
     * @param   objectB  The second object to compare
     * @throws  AssertionException raised when the objectA not equal to objectB
     */
    public static void equals(Object objectA, Object objectB)
        throws AssertionException
    {
        equals(objectA, objectB, DEFAULT_MSG);
    }

    /**
     * Assert that two objects are equal.
     *
     * @param   objectA   The first object to compare
     * @param   objectB   The second object to compare
     * @param   errorMsg  Text message used in the construction of an 
     *                    exception when the condition fails.
     * @throws  AssertionException raised when the objectA not equal to objectB
     */
    public static void equals(Object objectA, Object objectB, 
        String errorMsg) throws AssertionException
    {

        if (!objectA.equals(objectB))
        {
            raiseAssertionException(errorMsg + " (Expected <" + 
                objectA + "> but was <" + objectB + ">)");
        }
    }

    /**
     * Assert that the expressionResult is false.
     *
     * @param   expressionResult    Results in a boolean value
     * @throws  AssertionException raised when expressionResult is true
     */
    public static void isFalse(boolean expressionResult)
        throws AssertionException
    {
        isFalse(expressionResult, DEFAULT_MSG);
    }

    /**
     * Assert that the parameter is false.
     *
     * @param  expressionResult  A boolean; should be false.
     * @param  errorMsg          Text message used in the construction of an 
     *                           exception when expressionResult is true.
     * @throws AssertionException raised with message when expressionResult 
     *         is true
     */
    public static void isFalse(boolean expressionResult, String errorMsg)
        throws AssertionException
    {
        isTrue(!expressionResult, errorMsg);
    }

    /**
     * Assert that the expressionResult is true.
     *
     * @param   expressionResult    Results in a boolean value
     * @throws  AssertionException raised when expressionResult is false
     */
    public static void isTrue(boolean expressionResult)
        throws AssertionException
    {
        isTrue(expressionResult, DEFAULT_MSG);
    }

    /**
     * Assert that the parameter is true.
     *
     * @param  expressionResult  A boolean; should be true.
     * @param  errorMsg          Text message used in the construction of an 
     *                           exception when expressionResult is false.
     * @throws AssertionException raised with message when expressionResult is 
     *         false
     */
    public static void isTrue(boolean expressionResult, String errorMsg)
        throws AssertionException
    {
        if (!expressionResult)
        {
            raiseAssertionException(errorMsg);
        }
    }

    /**
     * Assert that an object is not null.
     *
     * @param   object  The non-null object
     * @throws  AssertionException raised when object is null.
     */
    public static void notNull(Object object) throws AssertionException
    {
        notNull(object, DEFAULT_MSG);
    }

    /**
     * Assert that an object is not null.
     *
     * @param  object    The non-null object
     * @param  errorMsg  Text message used in the construction of an exception 
     *                   when object is null.
     * @throws AssertionException raised with message when object is null.
     */
    public static void notNull(Object object, String errorMsg)
        throws AssertionException
    {
        if (object == null)
        {
            raiseAssertionException(errorMsg);
        }
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * A factory method for creating and throwing AssertionExceptions.
     *
     * @param  errorMsg  String message for the new exception
     * @throws AssertionException with given error message
     */
    protected static void raiseAssertionException(String errorMsg)
    {
        throw new AssertionException(errorMsg);
    }
}