package toolbox.util.ui;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import toolbox.util.ExceptionUtil;

/**
 * A thread safe table model that adds elements to the model on the 
 * EventDispatch thread. Updates that are made on an arbitrary thread can 
 * cause erratic repaint behavior and out of sync behavior between the 
 * model and view.
 */
public class ThreadSafeTableModel extends DefaultTableModel
{
    
    /**
     * Creates a table model 
     */
    public ThreadSafeTableModel()
    {
        this((Vector)null, 0);
    }


    /**
     * Creates a ThreadSafeTableModel
     */
    public ThreadSafeTableModel(int i, int j)
    {
        super(i, j);
    }


    /**
     * Creates a ThreadSafeTableModel
     */
    public ThreadSafeTableModel(Vector vector, int i)
    {
        super(vector, i);
    }


    /**
     * Creates a ThreadSafeTableModel
     */
    public ThreadSafeTableModel(Object aobj[], int i)
    {
        this(DefaultTableModel.convertToVector(aobj), i);
    }


    /**
     * Creates a ThreadSafeTableModel
     */
    public ThreadSafeTableModel(Vector vector, Vector vector1)
    {
        setDataVector(vector, vector1);
    }


    /**
     * Creates a ThreadSafeTableModel
     */
    public ThreadSafeTableModel(Object aobj[][], Object aobj1[])
    {
        setDataVector(aobj, aobj1);
    }


    
    /**
     * Runnable that adds a row to the table model
     */
    class AddRow implements Runnable
    {
        /** Row data **/
        Vector rowData_;

        /**
         * Creates a Runnable to add a row to the table model
         * 
         * @param  rowData  Data to add to the table
         */
        public AddRow(Vector rowData)
        {
            rowData_ = rowData;
        }
                
        /**
         * Adds a row to the table model 
         */
        public void run()
        {
            addRow(rowData_);
        }
    }

    /**
     * Adds a vector of data as a row to the table
     */
    public void addRow(Vector vector)
    {
        if(!SwingUtilities.isEventDispatchThread())
            SwingUtilities.invokeLater(new AddRow(vector));
        else
            super.addRow(vector);
    }

    /**
     * Saves the contents of the table model to a file
     * 
     * @param  s  Filename
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
}