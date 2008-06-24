package toolbox.util.ui.explorer.action;

import java.awt.event.ItemEvent;

import javax.swing.AbstractAction;

import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.explorer.JFileExplorerProxy;
import toolbox.util.ui.explorer.listener.DriveComboListener;

/**
 * Abstract base class for JFileExplorer directory actions that provides easy 
 * access to the file explorer and its proxy instance.
 */
public abstract class AbstractDirAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * Key for storing a reference to the file explorer.
     */
    private static final String KEY_EXPLORER = JFileExplorer.class.getName();
    
    /**
     * Key for storing a reference to the file explorer proxy. This is created
     * on demand.
     */
    private static final String KEY_PROXY = JFileExplorerProxy.class.getName();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AbstractListener.
     * 
     * @param explorer File explorer.
     */
    public AbstractDirAction(JFileExplorer explorer)
    {
        putValue(KEY_EXPLORER, explorer);
    }

    
    /**
     * Creates a AbstractDirAction.
     * 
     * @param name Name of this action.
     * @param explorer File explorer.
     */
    public AbstractDirAction(String name, JFileExplorer explorer)
    {
        super(name);
        putValue(KEY_EXPLORER, explorer);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Returns the file explorer.
     * 
     * @return JFileExplorer.
     */
    protected JFileExplorer getExplorer()
    {
        return (JFileExplorer) getValue(KEY_EXPLORER);
    }
    
    
    /**
     * Returns the proxy for the file explorer.
     * 
     * @return JFileExplorerProxy
     */
    protected JFileExplorerProxy getProxy()
    {
        JFileExplorerProxy proxy = (JFileExplorerProxy) getValue(KEY_PROXY);
        
        if (proxy == null)
        {
            proxy = new JFileExplorerProxy(getExplorer());
            putValue(KEY_PROXY, proxy);
        }
        
        return proxy;
    }


    /**
     * Refreshes the directory tree and selects the given folder after the 
     * refresh.
     * 
     * @param folder Directory to select after the refresh.
     */
    protected void refresh(String folder)
    {
        new DriveComboListener(
            getExplorer()).itemStateChanged(
                new ItemEvent(
                    getProxy().getRootsComboBox(), 
                    0, 
                    null, 
                    ItemEvent.SELECTED));
                
        getExplorer().selectFolder(folder);
    }
}