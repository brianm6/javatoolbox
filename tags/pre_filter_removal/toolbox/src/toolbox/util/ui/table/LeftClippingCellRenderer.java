package toolbox.util.ui.table;

import java.awt.Component;
import java.awt.FontMetrics;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 * LeftClippingRenderer left clips text in a table cell.
 */
public class LeftClippingCellRenderer extends SmartTableCellRenderer
{
    //--------------------------------------------------------------------------
    // Overrides DefaultTableCellRender
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(
     *      javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(
        JTable table, 
        Object value,
        boolean isSelected, 
        boolean hasFocus, 
        int row, 
        int column)
    {
        String clippedText = getLeftClippedText(
            (String) value, table, row, column);
        
        Component component = super.getTableCellRendererComponent(
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
        int column)
    {
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