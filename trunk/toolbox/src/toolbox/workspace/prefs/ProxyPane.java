package toolbox.workspace.prefs;

import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * ProxyPane is responsible for ___.
 */
public class ProxyPane extends JPanel implements PreferencesPane
{

    /**
     * Creates a ProxyPane.
     * 
     * 
     */
    public ProxyPane()
    {
        super();
    }

    //--------------------------------------------------------------------------
    // PreferencesPane Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.prefs.PreferencesPane#getLabel()
     */
    public String getLabel()
    {
        return "HTTP Proxy";
    }
    
    
    /**
     * @see toolbox.workspace.prefs.PreferencesPane#getView()
     */
    public JComponent getView()
    {
        return this;
    }


    /**
     * @see toolbox.workspace.prefs.PreferencesPane#onOK()
     */
    public void onOK()
    {
        
    }


    /**
     * @see toolbox.workspace.prefs.PreferencesPane#onApply()
     */
    public void onApply()
    {
        
    }


    /**
     * @see toolbox.workspace.prefs.PreferencesPane#onCancel()
     */
    public void onCancel()
    {
        
    }
}