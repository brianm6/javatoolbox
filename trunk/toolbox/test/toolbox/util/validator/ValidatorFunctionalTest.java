package toolbox.util.validator;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.validator.functional.BabyBellPhoneNumber;
import toolbox.util.validator.functional.BabyBellPhoneNumberValidator;
import toolbox.util.validator.functional.IPhoneNumber;
import toolbox.util.validator.functional.PhoneNumberValidatorContext;

/**
 * ValidatorFunctionalTest is a functional test for the validation framework
 * found in package toolbox.util.validator. See package 
 * toolbox.util.validator.functional for the classes used by this test.
 */
public class ValidatorFunctionalTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ValidatorFunctionalTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String args[])
    {
        TestRunner.run(ValidatorFunctionalTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        logger_.info(StringUtils.repeat("=", 80));
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    public void testBothConstraintsValid()
    {
        logger_.info("Running testBothConstraintsValid...");

        IPhoneNumber number = new BabyBellPhoneNumber();
        number.setNumber("972-555-1212");

        ValidatorContext context = new DefaultValidatorContext();
        context.addParticipant("number", number);

        Validator validator = new BabyBellPhoneNumberValidator();
        validator.validate(context);

        logger_.info("IsValid = " + context.isValid());

        logger_.info("Failures = "
            + ArrayUtil.toString(context.getFailures().toArray(), false));

        logger_.info("Warnings = "
            + ArrayUtil.toString(context.getWarnings().toArray(), false));

        assertTrue(context.isValid());
        assertTrue(context.getFailures().isEmpty());
        assertTrue(context.getWarnings().isEmpty());
    }


    public void testBothConstraintsInvalid_NotFailFast()
    {
        logger_.info("Running testBothConstraintsInvalid_NotFailFast...");

        IPhoneNumber number = new BabyBellPhoneNumber();
        number.setNumber("565-555");

        Validator validator = new BabyBellPhoneNumberValidator();
        ValidatorContext context = new PhoneNumberValidatorContext(number);
        context.setFailFast(false);
        validator.validate(context);

        logger_.info("IsValid = " + context.isValid());

        logger_.info("Failures = "
            + ArrayUtil.toString(context.getFailures().toArray(), false));

        logger_.info("Warnings = "
            + ArrayUtil.toString(context.getWarnings().toArray(), false));

        assertFalse(context.isValid());
        assertEquals(2, context.getFailures().size());
        assertTrue(context.getWarnings().isEmpty());
    }


    public void testBothConstraintsInvalid_FailFast()
    {
        logger_.info("Running testBothConstraintsInvalid_FailFast...");

        IPhoneNumber number = new BabyBellPhoneNumber();
        number.setNumber("565-555");

        Validator validator = new BabyBellPhoneNumberValidator();
        ValidatorContext context = new PhoneNumberValidatorContext(number);
        context.setFailFast(true);
        validator.validate(context);

        logger_.info("IsValid = " + context.isValid());

        logger_.info("Failures = "
            + ArrayUtil.toString(context.getFailures().toArray(), false));

        logger_.info("Warnings = "
            + ArrayUtil.toString(context.getWarnings().toArray(), false));

        assertFalse(context.isValid());
        assertEquals(1, context.getFailures().size());
        assertTrue(context.getWarnings().isEmpty());
    }
}