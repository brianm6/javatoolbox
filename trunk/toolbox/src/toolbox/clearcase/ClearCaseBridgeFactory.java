package toolbox.clearcase;

/**
 * ClearCaseBridgeFactory is responsible for ___.
 */
public class ClearCaseBridgeFactory
{

    /**
     * Creates a ClearCaseBridgeFactory.
     * 
     */
    private ClearCaseBridgeFactory()
    {
    }

    
    public static final ClearToolBridge create() 
    {
        return new ClearToolBridge();
    }
}
