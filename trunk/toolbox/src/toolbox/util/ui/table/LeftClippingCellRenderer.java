package toolbox.util.ui.table;

import java.awt.Component;
import java.awt.FontMetrics;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 * LeftClippingCellRenderer left clips text in a table cell instead of the right
 * which is the default. Also sets the tooltip to the full value of the text.
 * <p>
 * Example:
 * <pre>
 * A cell with a value of "Hello World" with a width of 6 will show "...rld"
 * </pre>
 */
public class LeftClippingCellRenderer extends SmartTableCellRenderer
{
    // TODO: Update to use Decorator pattern ala BorderedCellRenderer
    
    //--------------------------------------------------------------------------
    // Overrides DefaultTableCellRender
    //--------------------------------------------------------------------------
    
    /*
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
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
            super.getTableCellRendererComponent(
                table, clippedText, isSelected, hasFocus, row, column);
        
        setToolTipText((String) value);
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
        
        int widthForPainting;
        int stringWidth;
        int strValueLen = strValue.length();
        
        // I needed to give some extra space to get the clipping to work
        // correctly. Dirty but works.
        int extraSpace = 
            SwingUtilities.computeStringWidth(getFontMetrics(getFont()), "W");
        
        int availableWidth = 
            table.getCellRect(row, column, false).width - extraSpace;
        
        widthForPainting = 
            availableWidth - getInsets().left + getInsets().right;
        
        FontMetrics fm = getFontMetrics(this.getFont());
        
        stringWidth = 
            SwingUtilities.computeStringWidth(
                getFontMetrics(getFont()), strValue);
        
        int index = 0;
        
        if (widthForPainting > 0 && stringWidth > widthForPainting)
        {
            while (stringWidth > widthForPainting && index < strValueLen)
            {
                index += 1;
                String testStr = "..." + strValue.substring(index);
                stringWidth = fm.stringWidth(testStr);
            }
            
            strValue = "..." + strValue.substring(index);
        }
        
        return strValue;
    }
}