package toolbox.util.ui.table;

import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

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
    protected TableModel model_;

    public TableModel getModel()
    {
        return model_;
    }

    public void setModel(TableModel model)
    {
        model_ = model;
        model.addTableModelListener(this);
    }

    // By default, Implement TableModel by forwarding all messages 
    // to the model. 

    public Object getValueAt(int aRow, int aColumn)
    {
        return model_.getValueAt(aRow, aColumn);
    }

    public void setValueAt(Object aValue, int aRow, int aColumn)
    {
        model_.setValueAt(aValue, aRow, aColumn);
    }

    public int getRowCount()
    {
        return (model_ == null) ? 0 : model_.getRowCount();
    }

    public int getColumnCount()
    {
        return (model_ == null) ? 0 : model_.getColumnCount();
    }

    public String getColumnName(int aColumn)
    {
        return model_.getColumnName(aColumn);
    }

    public Class getColumnClass(int aColumn)
    {
        return model_.getColumnClass(aColumn);
    }

    public boolean isCellEditable(int row, int column)
    {
        return model_.isCellEditable(row, column);
    }
    
    //
    // Implementation of the TableModelListener interface, 
    //

    // By default forward all events to all the listeners. 
    public void tableChanged(TableModelEvent e)
    {
        fireTableChanged(e);
    }
}