package toolbox.util.ui;

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
 * EventDispatch thread. Updates that are made on an arbitrary thread can 
 * cause erratic repaint behavior and out of sync behavior between the 
 * model and view. To minimize thread creation (SwingUtilities.invokeLater())
 * rows are added to the table in a batch like manned (=>1 rows) per invocation
 * based on however many rows are availble to add. A blocking queue is used as
 * the bridge between the table row producer and the table row consumer.
 */
public class ThreadSafeTableModel extends DefaultTableModel 
    implements IBatchingQueueListener
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(ThreadSafeTableModel.class);
        
    private BlockingQueue       queue_;
    private BatchingQueueReader queueReader_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a table model 
     */
    public ThreadSafeTableModel()
    {
        this((Vector)null, 0);
    }


    /**
     * Creates a ThreadSafeTableModel
     * 
     * @param   i   Number of columns
     * @param   j   Number of rows
     */
    public ThreadSafeTableModel(int i, int j)
    {
        super(i, j);
        init();
    }


    /**
     * Creates a ThreadSafeTableModel
     * 
     * @param  vector  Vector of data
     * @param  i       Number of columns
     */
    public ThreadSafeTableModel(Vector vector, int i)
    {
        super(vector, i);
        init();
    }


    /**
     * Creates a ThreadSafeTableModel
     * 
     * @param  aobj  Array of objects
     * @param  i     Number of columns
     */
    public ThreadSafeTableModel(Object aobj[], int i)
    {
        this(DefaultTableModel.convertToVector(aobj), i);
    }


    /**
     * Creates a ThreadSafeTableModel
     * 
     * @param  vector  Vector of columns
     * @param  vector1 Vector of rows
     */
    public ThreadSafeTableModel(Vector vector, Vector vector1)
    {
        setDataVector(vector, vector1);
        init();
    }


    /**
     * Creates a ThreadSafeTableModel
     * 
     * @param  aobj  Array of column objects
     * @param  aobj1 Array of row objects
     */
    public ThreadSafeTableModel(Object aobj[][], Object aobj1[])
    {
        setDataVector(aobj, aobj1);
        init();
    }
    
    //--------------------------------------------------------------------------
    //  Implementation
    //--------------------------------------------------------------------------
    
    /**
     * Inits table model
     */
    protected void init()
    {
        queue_ = new BlockingQueue();
        queueReader_ = new BatchingQueueReader(queue_);
        queueReader_.addBatchQueueListener(this);
        Thread t = new Thread(queueReader_);
        t.start();
    }
    
    
    /**
     * Adds a vector of data as a row to the table
     * 
     * @param  vector  Adds vector of data to the table as a new row
     */
    public void addRow(Vector vector)
    {
        String method = "[addRow] ";
        
        if (!SwingUtilities.isEventDispatchThread())
        {
            // If not event dispatch thread, push to queue         
            try
            {
                queue_.push(vector);
            }
            catch (InterruptedException ioe)
            {
                // Ignore
            }
        }
        else
        {
            // Thread safe..just add directly
            super.addRow(vector);
        }
    }


    /**
     * Adds an array of rows to the table
     * 
     * @param  rows  Rows to add to the table
     */
    public void addRows(Object[] rows)
    {
        String method = "[addRws] ";
                
        if(!SwingUtilities.isEventDispatchThread())
        {
            // If not event dispatch thread, push rows to queue
            for (int i=0; i<rows.length; i++)
                addRow((Vector)rows[i]);
        }
        else
        {
            // Thread safe..just add rows directly
            for (int i=0; i<rows.length; i++)
                super.addRow((Vector)rows[i]);
        }        
    }


    /**
     * Saves the contents of the table model to a file
     * 
     * @param  s  Filename
     * @throws IOException on IO error
     */
    public void saveToFile(String s) throws IOException
    {
        FileWriter filewriter = new FileWriter(s);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            for(int j = 0; j < getColumnCount(); j++)
                filewriter.write(getValueAt(i, j) + " ");
            filewriter.write("\n");
        }

        filewriter.close();
    }

    //--------------------------------------------------------------------------
    //  Interfaces
    //--------------------------------------------------------------------------
    
    /**
     * Interface for IBatchQueueListner
     * 
     * @param  elements  Array of rows to add to the table
     */
    public void nextBatch(Object[] elements)
    {
        // Elements just popped off the queue. Add on event dispatch thread
        SwingUtilities.invokeLater(new AddRows(elements));
    }

    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Runnable that adds a row to the table model
     */
    class AddRows implements Runnable
    {
        /** Row data **/
        private Object[] rows_;

        /**
         * Creates a Runnable to add a row to the table model
         * 
         * @param  rowData  Data to add to the table
         */
        public AddRows(Object[] rows)
        {
            rows_ = rows;
        }
                
        /**
         * Adds a row to the table model 
         */
        public void run()
        {
            addRows(rows_);
        }
    }
}