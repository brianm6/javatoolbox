package toolbox.jsourceview;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Vector;

class ThreadSafeTableModel extends DefaultTableModel
{
    class AddRow
        implements Runnable
    {

        public void run()
        {
            addRow(v);
        }

        Vector v;

        public AddRow(Vector vector)
        {
            v = vector;
        }
    }


    public ThreadSafeTableModel()
    {
        this((Vector)null, 0);
    }

    public ThreadSafeTableModel(int i, int j)
    {
        super(i, j);
    }

    public ThreadSafeTableModel(Vector vector, int i)
    {
        super(vector, i);
    }

    public ThreadSafeTableModel(Object aobj[], int i)
    {
        this(DefaultTableModel.convertToVector(aobj), i);
    }

    public ThreadSafeTableModel(Vector vector, Vector vector1)
    {
        setDataVector(vector, vector1);
    }

    public ThreadSafeTableModel(Object aobj[][], Object aobj1[])
    {
        setDataVector(aobj, aobj1);
    }

    public void addRow(Vector vector)
    {
        if(!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(new AddRow(vector));
            return;
        }
        else
        {
            super.addRow(vector);
            return;
        }
    }

    public void saveToFile(String s)
    {
        Object obj = null;
        try
        {
            FileWriter filewriter = new FileWriter(s);
            for(int i = 0; i < getRowCount(); i++)
            {
                for(int j = 0; j < getColumnCount(); j++)
                    filewriter.write(getValueAt(i, j) + " ");
    
                filewriter.write("\n");
            }
    
            filewriter.close();
            return;
        }
        catch(Exception exception)
        {
            JOptionPane.showMessageDialog(null, exception.toString(), "Exception", 0);
        }
    }
}