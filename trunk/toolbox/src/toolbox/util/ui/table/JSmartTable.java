package toolbox.util.ui.table;

import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;

/**
 * JSmartTable adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 * 
 * @see JSmartTableHeader
 * @see JSmartTableModel
 * @see SmartTableCellRender
 */
public class JSmartTable extends JTable implements AntiAliased
{
    /**
     * Antialiased flag
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartTable
     */
    public JSmartTable()
    {
        setTableHeader(new JSmartTableHeader());        
    }

    /**
     * Creates a JSmartTable
     * 
     * @param numRows Number of rows
     * @param numColumns Number of columns
     */
    public JSmartTable(int numRows, int numColumns)
    {
        super(numRows, numColumns);
        setTableHeader(new JSmartTableHeader());
    }

    /**
     * Creates a JSmartTable
     * 
     * @param dm Table model
     */
    public JSmartTable(TableModel dm)
    {
        super(dm);
        setTableHeader(new JSmartTableHeader());
    }

    /**
     * Creates a JSmartTable
     * 
     * @param rowData Row data
     * @param columnNames Column names
     */
    public JSmartTable(Object[][] rowData, Object[] columnNames)
    {
        super(rowData, columnNames);
        setTableHeader(new JSmartTableHeader());
    }

    /**
     * Creates a JSmartTable
     * 
     * @param rowData Row data
     * @param columnNames Column names
     */
    public JSmartTable(Vector rowData, Vector columnNames)
    {
        super(rowData, columnNames);
        setTableHeader(new JSmartTableHeader());
    }

    /**
     * Creates a JSmartTable
     * 
     * @param dm Table model
     * @param cm Column model
     */
    public JSmartTable(TableModel dm, TableColumnModel cm)
    {
        super(dm, cm);
        setTableHeader(new JSmartTableHeader(cm));
    }

    /**
     * Creates a JSmartTable
     * 
     * @param dm Table model
     * @param cm Column model
     * @param sm Selection model
     */
    public JSmartTable(
        TableModel dm,
        TableColumnModel cm,
        ListSelectionModel sm)
    {
        super(dm, cm, sm);
        setTableHeader(new JSmartTableHeader(cm));
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Initializes the table
     */
    protected void init()
    {

    }
    
    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAlias()
     */
    public boolean isAntiAliased()
    {
        return antiAliased_;
    }

    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAliased_ = b;
        
        JTableHeader header = getTableHeader();
        
        if ((header != null) && (header instanceof AntiAliased))
            ((AntiAliased) header).setAntiAliased(b); 
            
        for (int i=0, c = getColumnCount(); i<c; i++)
        {
            TableCellRenderer tcr = getCellRenderer(0, i);
            
            if (tcr != null && tcr instanceof AntiAliased)
                ((AntiAliased) tcr).setAntiAliased(b);
        }            
    }

    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}