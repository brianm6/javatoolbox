package toolbox.util.service;

/**
 * Nameable interface is for entities which can be referenced via a name.
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
