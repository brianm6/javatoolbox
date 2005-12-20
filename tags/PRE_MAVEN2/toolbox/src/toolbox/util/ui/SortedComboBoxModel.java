package toolbox.util.ui;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

/**
 * SortedComboBoxModel sorts combobox items on insertion. Items in the model
 * must implement the Comparable interface.
 * 
 * @see java.lang.Comparable
 */
public class SortedComboBoxModel extends DefaultComboBoxModel
{
    //--------------------------------------------------------------------------
    // Constructors (Pass through)
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SortedComboBoxModel.
     */
    public SortedComboBoxModel()
    {
    }

    
    /**
     * Creates a SortedComboBoxModel.
     * 
     * @param items ComboBox elements.
     */
    public SortedComboBoxModel(Object[] items)
    {
        super(items);
    }

    
    /**
     * Creates a SortedComboBoxModel.
     * 
     * @param items ComboBox elements.
     */
    public SortedComboBoxModel(Vector items)
    {
        super(items);
    }

    //--------------------------------------------------------------------------
    // Overrides DefaultComboBoxModel
    //--------------------------------------------------------------------------
    
    /**
     * Maintain order on insert.
     * 
     * @see javax.swing.MutableComboBoxModel#addElement(java.lang.Object)
     */
    public void addElement(Object element)
    {
        int index = 0;
        int size = getSize();

        //  Determine where to insert element to keep list in sorted order

        for (index = 0; index < size; index++)
        {
            Comparable c = (Comparable) getElementAt(index);

            if (c.compareTo(element) > 0)
                break;
        }

        super.insertElementAt(element, index);
    }


    /**
     * @see javax.swing.MutableComboBoxModel#insertElementAt(
     *      java.lang.Object, int)
     */
    public void insertElementAt(Object element, int index)
    {
        addElement(element);
    }
}