package toolbox.util.ui.table;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;

/**
 * JSmartTableHeader adds the following features to the default implementation
 * of JTableHeader.<p>
 * <ul>
 *   <li>AntiAliased column headers
 *   <li>Column resizes to minimum width by double clicking on the column 
 *       divider +/- a few pixels to the left or the right.
 * </ul>
 * Works best with table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
 */
public class JSmartTableHeader extends JTableHeader implements AntiAliased
{
    private static final Logger logger_ = 
        Logger.getLogger(JSmartTableHeader.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Maximum number of horizontal pixels from the divider that the mouse can 
     * be clicked double clicked to trigger an automatic resize of the column.
     * Defaults to 3 pixels.
     */
    private static final int RESIZE_PROXIMITY = 3;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JSmartTableHeader.
     */
    public JSmartTableHeader()
    {
    }


    /**
     * Creates a JSmartTableHeader.
     *
     * @param cm Column model.
     */
    public JSmartTableHeader(TableColumnModel cm)
    {
        this(cm, null);
    }

    
    /**
     * Creates a JSmartTableHeader.
     * 
     * @param cm Column model.
     * @param table JTable which this header is going to belong.
     */
    public JSmartTableHeader(TableColumnModel cm, JTable table)
    {
        super(cm);
        setTable(table);
        addMouseListener(new DoubleClickListener());
    }
    
    
    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAliased()
     */
    public boolean isAntiAliased()
    {
        return antiAliased_;
    }

    
    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAliased(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAliased_ = b;
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
    
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Returns the minimum size for a given column based on its contents.
     * 
     * @param column Column to compute minimum size for.
     * @return int
     */
    private int getRequiredColumnWidth(TableColumn column)
    {
        int modelIndex = column.getModelIndex();
        TableCellRenderer renderer;
        Component component;
        int requiredWidth = 0;
        JTable table = getTable();
        int rows = table.getRowCount();
        
        for (int i = 0; i < rows; i++)
        {
            renderer = table.getCellRenderer(i, modelIndex);
            Object valueAt = table.getValueAt(i, modelIndex);
            
            component = renderer.getTableCellRendererComponent(
                table, valueAt, false, false, i, modelIndex);
            
            //logger_.debug("requiredWidth = " + requiredWidth);
            
            requiredWidth = Math.max(
                requiredWidth, component.getPreferredSize().width + 2);
        }
        
        return requiredWidth;
    }


    /**
     * Gets the column to be resized if the mouse click occured close enough to
     * the column divider.
     * 
     * @param p Point of mouse click.
     * @param column Column number.
     * @return TableColumn
     */
    private TableColumn getResizingColumn(Point p, int column)
    {
        logger_.debug("getResizingColumn " + p + " " + column);
        
        if (column == -1)
        {
            logger_.debug("return -- column == -1");
            return null;
        }
        
        Rectangle r = getHeaderRect(column);
        
        // increase to proximity from divider to qualify as a auto-resize
        r.grow(-RESIZE_PROXIMITY, 0);  
        
        if (r.contains(p))
        {
            return null;
        }
        
        int midPoint = r.x + r.width / 2;
        int columnIndex;
        
        if (getComponentOrientation().isLeftToRight())
        {
            columnIndex = (p.x < midPoint) ? column - 1 : column;
        }
        else
        {
            columnIndex = (p.x < midPoint) ? column : column - 1;
        }
        
        if (columnIndex == -1)
        {
            return null;
        }
        
        return getColumnModel().getColumn(columnIndex);
    }
    
    
    //--------------------------------------------------------------------------
    // DoubleClickListener
    //--------------------------------------------------------------------------
    
    /**
     * DoubleClickListener listens for a double click on the the column dividers
     * so that the column to the left of the divider can be automatically
     * resized to its minimum width.
     */
    class DoubleClickListener extends MouseAdapter
    {
        /**
         * @see java.awt.event.MouseAdapter#mouseClicked(
         *      java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e)
        {
            logger_.debug("doMouseClicked");
            
            // Make sure table has been set
            if (getTable() == null)
            {
                logger_.debug("return -- table not set");
                return;
            }   
            
            // Make sure column can be resized
            if (!getResizingAllowed())
            {
                logger_.debug("return -- resizing not allowed");
                return;
            }
            
            // Make sure its a double click
            if (e.getClickCount() != 2)
            {
                logger_.debug("return -- click count != 2");
                return;
            }
            
            // Find out which column we're targeting
            TableColumn column = 
                getResizingColumn(
                    e.getPoint(), 
                    columnAtPoint(e.getPoint()));
            
            // If click was not made on the divider, disregard it
            if (column == null)
            {
                logger_.debug("return -- column is null");
                return;
            }
            
            //int oldMinWidth = column.getMinWidth();
            
            int newWidth = getRequiredColumnWidth(column);
            
            //column.setMinWidth(minWidth);
            //column.setPreferredWidth(minWidth);
            column.setWidth(newWidth);
            
            if (newWidth < column.getMinWidth())
                column.setMinWidth(newWidth);
            
            setResizingColumn(column);
            getTable().doLayout();
            
            //column.setMinWidth(oldMinWidth);
        }
    }
}