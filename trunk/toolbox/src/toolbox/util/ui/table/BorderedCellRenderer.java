package toolbox.util.ui.table;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Decorator for a TableCellRenderer that is based on a JComponent which adds
 * to the existing border of the component on the inside.
 */
public class BorderedCellRenderer implements TableCellRenderer {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    /**
     * Renderer which is being decorated by this renderer.
     */
    private TableCellRenderer delegate_;
    
    /**
     * Border to decorate with.
     */
    private Border border_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
     * Creates a BorderedTabledCellRenderer for the given renderer. The passed
     * in renderer must be in instance of JComponent in order to apply a border
     * to it.
     * 
     *  @param delegate Renderer to decorate with a border.
     *  @param border Border to decorate the passed in renderer with.
     */
    public BorderedCellRenderer(TableCellRenderer delegate, Border border) {
        
        if (!(delegate instanceof JComponent))
            throw new IllegalArgumentException(
                "Renderer must be based on a JComponent");
            
        delegate_ = delegate;
        setBorder(border);
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
        
        CompoundBorder paddedBorder = 
            new CompoundBorder(
                c.getBorder(),  
                getBorder());
        
        c.setBorder(paddedBorder);
        return c;
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    public Border getBorder() {
        return border_;
    }

    
    public void setBorder(Border border) {
        border_ = border;
    }
}