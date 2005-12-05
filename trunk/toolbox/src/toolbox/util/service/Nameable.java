package toolbox.util.service;

/**
 * Implemented by objects which can be referenced by name.
 */
public interface Nameable {
    
    // --------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------

    /**
     * Javabean name property.
     */
    static final String PROP_NAME = "name";

    // --------------------------------------------------------------------------
    // Interface
    // --------------------------------------------------------------------------

    /**
     * Returns this object's name.
     * 
     * @return String
     */
    String getName();


    /**
     * Sets this object's name.
     * 
     * @param name Name to set.
     */
    void setName(String name);
}