package toolbox.util.service;

import java.util.Map;

/**
 * Implemented by services which can be initialized with an optional map of 
 * configuration information.
 * 
 * @see toolbox.util.service.Destroyable
 */
public interface Initializable extends Service
{
    /**
     * Initializes the service with optional configuration information.
     * 
     * @param config Configuration information for this service. Use
     *        {@link java.util.Collections#EMPTY_MAP} for an empty 
     *        configuration.
     * @throws IllegalStateException if trying to initialize from an invalid
     *         service state.
     * @throws ServiceException if the service encounters problems initializing.
     */
    void initialize(Map config) 
        throws IllegalStateException, ServiceException;
}