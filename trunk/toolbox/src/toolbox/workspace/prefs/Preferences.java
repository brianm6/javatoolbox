package toolbox.workspace.prefs;

import javax.swing.JComponent;

import toolbox.workspace.IPreferenced;

/**
 * Preferences is an interface for components that will be shown by the
 * Preferences dialog box in a card like layout once the view's node is selected
 * in the configuration tree.
 */
public interface Preferences extends IPreferenced
{
    /**
     * The label of the node in the tree that activates this preferences view.  
     *
     * @return String
     */
    String getLabel();


    /**
     * Returns this preferences user interface component.
     *
     * @return JComponent
     */
    JComponent getView();


    /**
     * Called when the user click on the OK button to accept the current
     * preferences.
     */
    void onOK();


    /**
     * Called when the user clicks on the Apply button to apply the current
     * preferences.
     */
    void onApply();


    /**
     * Called when the user clicks on the Cancel button to dismiss the
     * preferences dialog box without saving any changed preferences.
     */
    void onCancel();
}