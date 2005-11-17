package toolbox.clearcase.adapter;

import toolbox.clearcase.IClearCaseAdapter;

/**
 * Factory class for creating {@link toolbox.clearcase.IClearCaseAdapter}s.
 */
public class ClearCaseAdapterFactory {

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a ClearCaseAdapterFactory.
     */
    private ClearCaseAdapterFactory() {
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------

    /**
     * Creates a default implementation of a IClearCaseAdapter.
     * 
     * @return ClearToolAdapter
     */
    public static final IClearCaseAdapter create() {
        return new ClearToolAdapter();
    }
}