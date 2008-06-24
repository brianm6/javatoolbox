package toolbox.plugin.uidefaults;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.lang.reflect.Modifier;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import toolbox.util.ui.JSmartLabel;

/**
 * Custom table cell renderer for showing Look and Feel resources including 
 * fonts, colors, and icons.
 */
public class UIDefaultsTableCellRenderer extends JSmartLabel 
    implements TableCellRenderer
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a UIDefaultsTableCellRenderer.
     */
    public UIDefaultsTableCellRenderer()
    {
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true); // MUST do this for background to show up.
    }

    //----------------------------------------------------------------------
    // TableCellRenderer Interface
    //----------------------------------------------------------------------
    
    /**
     * @see javax.swing.table.TableCellRenderer
     *      #getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(
        JTable table,
        Object sample,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column)
    {
        setBackground(null);
        setIcon(null);
        setText("");
        String sampleName = sample.getClass().getName();

        //
        // Handle colors
        //
        if (sample instanceof Color)
        {
            setBackground((Color) sample);
        }

        //
        // Handle fonts
        //
        else if (sample instanceof Font)
        {
            setText("Sample");
            setFont((Font) sample);
        }

        //
        // Skip over private classes
        //
        else if (Modifier.isPrivate(sample.getClass().getModifiers()))
        {
            ;
        }

        //
        // Some Icons just don't play nice
        //
        else if ((sampleName.indexOf("CheckBox") >= 0) ||
            (sampleName.indexOf("RadioButton") >= 0)   ||
            (sampleName.indexOf("InternalFrame") >= 0) ||
            (sampleName.indexOf("PaletteCloseIcon") >= 0))
        {
            ; // No op
        }

        //
        //  Handle icons
        //
        else if (sample instanceof Icon)
        {
            Icon icon = (Icon) sample;
            setIcon(icon);
            setText(icon.getIconWidth() + "x" + icon.getIconHeight());
        }

        return this;
    }
}