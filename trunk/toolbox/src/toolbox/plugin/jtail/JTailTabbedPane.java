package toolbox.jtail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;

/**
 * JTailTabbedPane
 */
public class JTailTabbedPane extends JTabbedPane implements TailPane.ITailPaneListener
{
    private static final Logger logger_ =
        Logger.getLogger(JTailTabbedPane.class);
    
    /**
     * Creates a JTailTabbedPane
     */
    public JTailTabbedPane()
    {
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
//    public class TailPaneListener implements TailPane.ITailPaneListener
//    {
        /**
         * @see toolbox.jtail.TailPane.ITailPaneListener#newDataAvailable(
         *      toolbox.jtail.TailPane)
         */
        public void newDataAvailable(TailPane tailPane)
        {
            int index = indexOfComponent(tailPane);
            setTitleAt(index, "* "+ getTitleAt(index));
        }
        
        /**
         * @see toolbox.jtail.TailPane.ITailPaneListener#tailAggregated(
         *      toolbox.jtail.TailPane)
         */
        public void tailAggregated(TailPane tailPane)
        {
            int index = indexOfComponent(tailPane);
            
            try
            {
                setTitleAt(
                    index, JTail.makeTabLabel(tailPane.getConfiguration()));
 
                setToolTipTextAt(
                    index, JTail.makeTabToolTip(tailPane.getConfiguration()));
            }
            catch (IOException e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
//    }
}