package toolbox.util.ui.table.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.table.TableSorter;

/**
 * Unit test for TableSorter.
 */
public class TableSorterTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(TableSorterTest.class);

    private boolean DEBUG = true;
            
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(TableSorterTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests the features of the TableSorter.
     */
    public void testTableSorter()
    {
        logger_.info("Running testTableSorter...");
    
        TestTableModel myModel = new TestTableModel();
        TableSorter sorter = new TableSorter(myModel);
        JTable table = new JTable(sorter);
        sorter.addMouseListenerToHeaderInTable(table);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        
        launchInDialog(p);
    }

    //--------------------------------------------------------------------------
    // TestTableModel
    //--------------------------------------------------------------------------
    
    /**
     * TestTableModel.
     */
    class TestTableModel extends AbstractTableModel
    {
        final String[] columnNames = 
        { 
            "First Name", "Last Name", "Sport", "# of Years", "Vegetarian" 
        };
            
        final Object[][] data =
        {
            {
                "Mary",
                "Campione",
                "Snowboarding",
                new Integer(5),
                new Boolean(false)
            },
            {
                "Alison",
                "Huml",
                "Rowing",
                new Integer(3),
                new Boolean(true)
            },
            {
                "Kathy",
                "Walrath",
                "Chasing toddlers",
                new Integer(2),
                new Boolean(false)
            },
            {
                "Sharon",
                "Zakhour",
                "Speed reading",
                new Integer(20),
                new Boolean(true)
            },
            {
                "Angela",
                "Lih",
                "Teaching high school",
                new Integer(4),
                new Boolean(false)
            }
        };

        
        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount()
        {
            return columnNames.length;
        }

        
        /**
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount()
        {
            return data.length;
        }

        
        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int col)
        {
            return columnNames[col];
        }

        
        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col)
        {
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }

        
        /**
		 * Don't need to implement this method unless your table's editable.
		 */
        public boolean isCellEditable(int row, int col)
        {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            
            if (col < 2)
                return false;
            else
                return true;
        }

        
        /**
		 * Don't need to implement this method unless your table's data can
		 * change.
		 */
        public void setValueAt(Object value, int row, int col)
        {
            if (DEBUG)
            {
                System.out.println("Setting value at " + row + "," + col + 
                    " to " + value + " (an instance of " + 
                        value.getClass() + ")");
            }

            if (data[0][col] instanceof Integer && 
                !(value instanceof Integer))
            {
                //With JFC/Swing 1.1 and JDK 1.2, we need to create
                //an Integer from the value; otherwise, the column
                //switches to contain Strings.  Starting with v 1.3, 
                //the table automatically converts value to an Integer,
                //so you only need the code in the 'else' part of this
                //'if' block.
                
                try
                {
                    data[row][col] = new Integer(value.toString());
                    //fireTableCellUpdated(row, col);
                    fireTableDataChanged();
                }
                catch (NumberFormatException e)
                {
                    JOptionPane.showMessageDialog(new JLabel(),
                        "The \"" + getColumnName(col) + 
                            "\" column accepts only integer values.");
                }
            }
            else
            {
                data[row][col] = value;
                //fireTableCellUpdated(row, col);
                fireTableDataChanged();
            }

            if (DEBUG)
            {
                System.out.println("New value of data:");
                printDebugData();
            }
        }

        
        /**
         * Debug 
         */
        private void printDebugData()
        {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i = 0; i < numRows; i++)
            {
                System.out.print("    row " + i + ":");
                
                for (int j = 0; j < numCols; j++)
                    System.out.print("  " + data[i][j]);
                    
                System.out.println();
            }
            
            System.out.println("--------------------------");
        }
    }
}