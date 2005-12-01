package toolbox.util.ui.table;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

/**
 * LeftClippingCellRenderer left clips text in a table cell instead of the right
 * which is the default. Also sets the tooltip to the full value of the text.
 * <p>
 * Example:
 * <pre>
 * A cell with a value of "Hello World" with a width of 6 will show "...rld"
 * </pre>
 */
public class LeftClippingCellRenderer implements TableCellRenderer {

    private static final String CLIP = "..";
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private TableCellRenderer delegate_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public LeftClippingCellRenderer(TableCellRenderer delegate) {
        
        if (!(delegate instanceof JComponent)) 
            throw new IllegalArgumentException(
                "Delegate must be ain instnace of JComponent");
        
        delegate_ = delegate;
    }
    
    //--------------------------------------------------------------------------
    // TableCellRenderer Interface
    //--------------------------------------------------------------------------
    
    public Component getTableCellRendererComponent(
        JTable table, 
        Object value,
        boolean isSelected, 
        boolean hasFocus, 
        int row, 
        int column) {
        
        String clippedText = 
            getLeftClippedText(value.toString(), table, row, column);
        
        Component component = 
            delegate_.getTableCellRendererComponent(
                table, clippedText, isSelected, hasFocus, row, column);
        
        // TODO: Move tooltip to another Decorator
        //delegate_.setToolTipText((String) value);
        
        return component;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Returns the clipped text.
     * 
     * @param strValue Text string.
     * @param table Table.
     * @param row Row number.
     * @param column Column number.
     * @return String
     */
    protected String getLeftClippedText(
        String strValue, 
        JTable table, 
        int row,
        int column) {
        
        JComponent c = (JComponent) delegate_;
        Font font = c.getFont();
        FontMetrics fontMetrics = c.getFontMetrics(font);
        
        // I needed to give some extra space to get the clipping to work
        // correctly. Dirty but works.
        int extraSpace = SwingUtilities.computeStringWidth(fontMetrics, "W");
        
        int availableWidth = 
            table.getCellRect(row, column, false).width - extraSpace;
        
        Insets insets = c.getInsets();
        int widthForPainting = availableWidth - insets.left + insets.right;
        int stringWidth = SwingUtilities.computeStringWidth(fontMetrics, strValue);
        int index = 0;
        
        if (widthForPainting > 0 && stringWidth > widthForPainting) {
            
            while (stringWidth > widthForPainting && index < strValue.length()) {
                index += 1;
                String testStr = CLIP + strValue.substring(index);
                stringWidth = fontMetrics.stringWidth(testStr);
            }
            
            strValue = CLIP + strValue.substring(index);
        }
        
        return strValue;
    }
}