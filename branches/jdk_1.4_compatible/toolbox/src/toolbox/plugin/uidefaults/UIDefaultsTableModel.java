package toolbox.plugin.uidefaults;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Custom table model so show components characteristics.
 */
class UIDefaultsTableModel extends AbstractTableModel
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Reference to the table cell renderer.
     */
    private final TableCellRenderer tableCellRenderer_;

    /**
     * Column names.
     */
    private String[] columnNames_;
    
    /**
     * Row data.
     */
    private Object[][] rowData_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a UIDefaultsTableModel.
     *
     * @param tableCellRenderer The specialized table cell renderer.
     * @param rowData Row data.
     * @param columnNames Column names.
     */
    public UIDefaultsTableModel(
        TableCellRenderer tableCellRenderer, 
        Object[][] rowData, 
        String[] columnNames)
    {
        tableCellRenderer_ = tableCellRenderer;
        rowData_ = rowData;
        columnNames_ = columnNames;
    }

    //----------------------------------------------------------------------
    // TableModel Interface
    //----------------------------------------------------------------------
    
    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount()
    {
        return columnNames_.length;
    }


    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount()
    {
        return rowData_.length;
    }


    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int col)
    {
        return columnNames_[col];
    }


    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col)
    {
        return rowData_[row][col];
    }


    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int c)
    {
        Object o;

        if (c == 2)
            o = tableCellRenderer_;
        else
            o = getValueAt(0, c);

        return o.getClass();
    }


    /**
     * @see javax.swing.table.TableModel#setValueAt(
     *      java.lang.Object, int, int)
     */
    public void setValueAt(Object value, int row, int col)
    {
        rowData_[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}