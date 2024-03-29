package toolbox.util.ui.table;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.concurrent.BatchingQueueReader;
import toolbox.util.concurrent.IBatchingQueueListener;
import toolbox.util.service.Destroyable;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;

/**
 * SmartTableModel extends DefaultTableModel by adding the following features.
 * <p>
 * <ul>
 * <li>Adds rows on the event dispatch thread
 * <li>Save table contents to a file
 * <li>Uses batching to efficiently add large numbers of rows
 * </ul>
 */
public class SmartTableModel extends DefaultTableModel implements
    IBatchingQueueListener, Destroyable {

    private static final Logger logger_ = 
        Logger.getLogger(SmartTableModel.class);

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * Holding pen for rows that need to be added to the table.
     */
    private BlockingQueue queue_;

    /**
     * Reads table rows from the holding pen in batch mode.
     */
    private BatchingQueueReader queueReader_;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a table model.
     */
    public SmartTableModel() {
        this((Vector) null, 0);
    }


    /**
     * Creates a SmartTableModel.
     * 
     * @param rowCount Number of columns.
     * @param columnCount Number of rows.
     */
    public SmartTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
        init();
    }


    /**
     * Creates a SmartTableModel.
     * 
     * @param columnNames Vector of data.
     * @param rowCount Number of rows.
     */
    public SmartTableModel(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);
        init();
    }


    /**
     * Creates a SmartTableModel.
     * 
     * @param aobj Array of objects.
     * @param i Number of columns.
     */
    public SmartTableModel(Object aobj[], int i) {
        this(DefaultTableModel.convertToVector(aobj), i);
    }

    // -------------------------------------------------------------------------
    // Overrides javax.swing.table.DefaultTableModel
    // -------------------------------------------------------------------------

    /**
     * Adds a vector of data as a row to the table.
     * 
     * @param vector Adds vector of data to the table as a new row.
     * @see javax.swing.table.DefaultTableModel#addRow(Vector)
     */
    public void addRow(Vector vector) {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                // If not event dispatch thread, push to queue
                queue_.put(vector);
            }
            catch (InterruptedException e) {
                logger_.error(e);
            }
        }
        else {
            // Thread safe..just add directly
            super.addRow(vector);
        }
    }

    // -------------------------------------------------------------------------
    // Overrides javax.swing.table.AbstractTableModel
    // -------------------------------------------------------------------------

    /**
     * Returns class associated with a given column. Needed for sorting
     * capability.
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex) {
        return (getRowCount() > 0)
            ? getValueAt(0, columnIndex).getClass()
            : super.getColumnClass(columnIndex);
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------

    /**
     * Adds an array of rows to the table.
     * 
     * @param rows Rows to add to the table.
     */
    public void addRows(Object[] rows) {
        if (!SwingUtilities.isEventDispatchThread()) {
            // If not event dispatch thread, push rows to queue
            for (int i = 0; i < rows.length; i++)
                addRow((Vector) rows[i]);
        }
        else {
            // Thread safe..just add rows directly
            for (int i = 0; i < rows.length; i++)
                super.addRow((Vector) rows[i]);
        }
    }


    /**
     * Saves the contents of the table model to a file.
     * 
     * @param s Filename to save to.
     * @throws IOException on I/O error.
     */
    public void saveToFile(String s) throws IOException {
        FileWriter filewriter = new FileWriter(s);

        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++)
                filewriter.write(getValueAt(i, j) + " ");
            filewriter.write("\n");
        }

        filewriter.close();
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------

    /**
     * Inits table model.
     */
    protected void init() {
        queue_ = new LinkedBlockingQueue();
        queueReader_ = new BatchingQueueReader(queue_);
        queueReader_.addBatchingQueueListener(this);
        queueReader_.start();
    }

    // -------------------------------------------------------------------------
    // IBatchingQueueListener Interface
    // -------------------------------------------------------------------------

    /**
     * Next batch of rows is available.
     * 
     * @param elements Array of rows to add to the table.
     */
    public void nextBatch(Object[] elements) {
        // Elements just popped off the queue. Add on event dispatch thread
        SwingUtilities.invokeLater(new AddRows(elements));
    }

    // -------------------------------------------------------------------------
    // Destroyable Interface
    // -------------------------------------------------------------------------

    /*
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws IllegalStateException, ServiceException {
        logger_.debug(StringUtil.banner("SmartTableModel.destroy"));
        queueReader_.stop();
    }

    /*
     * @see toolbox.util.service.Destroyable#isDestroyed()
     */
    public boolean isDestroyed() {
        return queueReader_.getState() == ServiceState.STOPPED;
    }
    
    // -------------------------------------------------------------------------
    // Service Interface
    // -------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState() {
        return null;
    }

    // -------------------------------------------------------------------------
    // AddRows
    // -------------------------------------------------------------------------

    /**
     * Runnable that adds a row to the table model.
     */
    class AddRows implements Runnable {

        /**
         * Row data.
         */
        private Object[] rows_;


        /**
         * Creates a Runnable to add a row to the table model.
         * 
         * @param rows Data to add to the table.
         */
        public AddRows(Object[] rows) {
            rows_ = rows;
        }


        /**
         * Adds a row to the table model.
         */
        public void run() {
            addRows(rows_);
        }
    }
}