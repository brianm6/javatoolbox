package toolbox.util.ui;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

/**
 * A thread safe list model that adds elements to the model on the EventDispatch
 * thread. Updates that are made on an arbitrary thread can cause erratic 
 * repaint behavior and out of sync behavior between the model and view.
 */
public class ThreadSafeListModel extends DefaultListModel
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for ThreadSafeListModel.
     */
    public ThreadSafeListModel()
    {
    }

    //--------------------------------------------------------------------------
    // Overridden from javax.swing.DefaultListModel
    //--------------------------------------------------------------------------
    
    /**
     * Adds an element to the list model
     * 
     * @param  obj  Object to add to the list model
     */
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

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Runnable which adds an element to the model on the event dispatch thread
     */    
    class AddElement implements Runnable
    {
        /** 
         * Element to add to the model 
         */   
        private Object element_;


        /**
         * Creates AddElement
         * 
         * @param  element  Element to add to the model
         */
        public AddElement(Object element)
        {
            element_ = element;
        }
        
        
        /**
         * Adds the element to the model
         */
        public void run()
        {
            addElement(element_);
        }
    }
}
