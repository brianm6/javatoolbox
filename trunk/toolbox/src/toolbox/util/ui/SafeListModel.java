package toolbox.util.ui;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

/**
 * enclosing_type 
 */
public class SafeListModel extends DefaultListModel
{

    /**
     * Constructor for SafeListModel.
     */
    public SafeListModel()
    {
        super();
    }
    
    class AddElement implements Runnable
    {
        Object element_;

        public AddElement(Object element)
        {
            element_ = element;
        }
        
        public void run()
        {
            addElement(element_);
        }
    }

    public void addElement(Object obj)
    {
        if(!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(new AddElement(obj));
            return;
        }
        else
        {
            super.addElement(obj);
            return;
        }
    }
}
