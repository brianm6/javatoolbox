package toolbox.util.ui.event;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import org.apache.log4j.Logger;

/**
 * DebugContainerListener is useful for tracing container events on a given
 * contaner.
 */
public class DebugContainerListener implements ContainerListener
{
    private static final Logger logger_ =
        Logger.getLogger(DebugContainerListener.class);
    
    //--------------------------------------------------------------------------
    // ContainerListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ContainerListener#componentAdded(
     *      java.awt.event.ContainerEvent)
     */
    public void componentAdded(ContainerEvent e)
    {
        logger_.debug("Added: " + e.paramString());
    }

    /**
     * @see java.awt.event.ContainerListener#componentRemoved(
     *      java.awt.event.ContainerEvent)
     */
    public void componentRemoved(ContainerEvent e)
    {
        logger_.debug("Removed: " + e.paramString());
    }

}