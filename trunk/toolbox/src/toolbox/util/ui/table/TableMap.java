package toolbox.util.ui.table;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/** 
 * In a chain of data manipulators some behaviour is common. TableMap
 * provides most of this behavour and can be subclassed by filters
 * that only need to override a handful of specific methods. TableMap 
 * implements TableModel by routing all requests to its model, and
 * TableModelListener by routing all events to its listeners. Inserting 
 * a TableMap which has not been subclassed into a chain of table filters 
 * should have no effect.
 *
 * @version 1.9 12/03/01
 * @author Philip Milne 
 */
public class TableMap extends AbstractTableModel implements TableModelListener
{
    /** Delegate table model */
    private TableModel model_;

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * @return  Table model
     */    
    public TableModel getModel()
    {
        return model_;
    }

    /**
     * Sets the table model
     * 
     * @param model Table model 
     */
    public void setModel(TableModel model)
    {
        model_ = model;
        model.addTableModelListener(this);
    }

    //--------------------------------------------------------------------------
    // Overrides AbstractTableModel
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */        
    public Object getValueAt(int aRow, int aColumn)
    {
        return model_.getValueAt(aRow, aColumn);
    }
        
    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int aRow, int aColumn)
    {
        model_.setValueAt(aValue, aRow, aColumn);
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount()
    {
        return (model_ == null) ? 0 : model_.getRowCount();
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount()
    {
        return (model_ == null) ? 0 : model_.getColumnCount();
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int aColumn)
    {
        return model_.getColumnName(aColumn);
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int aColumn)
    {
        return model_.getColumnClass(aColumn);
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int column)
    {
        return model_.isCellEditable(row, column);
    }
    
    //--------------------------------------------------------------------------
    // TableModelListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.event.TableModelListener#
     *          tableChanged(javax.swing.event.TableModelEvent)
     */
    public void tableChanged(TableModelEvent e)
    {
        // By default forward all events to all the listeners.
        fireTableChanged(e);
    }
}