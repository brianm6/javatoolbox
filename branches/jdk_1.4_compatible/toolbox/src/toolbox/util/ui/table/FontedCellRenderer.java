package toolbox.util.ui.table;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Decorates a TableCellRenderer with a font. Saves from having to create a
 * new TableCellRenderer implementing class by using the Decorator pattern.
 * 
 * @see toolbox.util.ui.table.BorderedCellRenderer
 */
public class FontedCellRenderer implements TableCellRenderer {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    /**
     * Renderer which is being decorated by this renderer.
     */
    private TableCellRenderer delegate_;
    
    /**
     * Font to decorate with.
     */
    private Font font_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
     * Creates a FontedCellRenderer.
     * 
     * @param delegate Renderer to decorate with a font.
     * @param font Font to set on the cell renderer.
     */
    public FontedCellRenderer(TableCellRenderer delegate, Font font) {
        delegate_ = delegate;
        setFont(font);
    }
    
    // -------------------------------------------------------------------------
    // TableCellRenderer Interface
    // -------------------------------------------------------------------------
    
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {

        JComponent c = (JComponent) 
            delegate_.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        c.setFont(getFont());
        return c;
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    public Font getFont() {
        return font_;
    }

    
    public void setFont(Font font) {
        font_ = font;
    }
}