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

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.PreferencedUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.AntiAliased;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * JSmartTable adds the following features to the default JTable implementation. 
 * <p>
 * <ul>
 *   <li>Antialiased text
 *   <li>AutoTailing as data is added to the table
 *   <li>Persistence of preferences
 * </ul>
 *
 * @see toolbox.util.ui.table.JSmartTableHeader
 * @see toolbox.util.ui.table.SmartTableModel
 * @see toolbox.util.ui.table.SmartTableCellRenderer
 */
public class JSmartTable extends JTable implements AntiAliased, IPreferenced
{
    // TODO: Cut/copy/paste popup menu
    
    private static final Logger logger_ = Logger.getLogger(JSmartTable.class);

    //--------------------------------------------------------------------------
    // XML Preferences Constants
    //--------------------------------------------------------------------------

    /**
     * Prefences node for all JSmartTable settings.
     */
    private static final String NODE_JSMARTTABLE = "JSmartTable";

    /**
     * Javabean property for automatic tailing.
     */
    public static final String PROP_AUTOTAIL = "autoTail";
    
    /**
     * List of properties that are saved via the IPreferenced interface.
     */
    private static final String[] SAVED_PROPS = 
        { PROP_AUTOTAIL, PROP_ANTIALIAS};

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
    private TableModelListener autoTailTracker_;

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
        setTableHeader(new JSmartTableHeader(getColumnModel(), this));
        autoTailTracker_ = new AutoTailTracker();

        addPropertyChangeListener(
            "model",
            tableModelTracker_ = new TableModelTracker());

        getModel().addTableModelListener(autoTailTracker_);
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
     * Ensures the last row of the table is always selected ensuring the
     * scrollbar will always be at the very bottom as new rows are added.
     *
     * @param autoTail True to activate tailing, false otherwise.
     */
    public void setAutoTail(boolean autoTail)
    {
        if (autoTail == isAutoTail())
            return;

        autoTail_ = autoTail;
        firePropertyChange(PROP_AUTOTAIL, !autoTail, autoTail);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JSMARTTABLE, new Element(NODE_JSMARTTABLE));

        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_JSMARTTABLE);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);
        XOMUtil.insertOrReplace(prefs, root);
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
     * Updates rendering hints to support antialiasing.
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }

    //--------------------------------------------------------------------------
    // Overrides JTable
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.JTable#changeSelection(int, int, boolean, boolean)
     */
    //public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
    //    boolean extend)
    //{
    //    //Always toggle on single selection
    //    super.changeSelection(rowIndex, columnIndex, !extend, extend);
    //}
    
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
                old.removeTableModelListener(autoTailTracker_);

            if (knew != null)
                knew.addTableModelListener(autoTailTracker_);
        }
    }

    //--------------------------------------------------------------------------
    // AutoTailTracker
    //--------------------------------------------------------------------------

    /**
     * Listens for insertions/deletions from the table model. Whenever this
     * happens, if the follow flag is active, the table will automatically be
     * scrolled to the end.
     */
    class AutoTailTracker implements TableModelListener
    {
        /**
         * @see javax.swing.event.TableModelListener#tableChanged(
         *      javax.swing.event.TableModelEvent)
         */
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