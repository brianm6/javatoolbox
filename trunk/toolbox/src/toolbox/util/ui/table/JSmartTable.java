package toolbox.util.ui.table;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.AntiAliased;
import toolbox.workspace.IPreferenced;

/**
 * JSmartTable adds the following behavior.
 * <p>
 * <ul>
 *   <li>Antialised text.
 *   <li>Automatic tailing as the number of rows increases.
 * </ul>
 * 
 * @see JSmartTableHeader
 * @see JSmartTableModel
 * @see SmartTableCellRender
 */
public class JSmartTable extends JTable implements AntiAliased, IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(JSmartTable.class);

    //--------------------------------------------------------------------------
    // Constants 
    //--------------------------------------------------------------------------
    
    /**
     * Prefences node for all JSmartTable settings.
     */
    private static final String NODE_JSMARTTABLE = "JSmartTable";

    /** 
     * Preference that stores automatic tailing of the table flag as a boolean.
     */
    private static final String ATTR_AUTOTAIL = "autotail";

    /**
     * Preference that stores antialiasing of the text flag as a boolean.
     */
    private static final String ATTR_ANTIALIAS = "antialias";
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    /**
     * Listens for switching out of the table model so that the tracker that
     * is responsible for "following" the table is transferred over seamlessly.
     */
    private PropertyChangeListener tableModelTracker_;
    
    /**
     * Listens for changes to the tables model (specifically insert and delete
     * row operations) so that the table can be scrolled to the very end
     * (assumning that the follow flag is turned on).
     */
    private TableModelListener followTracker_;
    
    /**
     * Toggles automatic scrolling of table to its very last row as rows are 
     * added in real time.
     */
    private boolean autoTail_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartTable.
     */
    public JSmartTable()
    {
        smartInit();        
    }


    /**
     * Creates a JSmartTable.
     * 
     * @param numRows Number of rows.
     * @param numColumns Number of columns.
     */
    public JSmartTable(int numRows, int numColumns)
    {
        super(numRows, numColumns);
        smartInit();
    }


    /**
     * Creates a JSmartTable.
     * 
     * @param dm Table model.
     */
    public JSmartTable(TableModel dm)
    {
        super(dm);
        smartInit();
    }


    /**
     * Creates a JSmartTable.
     * 
     * @param rowData Row data.
     * @param columnNames Column names.
     */
    public JSmartTable(Object[][] rowData, Object[] columnNames)
    {
        super(rowData, columnNames);
        smartInit();
    }


    /**
     * Creates a JSmartTable.
     * 
     * @param rowData Row data.
     * @param columnNames Column names.
     */
    public JSmartTable(Vector rowData, Vector columnNames)
    {
        super(rowData, columnNames);
        smartInit();
    }


    /**
     * Creates a JSmartTable.
     * 
     * @param dm Table model.
     * @param cm Column model.
     */
    public JSmartTable(TableModel dm, TableColumnModel cm)
    {
        super(dm, cm);
        smartInit();
    }


    /**
     * Creates a JSmartTable.
     * 
     * @param dm Table model.
     * @param cm Column model.
     * @param sm Selection model.
     */
    public JSmartTable(
        TableModel dm,
        TableColumnModel cm,
        ListSelectionModel sm)
    {
        super(dm, cm, sm);
        smartInit();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Initializes the table.
     */
    protected void smartInit()
    {
        setTableHeader(new JSmartTableHeader(getColumnModel()));
        followTracker_ = new FollowTracker();

        addPropertyChangeListener(
            "model", 
            tableModelTracker_ = new TableModelTracker());
        
        getModel().addTableModelListener(followTracker_);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Returns true if automatic tailing is active, false otherwise.
     * 
     * @return boolean
     */
    public boolean isAutoTail()
    {
        return autoTail_;
    }
   
    
    /**
     * Activates autoatic tailing of the table as the number of rows increases.
     * 
     * @param autoTail True to activate tailing, false otherwise.
     */
    public void setAutoTail(boolean autoTail)
    {
        if (autoTail == isAutoTail())
            return;
        
        autoTail_ = autoTail;
        firePropertyChange("autotail", !autoTail, autoTail);
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs)
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JSMARTTABLE, new Element(NODE_JSMARTTABLE));
        
        setAutoTail(XOMUtil.getBooleanAttribute(root, ATTR_AUTOTAIL, true));
        setAntiAliased(XOMUtil.getBooleanAttribute(root, ATTR_ANTIALIAS, true));
    }

    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_JSMARTTABLE);
        root.addAttribute(new Attribute(ATTR_AUTOTAIL, isAutoTail() + ""));
        root.addAttribute(new Attribute(ATTR_ANTIALIAS, isAntiAliased() + ""));
        prefs.appendChild(root);
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
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAliased_ = b;

        // TODO: Is this necessary?
        JTableHeader header = getTableHeader();
        
        if ((header != null) && (header instanceof AntiAliased))
            ((AntiAliased) header).setAntiAliased(b); 
            
        for (int i = 0, c = getColumnCount(); i < c; i++)
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
    
    //--------------------------------------------------------------------------
    // TableModelTracker
    //--------------------------------------------------------------------------

    /**
     * Keeps track of when a model is associated/disassociated with the current
     * table. Whenever the  model is switched out, the followTracker is removed
     * from the old table and associated with the replacement.
     */
    class TableModelTracker implements PropertyChangeListener
    {
        /**
         * @see java.beans.PropertyChangeListener#propertyChange(
         *      java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent evt)
        {
            TableModel old = (TableModel) evt.getOldValue();
            TableModel knew = (TableModel) evt.getNewValue();
            
            if (old != null)
                old.removeTableModelListener(followTracker_);
            
            if (knew != null)
                knew.addTableModelListener(followTracker_);
        }
    }

    //--------------------------------------------------------------------------
    // FollowTracker
    //--------------------------------------------------------------------------
    
    /**
     * Listens for insertions/deletions from the table model. Whenever this 
     * happens, if the follow flag is active, the table will automatically be
     * scrolled to the end. 
     */
    class FollowTracker implements TableModelListener
    {
        public void tableChanged(TableModelEvent e)
        {
            // Only update scrolly bar if a row was inserted or deleted.
            
            if (isAutoTail() &&
                (e.getType() == TableModelEvent.INSERT ||
                 e.getType() == TableModelEvent.DELETE))
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Rectangle rect = getCellRect(
                            getRowCount(), getColumnCount(), true);
                        
                        scrollRectToVisible(rect);
                    }
                });
            }
        }
    }    
}