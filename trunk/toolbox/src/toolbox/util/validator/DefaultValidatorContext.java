package toolbox.util.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;

/**
 * Default implementation of a {@link ValidatorContext}.
 */
public class DefaultValidatorContext implements ValidatorContext
{
    //--------------------------------------------------------------------------
    // Default Constants
    //--------------------------------------------------------------------------

    /**
     * Failfast is enabled by default.
     */
    private boolean DEFAULT_FAILFAST = true;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * List of failures that occurred during validation.
     */
    private List failures_;
    
    /**
     * List of warnings generated during validation.
     */
    private List warnings_;
    
    /**
     * Flag to enable the failfast feature.
     */
    private boolean failFast_;
    
    /**
     * A map of arbitrary objects that are participants in the validation 
     * process. Individual {@link ValidatorConstraint}s should know which 
     * participants to lookup to evaulate themselves.
     */
    private Map participants_;
    
    /**
     * Evaluation of this constraint performs the validation.
     */
    private Predicate constraint_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DefaultValidatorContext.
     */
    public DefaultValidatorContext()
    {
        failures_ = new ArrayList(1);
        warnings_ = new ArrayList(0);
        participants_ = new HashMap();
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
        failures_.clear();
        warnings_.clear();
        participants_.clear();
        failFast_ = DEFAULT_FAILFAST;
        constraint_ = TruePredicate.INSTANCE;
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
        return failures_;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#getWarnings()
     */
    public List getWarnings()
    {
        return warnings_;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#isFailFast()
     */
    public boolean isFailFast()
    {
        return failFast_;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#setFailFast(boolean)
     */
    public void setFailFast(boolean failfast)
    {
        failFast_ = failfast;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addParticipant(
     *      java.lang.Object, java.lang.Object)
     */
    public void addParticipant(Object key, Object participant)
    {
        participants_.put(key, participant);
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#getParticipant(
     *      java.lang.Object)
     */
    public Object getParticipant(Object key)
    {
        return participants_.get(key);
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#removeParticipant(
     *      java.lang.Object)
     */
    public void removeParticipant(Object key)
    {
        participants_.remove(key);
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addFailure(
     *      java.lang.String)
     */
    public void addFailure(String failure)
    {
        failures_.add(new Reason(failure));
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addFailure(
     *      java.lang.String, java.lang.Throwable)
     */
    public void addFailure(String failure, Throwable cause)
    {
        failures_.add(new Reason(failure, cause));
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addWarning(
     *      java.lang.String)
     */
    public void addWarning(String warning)
    {
        warnings_.add(new Reason(warning));
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#addWarning(
     *      java.lang.String, java.lang.Throwable)
     */
    public void addWarning(String warning, Throwable cause)
    {
        warnings_.add(new Reason(warning, cause));
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#getConstraint()
     */
    public Predicate getConstraint()
    {
        return constraint_;
    }


    /**
     * @see toolbox.util.validator.ValidatorContext#setConstraint(
     *      org.apache.commons.collections.Predicate)
     */
    public void setConstraint(Predicate constraint)
    {
        constraint_ = constraint;
    }
}