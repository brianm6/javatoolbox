package toolbox.util.ui.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;

/**
 * Convenience PropertyChangeListener that dumps the contents of the 
 * PropertyChangeEvent to debug output. Useful for spying on a component to
 * find out what properties it changes.
 */
public class DebugPropertyChangeListener implements PropertyChangeListener
{
    private static final Logger logger_ =
        Logger.getLogger(DebugPropertyChangeListener.class);

    //--------------------------------------------------------------------------
    // PropertyChangeListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.beans.PropertyChangeListener#propertyChange(
     *      java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        logger_.debug(
            evt.getPropertyName()
                + " : "
                + evt.getOldValue()
                + " --> "
                + evt.getNewValue());
    }
}