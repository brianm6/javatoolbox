package toolbox.jtail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

/**
 * enclosing_type
 */
public class JTailTabbedPane extends JTabbedPane
{
    private static final Logger logger_ =
        Logger.getLogger(JTailTabbedPane.class);
    
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
            public void propertyChange(PropertyChangeEvent evt)
            {
                logger_.debug(evt);
            }
        });
    }
   
    /**
     * Tail pane listener
     */
    public class TailPaneListener implements TailPane.ITailPaneListener
    {
        public void newDataAvailable(TailPane tailPane)
        {
            int index = indexOfComponent(tailPane);
            setTitleAt(index, "* "+ getTitleAt(index));
        }
    }
}
