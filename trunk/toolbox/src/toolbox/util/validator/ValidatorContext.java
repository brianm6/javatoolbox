package toolbox.util.validator;

import java.util.List;

import org.apache.commons.collections.Predicate;

/**
 * ValidatorContext places the following responsiblities on implementing
 * classes.
 * <ul>
 *   <li>Provides access to arbitrary contextual information to evaluate the
 *       validity of an entity.
 *   <li>Serves as a repository of validation results from any number of
 *       constraints evaluated in a validation plan.
 *   <li>Decides whether an entity is deemded valid based on the results of a
 *       validation constraint.
 *   <li>Contains the failfast option used to prematurely terminate a validation
 *       plan as soon as the first failure is encountered.
 * </ul>
 */
public interface ValidatorContext
{
    /**
     * Returns the constraint associated with this context.
     * 
     * @return Predicate
     */
    Predicate getConstraint();


    /**
     * Sets the constraint associated with this context.
     * 
     * @param constraint Constraint to set.
     */
    void setConstraint(Predicate constraint);


    /**
     * Returns true if the entity is valid, false otherwise.
     * 
     * @return boolean
     */
    boolean isValid();


    /**
     * Returns the list of failures.
     * 
     * @return List<Reason>
     */
    List getFailures();


    /**
     * Returns the list of warnings.
     * 
     * @return List<Reason>
     */
    List getWarnings();


    /**
     * Returns true if the evaluation of the constraint should terminate at the
     * occurence of the first failure, false otherwise.
     * 
     * @return boolean
     */
    boolean isFailFast();


    /**
     * Sets the failfast flag.
     * 
     * @param b True to enable failfast, false otherwise.
     */
    void setFailFast(boolean b);


    /**
     * Adds a participant to the constraint evaluation.
     * 
     * @param key Key of the participant.
     * @param participant Participant object.
     */
    void addParticipant(Object key, Object participant);


    /**
     * Retrieves a participant given its key.
     * 
     * @param key Key of the participant.
     * @return Object
     */
    Object getParticipant(Object key);


    /**
     * Removes a participant from the constraint evaluation.
     * 
     * @param key Key of the participant to remove.
     */
    void removeParticipant(Object key);


    /**
     * Adds a failure to this context.
     * 
     * @param failure Failure message.
     */
    void addFailure(String failure);


    /**
     * Adds a failure to this context.
     * 
     * @param failure Failure message.
     * @param cause Cause of the failure.
     */
    void addFailure(String failure, Throwable cause);


    /**
     * Adds a warning to this context.
     * 
     * @param warning Warning message.
     */
    void addWarning(String warning);


    /**
     * Adds a warning to this context.
     * 
     * @param warning Warning message.
     * @param cause Cause of this warning.
     */
    void addWarning(String warning, Throwable cause);
}