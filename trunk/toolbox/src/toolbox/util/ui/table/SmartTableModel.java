package toolbox.util.ui.table;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import toolbox.util.concurrent.BatchingQueueReader;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.concurrent.IBatchingQueueListener;

/**
 * A thread safe table model that adds elements to the model on the 
 * EventDispatch thread.
 */
public class SmartTableModel extends DefaultTableModel 
    implements IBatchingQueueListener
{
    private static final Logger logger_ = 
        Logger.getLogger(SmartTableModel.class);
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Holding pen for rows that need to be added to the table.
     */    
    private BlockingQueue queue_;
    
    /**
     * Reads table rows from the holding pen in batch mode.
     */
    private BatchingQueueReader queueReader_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a table model. 
     */
    public SmartTableModel()
    {
        this((Vector) null, 0);
    }


    /**
     * Creates a SmartTableModel.
     * 
     * @param i Number of columns
     * @param j Number of rows
     */
    public SmartTableModel(int i, int j)
    {
        super(i, j);
        init();
    }


    /**
     * Creates a SmartTableModel.
     * 
     * @param vector Vector of data
     * @param i Number of columns
     */
    public SmartTableModel(Vector vector, int i)
    {
        super(vector, i);
        init();
    }


    /**
     * Creates a SmartTableModel.
     * 
     * @param aobj Array of objects
     * @param i Number of columns
     */
    public SmartTableModel(Object aobj[], int i)
    {
        this(DefaultTableModel.convertToVector(aobj), i);
    }

    //--------------------------------------------------------------------------
    // Overrides javax.swing.table.DefaultTableModel
    //--------------------------------------------------------------------------
        
    /**
     * Adds a vector of data as a row to the table.
     * 
     * @param vector Adds vector of data to the table as a new row.
     * @see javax.swing.table.DefaultTableModel#addRow(Vector)
     */
    public void addRow(Vector vector)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            // If not event dispatch thread, push to queue         
            queue_.push(vector);
        }
        else
        {
            // Thread safe..just add directly
            super.addRow(vector);
        }
    }

    //--------------------------------------------------------------------------
    // Overrides javax.swing.table.AbstractTableModel
    //--------------------------------------------------------------------------

    /**
     * Returns class associated with a given column. Needed for sorting
     * capability.
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex)
    {
        return (getRowCount() > 0) 
            ? getValueAt(0, columnIndex).getClass() 
            : super.getColumnClass(columnIndex);    
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds an array of rows to the table.
     * 
     * @param rows Rows to add to the table.
     */
    public void addRows(Object[] rows)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            // If not event dispatch thread, push rows to queue
            for (int i = 0; i < rows.length; i++)
                addRow((Vector) rows[i]);
        }
        else
        {
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
    public void saveToFile(String s) throws IOException
    {
        FileWriter filewriter = new FileWriter(s);
        
        for (int i = 0; i < getRowCount(); i++)
        {
            for (int j = 0; j < getColumnCount(); j++)
                filewriter.write(getValueAt(i, j) + " ");
            filewriter.write("\n");
        }

        filewriter.close();
    }

    //--------------------------------------------------------------------------
    //  Protected
    //--------------------------------------------------------------------------
    
    /**
     * Inits table model.
     */
    protected void init()
    {
        queue_ = new BlockingQueue();
        queueReader_ = new BatchingQueueReader(queue_);
        queueReader_.addBatchingQueueListener(this);
        queueReader_.start();
    }

    //--------------------------------------------------------------------------
    //  IBatchingQueueListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * Next batch of rows is available.
     * 
     * @param elements Array of rows to add to the table.
     */
    public void nextBatch(Object[] elements)
    {
        // Elements just popped off the queue. Add on event dispatch thread
        SwingUtilities.invokeLater(new AddRows(elements));
    }

    //--------------------------------------------------------------------------
    //  AddRows
    //--------------------------------------------------------------------------
    
    /**
     * Runnable that adds a row to the table model.
     */
    class AddRows implements Runnable
    {
        /** 
         * Row data.
         */
        private Object[] rows_;


        /**
         * Creates a Runnable to add a row to the table model.
         * 
         * @param rows Data to add to the table.
         */
        public AddRows(Object[] rows)
        {
            rows_ = rows;
        }

                
        /**
         * Adds a row to the table model. 
         */
        public void run()
        {
            addRows(rows_);
        }
    }
}