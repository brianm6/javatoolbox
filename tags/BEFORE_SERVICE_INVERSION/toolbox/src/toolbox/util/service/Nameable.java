package toolbox.util.service;

/**
 * Entities which can be assigned a name implement this
 * Nameable interface.
 */
public interface Nameable
{
    /**
     * Returns this elements name.
     *
     * @return String
     */
    String getName();
    
    
    /**
     * Sets this elements name.
     *
     * @param name Name
     */
    void setName(String name);
}
