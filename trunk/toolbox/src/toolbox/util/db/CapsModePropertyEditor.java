package toolbox.util.db;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

import toolbox.util.StringUtil;

/**
 * Propery editor for CapsMode.
 */
public class CapsModePropertyEditor extends ComboBoxPropertyEditor
{
    public CapsModePropertyEditor()
    {
        System.out.println(StringUtil.banner("creating caps mode prop editor!!!"));
        
        Object[] values = new Object[]
        {
            CapsMode.LOWERCASE,
            CapsMode.UPPERCASE,
            CapsMode.PRESERVE
        };
        
        setAvailableValues(values);
    }
}

