package toolbox.jtail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTabbedPane;

import org.apache.log4j.Category;

/**
 * enclosing_type
 */
public class JTailTabbedPane extends JTabbedPane
{
    private static final Category logger_ =
        Category.getInstance(JTailTabbedPane.class);
    
    /**
     * Constructor for JTailTabbedPane.
     */
    public JTailTabbedPane()
    {
        super();
        init();
    }

    protected void init()
    {
        addPropertyChangeListener( new PropertyChangeListener()
        {
            /**
             * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
             */
            public void propertyChange(PropertyChangeEvent evt)
            {
                logger_.debug(evt);
            }
        });
    }
   
    
    public class TailPaneListener implements TailPane.ITailPaneListener
    {
        /**
         * @see toolbox.jtail.TailPane.ITailPaneListener#newDataAvailable(TailPane)
         */
        public void newDataAvailable(TailPane tailPane)
        {
            int index = indexOfComponent(tailPane);
            setTitleAt(index, "* "+ getTitleAt(index));
        }
    }
}