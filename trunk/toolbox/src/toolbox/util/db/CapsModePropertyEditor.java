package toolbox.util.db;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

/**
 * Propery editor for CapsMode that renders each of the available capitalization
 * modes in a combobox.
 */
public class CapsModePropertyEditor extends ComboBoxPropertyEditor
{
    /**
     * Creates a CapsModePropertyEditor.
     */
    public CapsModePropertyEditor()
    {
        Object[] values = new Object[]
        {
            CapsMode.LOWERCASE,
            CapsMode.UPPERCASE,
            CapsMode.PRESERVE
        };
        
        setAvailableValues(values);
    }
}

