package toolbox.util.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;

/**
 * Default implemenation of a {@link ValidatorContext}.
 */
public class DefaultValidatorContext implements ValidatorContext
{
    //--------------------------------------------------------------------------
    // Defaults
    //--------------------------------------------------------------------------

    /**
     * Failfast is true be default.
     */
    private boolean DEFAULT_FAILFAST = true;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Failures that occurred during validation.
     */
    private List failures;
    
    /**
     * Warnings generated during validation.
     */
    private List warnings;
    
    /**
     * Flag to fail validation fast.
     */
    private boolean failFast;
    
    /**
     * Arbitrary objects that are participants in the validation process.
     */
    private Map participants;
    
    /**
     * Evaluation of this constraint performs the validation.
     */
    private Predicate constraint;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DefaultValidatorContext.
     */
    public DefaultValidatorContext()
    {
        failures = new ArrayList(1);
        warnings = new ArrayList(0);
        participants = new HashMap();
        reset();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Resets this context so that a new one does not have to be instantiated.
     */
    public void reset()
    {
        failures.clear();
        warnings.clear();
        participants.clear();
        failFast = DEFAULT_FAILFAST;
        constraint = TruePredicate.INSTANCE;
    }

    //--------------------------------------------------------------------------
    // ValidatorContext Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.validator.ValidatorContext#isValid()
     */
    public boolean isValid()
    {
        return getFailures().isEmpty();
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#getErrors()
     */
    public List getFailures()
    {
        return failures;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#getWarnings()
     */
    public List getWarnings()
    {
        return warnings;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#isFailFast()
     */
    public boolean isFailFast()
    {
        return failFast;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#setFailFast(boolean)
     */
    public void setFailFast(boolean b)
    {
        failFast = b;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addParticipant(
     *      java.lang.Object, java.lang.Object)
     */
    public void addParticipant(Object key, Object participant)
    {
        participants.put(key, participant);
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#getParticipant(
     *      java.lang.Object)
     */
    public Object getParticipant(Object key)
    {
        return participants.get(key);
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#removeParticipant(
     *      java.lang.Object)
     */
    public void removeParticipant(Object key)
    {
        participants.remove(key);
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addFailure(
     *      java.lang.String)
     */
    public void addFailure(String failure)
    {
        failures.add(new Reason(failure));
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addFailure(
     *      java.lang.String, java.lang.Throwable)
     */
    public void addFailure(String failure, Throwable cause)
    {
        failures.add(new Reason(failure, cause));
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addWarning(
     *      java.lang.String)
     */
    public void addWarning(String warning)
    {
        warnings.add(new Reason(warning));
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addWarning(
     *      java.lang.String, java.lang.Throwable)
     */
    public void addWarning(String warning, Throwable cause)
    {
        warnings.add(new Reason(warning, cause));
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#getRule()
     */
    public Predicate getConstraint()
    {
        return constraint;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#setRule(
     *      toolbox.util.validator.ValidatorConstraint)
     */
    public void setConstraint(Predicate constraint)
    {
        this.constraint = constraint;
    }
}