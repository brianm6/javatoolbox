package toolbox.clearcase.adapter;


/**
 * ClearCaseAdapterFactory is responsible for ___.
 */
public class ClearCaseAdapterFactory
{

    /**
     * Creates a ClearCaseAdapterFactory.
     * 
     */
    private ClearCaseAdapterFactory()
    {
    }

    
    public static final ClearToolAdapter create() 
    {
        return new ClearToolAdapter();
    }
}
