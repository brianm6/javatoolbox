package toolbox.workspace.prefs;

import javax.swing.JComponent;

/**
 * PreferencesPane is an interface for components that will be shown by the
 * Prefreences dialog box in a card like layout once the pane's node is selected
 * in the preferences tree view.
 */
public interface PreferencesPane
{
    public String getLabel();
    
    public JComponent getView();
    
    public void onOK();
    
    public void onApply();
    
    public void onCancel();
}
