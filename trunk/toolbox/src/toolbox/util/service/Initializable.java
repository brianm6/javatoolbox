package toolbox.util.service;

import java.util.Map;

/**
 * Initializable service.
 * 
 * @see toolbox.util.service.Destroyable
 */
public interface Initializable extends Service
{
    /**
     * Initializes the service with optional configuration informatin.
     * 
     * @param configuration Configuration information for this service. Use
     *        {@link java.util.Collections#EMPTY_MAP} for no configuration.
     * @throws IllegalStateException if trying to initialize from an invalid
     *         service state.
     * @throws ServiceException if the service encounters problems initializing.
     */
    void initialize(Map configuration) 
        throws IllegalStateException, ServiceException;
}