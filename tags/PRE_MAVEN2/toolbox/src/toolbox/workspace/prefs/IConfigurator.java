package toolbox.workspace.prefs;

import javax.swing.Icon;
import javax.swing.JComponent;

import toolbox.workspace.IPreferenced;

/**
 * An IConfigurator is responsible for allowing an IPlugin's preferences to be
 * viewed and edited via a user interface.
 */
public interface IConfigurator extends IPreferenced
{
    /**
     * Returns the text label attached to this configurator. Should be suitable
     * for display on a button or treenode to activate or select this 
     * configurators user interface component.  
     *
     * @return String
     */
    String getLabel();


    /**
     * Returns this configurators user interface component used to view and
     * edit plugin preferences.
     *
     * @return JComponent
     */
    JComponent getView();


    /**
     * Returns the icon to associate with this configurator. The icon should
     * be suitable for display in a button, treenode, or tab panel.
     * 
     * @return Icon
     */
    Icon getIcon();
    
    
    /**
     * Called when the user chooses to accept the current set of preferences.
     */
    void onOK();


    /**
     * Called when the user chooses to apply the current set of preferences.
     */
    void onApply();


    /**
     * Called when the user chooses not to accept the current set of 
     * preferences.
     */
    void onCancel();
    
    
    /**
     * Returns true if this configurator's values are to be applied during the
     * initial startup process, false otherwise.
     * 
     * @return boolean
     */
    boolean isApplyOnStartup();
}