package toolbox.util.ui.table;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import toolbox.util.ResourceUtil;

/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel) 
 * and itself implements TableModel. TableSorter does not store or copy 
 * the data in the TableModel, instead it maintains an array of 
 * integers which it keeps the same size as the number of rows in its 
 * model. When the model changes it notifies the sorter that something 
 * has changed eg. "rowsAdded" so that its internal array of integers 
 * can be reallocated. As requests are made of the sorter (like 
 * getValueAt(row, col) it redirects them to its model via the mapping 
 * array. That way the TableSorter appears to hold another copy of the table 
 * with the rows in a different order. The sorting algorthm used is stable 
 * which means that it does not move around rows when its comparison 
 * function returns 0 to denote that they are equivalent. 
 *
 * <p>
 * Extended toolbox version provides the following additional functionality:
 * <ul>
 * <li>
 * The sorter can be enabled/disabled. This is useful when populating a 
 * table with a large amount of data. If populating a row at a time, a sort
 * is executed for each change in the table model thus making it very
 * inefficient. Example: 
 * <pre>
 * TableSorter sorter = new TableSorter(myModel);
 * sorter.setEnabled(false);
 * // do lots of model manipulation 
 * sorter.setEnabled(true);
 * </pre>
 * </li>
 * <li>
 * It is no longer necessary to press &lt;shift&gt; + &lt;left click&gt; on the 
 * header to trigger a reverse sort. Just click on the header again and the
 * sort order will be reversed.
 * </li>
 * </ul>
 *
 * @author Philip Milne (original)
 */
public class TableSorter extends TableMap
{
    private static final Logger logger_ = 
        Logger.getLogger(TableSorter.class);
    
    private int     indexes_[];
    private List    sortingColumns_;
    private boolean ascending_;
    private int     sortingColumn_;
    private int     compares_;
    private boolean enabled_;
    private Icon    forwardSortIcon_;
    private Icon    reverseSortIcon_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public TableSorter()
    {
        indexes_ = new int[0]; // For consistency.
        init();        
    }
    
    /**
     * Creates a TableSorter for the given TableModel
     * 
     * @param  model Model to provide a sorted view of
     */
    public TableSorter(TableModel model)
    {
        setModel(model);
        init();
    }
    
    //--------------------------------------------------------------------------
    // Overrides toolbox.ui.table.TableMap
    //--------------------------------------------------------------------------
    
    /**
     * Sets the model
     * 
     * @param  model  Table model
     */
    public void setModel(TableModel model)
    {
        super.setModel(model);
        reallocateIndexes();
    }

    /**
     * Gets table cell value
     * 
     * @param  row      Row number
     * @param  column   Column number
     * @return Table cell value at the given coordinates 
     */
    public Object getValueAt(int row, int column)
    {
        // The mapping only affects the contents of the data rows.
        // Pass all requests to these rows through the mapping array: "indexes".
        
        checkModel();
        return getModel().getValueAt(indexes_[row], column);
    }
    
    /**
     * Sets table cell value
     * 
     * @param  value   Cell value
     * @param  row     Row number
     * @param  column  Column number
     */
    public void setValueAt(Object value, int row, int column)
    {
        checkModel();
        getModel().setValueAt(value, indexes_[row], column);
    }
    
    //--------------------------------------------------------------------------
    // Interface javax.swing.table.TableModelListener
    //--------------------------------------------------------------------------
    
    /**
     * Notification that the table model has changed
     * 
     * @param  e  Table model event
     */
    public void tableChanged(TableModelEvent e)
    {
        reallocateIndexes();
        
        if (enabled_)
        {
            // Table data has changed.  Force a resort.
            sort(this);
        }
            
        super.tableChanged(e);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sorts by the given column
     * 
     * @param  column  Column index
     */
    public void sortByColumn(int column)
    {
        sortByColumn(column, true);
    }
    
    /**
     * Sorts by the given column
     * 
     * @param column     Column index
     * @param ascending  Sort ascending
     */
    public void sortByColumn(int column, boolean ascending)
    {
        sortingColumn_ = column;
        ascending_ = ascending;
        sortingColumns_.clear();
        sortingColumns_.add(new Integer(column));
        sort(this);
        super.tableChanged(new TableModelEvent(this));
    }

    /**
     * Shortcut to add mouse listener to the jtables internal table header
     * 
     * @param table  Table with header
     */    
    public void addMouseListenerToHeaderInTable(JTable table)
    {
        // There is no-where else to put this. 
        // Add a mouse listener to the Table to trigger a table sort 
        // when a column heading is clicked in the JTable. 
        
        table.setColumnSelectionAllowed(false);
        MouseAdapter sortListener = new SortingMouseListener(table);
        JTableHeader th = table.getTableHeader();
        th.addMouseListener(sortListener);
        th.setDefaultRenderer(createDefaultRenderer());
    }

    /**
     * @return  True if sorter is enabled, false otherwise
     */
    public boolean isEnabled()
    {
        return enabled_;
    }

    /**
     * @param  b  Flag to enable/disable the sorter
     */
    public void setEnabled(boolean b)
    {
        enabled_ = b;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Initializes the TableSorter fields and icons
     */
    protected void init()
    {
        sortingColumns_  = new ArrayList();
        ascending_       = true;
        sortingColumn_   = -1;
        enabled_         = true;
        
        forwardSortIcon_ = 
            ResourceUtil.getResourceAsIcon(
                "/toolbox/util/ui/images/SortAscending.gif");
                
        reverseSortIcon_ = 
            ResourceUtil.getResourceAsIcon(
                "/toolbox/util/ui/images/SortDescending.gif");
    }

    /**
     * Compares rows by column
     * 
     * @param  row1    First row index
     * @param  row2    Second row index
     * @param  column  Column index
     * @return 1 if row1 > row2, 0 if row1 == row2, -1 if row1 < row2
     */    
    protected int compareRowsByColumn(int row1, int row2, int column)
    {
        Class type = getModel().getColumnClass(column);
        TableModel data = getModel();
        
        // Check for nulls
        Object o1 = data.getValueAt(row1, column);
        Object o2 = data.getValueAt(row2, column);
        
        if (o1 == null && o2 == null)
        {
            // If both values are null return 0
            return 0;
        }
        else if (o1 == null)
        { 
            // Define null less than everything. 
            return -1;
        }
        else if (o2 == null)
        {
            return 1;
        }
        
        // We copy all returned values from the getValue call in case
        // optimised model is reusing one object to return many values.
        // The Number subclasses in the JDK are immutable and so will not be 
        // used in this way but other subclasses of Number might want to do 
        // this to save space and avoid unnecessary heap allocation. 
        
        if (type.getSuperclass() == Number.class)
        {
            Number n1 = (Number) data.getValueAt(row1, column);
            double d1 = n1.doubleValue();
            Number n2 = (Number) data.getValueAt(row2, column);
            double d2 = n2.doubleValue();
            
            if (d1 < d2)
                return -1;
            else if (d1 > d2)
                return 1;
            else
                return 0;
        }
        else if (type == Date.class)
        {
            Date d1 = (Date) data.getValueAt(row1, column);
            long n1 = d1.getTime();
            Date d2 = (Date) data.getValueAt(row2, column);
            long n2 = d2.getTime();
            
            if (n1 < n2)
                return -1;
            else if (n1 > n2)
                return 1;
            else
                return 0;
        }
        else if (type == String.class)
        {
            String s1 = (String) data.getValueAt(row1, column);
            String s2 = (String) data.getValueAt(row2, column);
            int result = s1.compareTo(s2);
            
            if (result < 0)
                return -1;
            else if (result > 0)
                return 1;
            else
                return 0;
        }
        else if (type == Boolean.class)
        {
            Boolean bool1 = (Boolean) data.getValueAt(row1, column);
            boolean b1 = bool1.booleanValue();
            Boolean bool2 = (Boolean) data.getValueAt(row2, column);
            boolean b2 = bool2.booleanValue();
            
            if (b1 == b2)
                return 0;
            else if (b1) // Define false < true
                return 1;
            else
                return -1;
        }
        else
        {
            Object v1 = data.getValueAt(row1, column);
            String s1 = v1.toString();
            Object v2 = data.getValueAt(row2, column);
            String s2 = v2.toString();
            int result = s1.compareTo(s2);
            
            if (result < 0)
                return -1;
            else if (result > 0)
                return 1;
            else
                return 0;
        }
    }
    
    /**
     * Compares two rows
     * 
     * @param   row1  First row index
     * @param   row2  Second row index
     * @return  1 if row1 > row2, 0 if row1 == row2, -1 if row1 < row2
     */
    protected int compare(int row1, int row2)
    {
        compares_++;
        
        for (int level = 0; level < sortingColumns_.size(); level++)
        {
            Integer column = (Integer) sortingColumns_.get(level);
            int result = compareRowsByColumn(row1, row2, column.intValue());
            if (result != 0)
                return ascending_ ? result : -result;
        }
        
        return 0;
    }
    
    /**
     * Reallocates the indexes because of a possible change in the table model
     */
    protected void reallocateIndexes()
    {
        int rowCount = getModel().getRowCount();
        
        // Set up a new array of indexes with the right number of elements
        // for the new data model.
        indexes_ = new int[rowCount];
        
        // Initialise with the identity mapping.
        for (int row = 0; row < rowCount; row++)
            indexes_[row] = row;
    }

    /**
     * Checks that the table sorder index is in sync with the model of the
     * original table.
     */    
    protected void checkModel()
    {
        if (indexes_.length != getModel().getRowCount())
            logger_.error("Sorter not informed of a change in model.");
    }
    
    /**
     * Sorts the table
     * 
     * @param sender  Initiater of the sort (currently unused)
     */
    protected void sort(Object sender)
    {
        checkModel();
        compares_ = 0;
        
        //qsort(0, indexes_.length-1);
        shuttlesort((int[]) indexes_.clone(), indexes_, 0, indexes_.length);
        
        logger_.debug("Sorting...compares=" + compares_);
    }

    /**
     * Implementation of a fast sorting strategy
     * 
     * @param  from  Source
     * @param  to    Destination
     * @param  low   Start index
     * @param  high  End index
     */    
    protected void shuttlesort(int from[], int to[], int low, int high)
    {
        // This is a home-grown implementation which we have not had time
        // to research - it may perform poorly in some circumstances. It
        // requires twice the space of an in-place algorithm and makes
        // NlogN assigments shuttling the values between the two
        // arrays. The number of compares appears to vary between N-1 and
        // NlogN depending on the initial order but the main reason for
        // using it here is that, unlike qsort, it is stable.
        
        if (high - low < 2)
        {
            return;
        }
        
        int middle = (low + high) / 2;
        shuttlesort(to, from, low, middle);
        shuttlesort(to, from, middle, high);
        int p = low;
        int q = middle;
        
        // This is an optional short-cut; at each recursive call,
        // check to see if the elements in this subset are already
        // ordered.  If so, no further comparisons are needed; the
        // sub-array can just be copied.  The array must be copied rather
        // than assigned otherwise sister calls in the recursion might
        // get out of sinc.  When the number of elements is three they
        // are partitioned so that the first set, [low, mid), has one
        // element and and the second, [mid, high), has two. We skip the
        // optimisation when the number of elements is three or less as
        // the first compare in the normal merge will produce the same
        // sequence of steps. This optimisation seems to be worthwhile
        // for partially ordered lists but some analysis is needed to
        // find out how the performance drops to Nlog(N) as the initial
        // order diminishes - it may drop very quickly.
        
        if (high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0)
        {
            for (int i = low; i < high; i++)
            {
                to[i] = from[i];
            }
            return;
        }
        
        // A normal merge. 
        for (int i = low; i < high; i++)
        {
            if (q >= high || (p < middle && compare(from[p], from[q]) <= 0))
            {
                to[i] = from[p++];
            }
            else
            {
                to[i] = from[q++];
            }
        }
    }

    /**
     * Swaps two indices in the indexes_ array
     *
     * @param  i  First index
     * @param  j  Second index
     */    
    protected void swap(int i, int j)
    {
        int tmp = indexes_[i];
        indexes_[i] = indexes_[j];
        indexes_[j] = tmp;
    }

    /**
     * @return Table header cell renderer
     */    
    protected TableCellRenderer createDefaultRenderer()
    {
        DefaultTableCellRenderer label = new TableHeaderCellRenderer();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setHorizontalTextPosition(SwingConstants.LEADING);
        label.setIconTextGap(10);
        return label;
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Listens for mouse clicks on the table header cells to trigger sorting 
     */
    private final class SortingMouseListener extends MouseAdapter
    {
        private final JTable tableView_;
        
        private SortingMouseListener(JTable tableView)
        {
            tableView_ = tableView;
        }
        
        public void mouseClicked(MouseEvent e)
        {
            TableColumnModel columnModel = tableView_.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = tableView_.convertColumnIndexToModel(viewColumn);
            
            if (e.getClickCount() == 1 && column != -1)
                sortByColumn(column, !ascending_);
            
            tableView_.getTableHeader().repaint();
        }
    }
    
    /**
     * Renders the ascending/descending sort direction graphic in the table
     * header cell. 
     */
    private final class TableHeaderCellRenderer extends DefaultTableCellRenderer
    {
        public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column)
        {
            if (table != null)
            {
                JTableHeader header = table.getTableHeader();
                
                if (header != null)
                {
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                    
                    if (column == sortingColumn_)
                    {
                        if (ascending_)
                            setIcon(forwardSortIcon_);
                        else
                            setIcon(reverseSortIcon_);
                    }
                    else
                        setIcon(null);
                }
            }
            
            setText((value == null) ? "" : value.toString());
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            return this;
        }
    }
}