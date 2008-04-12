package toolbox.util.ui.explorer.listener;

import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.explorer.JFileExplorerProxy;

/**
 * Abstract base class for JFileExplorer listeners that provides easy access
 * to the file explorer or its proxy instance.
 */
public abstract class AbstractListener
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * File explorer.
     */
    private JFileExplorer explorer_;
    
    /**
     * Proxy for the file explorer so this listener can have access to 
     * protected members of JFileExplorer.
     */
    private JFileExplorerProxy proxy_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AbstractListener.
     */
    public AbstractListener(JFileExplorer explorer)
    {
        explorer_ = explorer;
    }
    
    
    /**
     * Returns the file explorer.
     * 
     * @return JFileExplorer.
     */
    public JFileExplorer getExplorer()
    {
        return explorer_;
    }
    
    
    /**
     * Returns the proxy for the file explorer.
     * 
     * @return JFileExplorerProxy
     */
    public JFileExplorerProxy getProxy()
    {
        if (proxy_ == null)
            proxy_ = new JFileExplorerProxy(getExplorer());
        
        return proxy_;
    }
}
