package toolbox.workspace.prefs;

import javax.swing.JComponent;

import toolbox.workspace.IPreferenced;

/**
 * PreferencesPane is an interface for components that will be shown by the
 * Prefreences dialog box in a card like layout once the view's node is selected
 * in the configuration tree.
 */
public interface PreferencesView extends IPreferenced
{
    /**
     * The label of the view in the configuration tree.
     *
     * @return String
     */
    String getLabel();


    /**
     * Returns the views user interface component.
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
