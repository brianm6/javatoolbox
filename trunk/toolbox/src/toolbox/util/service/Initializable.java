package toolbox.util.service;

import java.util.Map;

/**
 * Implementors of Initializable are initialized once and only once throughout
 * the lifecycle of the object.
 * 
 * @see toolbox.util.service.Destroyable
 */
public interface Initializable
{
    /**
     * Initializes the service with optional configuration informatin.
     * 
     * @param configuration Configuration information for this service. Use
     *        {@link Collections#EMPTY_MAP} for no configuration.
     * @throws ServiceException if the service encounters problems initializing.
     */
    void initialize(Map configuration) throws ServiceException;
}