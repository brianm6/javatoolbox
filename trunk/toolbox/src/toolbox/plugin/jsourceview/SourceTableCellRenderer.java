package toolbox.jsourceview;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import toolbox.util.MathUtil;
import toolbox.util.ui.table.SmartTableCellRenderer;

/**
 * Customized renderer for the contents of the source table.
 */   
public class SourceTableCellRenderer extends SmartTableCellRenderer
{
    /**
     * Formatter for whole numbers.
     */
    private DecimalFormat decimalFormatter_;
    
    /**
     * Formatter for percentages.
     */
    private NumberFormat percentFormatter_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SourceTableCellRenderer.
     */
    public SourceTableCellRenderer()
    {
        decimalFormatter_ = new DecimalFormat("###,###");
        percentFormatter_ = NumberFormat.getPercentInstance();
    }
    
    //--------------------------------------------------------------------------
    // Overrides javax.swing.table.DefaultTableCellRenderer
    //--------------------------------------------------------------------------
    
    /**
     * Returns the default table cell renderer.
     *
     * @param table JTable
     * @param value Value to assign to the cell at [row, column]
     * @param isSelected True if the cell is selected
     * @param hasFocus True if cell has focus
     * @param row Row of the cell to render
     * @param column Column of the cell to render
     * @return Default table cell renderer
     */
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column)
    {
        String text = value.toString();
        
        if (isSelected)
        {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        }
        else
        {
            setForeground(table.getForeground());

            // Alternate row background colors colors
                            
            if (MathUtil.isEven(row))
                setBackground(table.getBackground());
            else
                setBackground(new Color(240,240,240));
        }

        if (hasFocus)
        {
            setBorder(
                UIManager.getBorder("Table.focusCellHighlightBorder"));
                
            if (table.isCellEditable(row, column))
            {
                setForeground(
                    UIManager.getColor("Table.focusCellForeground"));
                    
                setBackground(
                    UIManager.getColor("Table.focusCellBackground"));
            }
        }
        else
            setBorder(noFocusBorder);

        switch (column)
        {
            case JSourceView.COL_NUM:
            
                setHorizontalAlignment(SwingConstants.CENTER);
                setValue(text);
                break;
                
            case JSourceView.COL_DIR:
            case JSourceView.COL_FILE:
            
                setHorizontalAlignment(SwingConstants.LEFT);
                setValue(text);
                break;
                
            case JSourceView.COL_CODE:
            case JSourceView.COL_COMMENTS:
            case JSourceView.COL_BLANK:
            case JSourceView.COL_THROWN_OUT:
            case JSourceView.COL_TOTAL:
            
                setHorizontalAlignment(SwingConstants.CENTER);
                setValue(decimalFormatter_.format(value));
                break;

            case JSourceView.COL_PERCENTAGE:
            
                setHorizontalAlignment(SwingConstants.CENTER);
                int i = ((Integer) value).intValue();
                Float f = new Float((float) i/100);
                setValue(percentFormatter_.format(f));
                break;
                
            default:
                setValue(value);
        }
        
        return this;
    }
}