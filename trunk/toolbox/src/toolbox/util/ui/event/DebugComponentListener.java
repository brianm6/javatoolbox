package toolbox.util.ui.event;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.apache.log4j.Logger;

/**
 * DebugComponentListener 
 */
public class DebugComponentListener implements ComponentListener
{
    private static final Logger logger_ =
        Logger.getLogger(DebugComponentListener.class);
        
    /**
     * @see java.awt.event.ComponentListener#componentHidden(
     *      java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent e)
    {
        logger_.debug("Hidden: " + e.getComponent().getName());    
    }

    /**
     * @see java.awt.event.ComponentListener#componentMoved(
     *      java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent e)
    {
        logger_.debug("Moved: " + e.getComponent().getName() + " : " + 
            e.paramString());
    }

    /**
     * @see java.awt.event.ComponentListener#componentResized(
     *      java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e)
    {
        logger_.debug("Resized: " + e.getComponent().getName() + " : " + 
            e.paramString());
    }

    /**
     * @see java.awt.event.ComponentListener#componentShown(
     *      java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent e)
    {
        logger_.debug("Shown: " + e.getComponent().getName());
    }
}
