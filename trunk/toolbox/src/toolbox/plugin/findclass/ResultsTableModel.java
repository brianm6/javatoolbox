package toolbox.plugin.findclass;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import toolbox.findclass.FindClassResult;

/**
 * Table model for the search results.
 */
public class ResultsTableModel extends AbstractTableModel
{
    // Table columns
    public static final int COL_NUM       = 0;
    public static final int COL_SOURCE    = 1;
    public static final int COL_CLASS     = 2;
    public static final int COL_SIZE      = 3;
    public static final int COL_TIMESTAMP = 4;

    /** 
     * Result table column headers 
     */    
    private String[] resultColumns_ = new String[] 
    {
        "Num", 
        "Source", 
        "Class File",
        "Size", 
        "Timestamp"
    };

    /**
     * Backing store for the list of results (FindClassResult).
     */
    private List results_;
    
    /**
     * Maps result # (not the row number) to its corresponding FindClassResult. 
     */
    private Map resultsMap_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ResultsTableModel.
     */
    public ResultsTableModel()
    {
        results_ = new ArrayList();
        resultsMap_ = new HashMap();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds a search result to the end of the table.
     * 
     * @param results Search result
     */        
    public void addResult(FindClassResult result)
    {
        results_.add(result);
        resultsMap_.put(results_.size()-1+"", result);
        fireTableRowsInserted(results_.size()-1, results_.size());
    }
    
    
    /**
     * Clears the table.
     */
    public void clear()
    {
        results_.clear();
        resultsMap_.clear();
        fireTableDataChanged();
    }
    
    
    /**
     * Retrieves the FindClassResult for the given result number.
     * 
     * @param number Result number (not row number)
     * @return FindClassResult or null if not found
     */
    public FindClassResult getResult(String number)
    {
        return (FindClassResult) resultsMap_.get(number);
    }
    
    //--------------------------------------------------------------------------
    // Overrides javax.swing.table.TableModel
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount()
    {
        return resultColumns_.length;
    }

    
    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount()
    {
        return results_.size();
    }

    
    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Object value = null;
        FindClassResult row = (FindClassResult) results_.get(rowIndex);
        
        switch (columnIndex)
        {
            case COL_NUM        : value = new Integer(rowIndex); break;
            case COL_SOURCE     : value = row.getClassLocation(); break;
            case COL_CLASS      : value = row.getClassFQN(); break;
            case COL_SIZE       : value = new Long(row.getFileSize()); break;
            case COL_TIMESTAMP  : value = row.getTimestamp(); break;
            default             : value = "";    
        }
        
        return value;
    }

    
    /**
     * Returns class associated with a given column. Needed for sorting
     * capability.
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex)
    {
        switch (columnIndex)
        {
            case COL_NUM        : return Integer.class;
            case COL_SOURCE     : return String.class;
            case COL_CLASS      : return String.class;
            case COL_SIZE       : return Integer.class;
            case COL_TIMESTAMP  : return Date.class;
            default             : return super.getColumnClass(columnIndex);
        }
    }

    
    /**
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column)
    {
        return resultColumns_[column];
    }
}
