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
    /** Default assertion error message **/
    private static final String DEFAULT_MSG = "";

    /**
     * Enforce singleton
     */
    private Assert()
    {
    }

    /**
     * Assert that two doubles are equals within some tolerance.
     *
     * @param aDoubleA          The first number to compare
     * @param aDoubleB          The second number to compare
     * @param allowedTolerance  The allowed difference between 
     *                          aDoubleA and aDoubleB
     * @param anErrorMsg        A text message used in the construction 
     *                          of an exception when the two numbers differ 
     *                          beyond allowedTolerance.
     * @exception               AssertionException raised with anErrorMsg 
     *                          when the condition fails.
     */
    public static void equals(double aDoubleA, double aDoubleB, 
        double allowedTolerance, String anErrorMsg) throws AssertionException
    {
        if (Math.abs(aDoubleA - aDoubleB) > allowedTolerance)
        {
            raiseAssertionException(anErrorMsg + " (Expected <" + aDoubleA + 
                "> but was <" + aDoubleB + ">)");
        }
    }


    /**
     * Assert that two floats are equals within some tolerance.
     *
     * @param aFloatA           The first number to compare
     * @param aFloatB           The second number to compare
     * @param allowedTolerance  The allowed diff between aFloatA and aFloatB
     * @param anErrorMsg        A text message used in the construction of an 
     *                          exception when the two numbers differ beyond 
     *                          allowedTolerance.
     * @exception               AssertionException raised with anErrorMsg 
     *                          when the condition fails.
     */
    public static void equals(float aFloatA, float aFloatB, 
        float allowedTolerance, String anErrorMsg) throws AssertionException
    {
        if (Math.abs(aFloatA - aFloatB) > allowedTolerance)
        {
            raiseAssertionException(anErrorMsg + " (Expected <" + aFloatA + 
                "> but was <" + aFloatB + ">)");
        }
    }

    /**
     * Assert that two longs are <code>equal</code>.
     * 
     * @param aLongA    First long to compare
     * @param aLongB    Second long to compare
     * @param message   A text message used in the construction of an exception
     *                  when anObject is null.
     * @exception       AssertionException raised with message when anObject 
     *                  is null.
     */
    public static void equals(long aLongA, long aLongB, String message)
        throws AssertionException
    {

        if (aLongA != aLongB)
        {
            raiseAssertionException(message + " (Expected <" + aLongA + 
                "> but was <" + aLongB + ">)");
        }
    }

    /**
     * Assert that two objects are equal.
     *
     * @param     anObjectA  The first object to compare
     * @param     anObjectB  The second object to compare
     * @exception            AssertionException raised when the anObjectA not 
     *                       equal to anObjectB
     */
    public static void equals(Object anObjectA, Object anObjectB)
        throws AssertionException
    {
        equals(anObjectA, anObjectB, DEFAULT_MSG);
    }

    /**
     * Assert that two objects are equal.
     *
     * @param anObjectA   The first object to compare
     * @param anObjectB   The second object to compare
     * @param anErrorMsg  A text message used in the construction of an 
     *                    exception when the condition fails.
     * @exception         AssertionException raised when the anObjectA not 
     *                    equal to anObjectB
     */
    public static void equals(Object anObjectA, Object anObjectB, 
        String anErrorMsg) throws AssertionException
    {

        if (!anObjectA.equals(anObjectB))
        {
            raiseAssertionException(anErrorMsg + " (Expected <" + 
                anObjectA + "> but was <" + anObjectB + ">)");
        }
    }

    /**
     * Assert that the anExpressionResult is false.
     *
     * @param anExpressionResult   Results in a boolean value
     * @exception AssertionException  Raised when anExpressionResult is true
     */
    public static void isFalse(boolean anExpressionResult)
        throws AssertionException
    {
        isFalse(anExpressionResult, DEFAULT_MSG);
    }

    /**
     * Assert that the parameter is false.
     *
     * @param anExpressionResult  A boolean; should be false.
     * @param anErrorMsg          A text message used in the construction of an 
     *                            exception when anExpressionResult is true.
     * @exception                 AssertionException raised with message when 
     *                            anExpressionResult is true
     */
    public static void isFalse(boolean anExpressionResult, String anErrorMsg)
        throws AssertionException
    {

        isTrue(!anExpressionResult, anErrorMsg);
    }

    /**
     * Assert that the anExpressionResult is true.
     *
     * @param anExpressionResult   Results in a boolean value
     * @exception AssertionException  Raised when anExpressionResult is false
     */
    public static void isTrue(boolean anExpressionResult)
        throws AssertionException
    {
        isTrue(anExpressionResult, DEFAULT_MSG);
    }

    /**
     * Assert that the parameter is true.
     *
     * @param anExpressionResult  A boolean; should be true.
     * @param anErrorMsg          A text message used in the construction 
     *                            of an exception when anExpressionResult 
     *                            is false.
     * @exception                 AssertionException raised with message when 
     *                            anExpressionResult is false
     */
    public static void isTrue(boolean anExpressionResult, String anErrorMsg)
        throws AssertionException
    {

        if (!anExpressionResult)
        {
            raiseAssertionException(anErrorMsg);
        }
    }

    /**
     * Assert that an object is not null.
     *
     * @param anObject  The non-null object
     * @exception AssertionException  Raised when anObject is null.
     */
    public static void notNull(Object anObject) throws AssertionException
    {
        notNull(anObject, DEFAULT_MSG);
    }

    /**
     * Assert that an object is not null.
     *
     * @param anObject    The non-null object
     * @param anErrorMsg  A text message used in the construction of an 
     *                    exception when anObject is null.
     * @exception AssertionException raised with message when anObject is null.
     */
    public static void notNull(Object anObject, String anErrorMsg)
        throws AssertionException
    {

        if (anObject == null)
        {
            raiseAssertionException(anErrorMsg);
        }
    }

    /**
     * A factory method for creating and throwing AssertionExceptions.
     *
     * @param anErrorMsg  a String message for the new exception
     * @exception anAssertionException
     */
    protected static void raiseAssertionException(String anErrorMsg)
    {
        throw new AssertionException(anErrorMsg);
    }
}
