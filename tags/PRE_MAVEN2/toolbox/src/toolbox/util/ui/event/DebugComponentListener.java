package toolbox.util.ui.event;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.apache.log4j.Logger;

/**
 * DebugComponentListener is useful for attaching to a component when trying to
 * get an insight on what events are being generated by it. 
 */
public class DebugComponentListener implements ComponentListener
{
    private static final Logger logger_ =
        Logger.getLogger(DebugComponentListener.class);

    //--------------------------------------------------------------------------
    // ComponentListener Interface
    //--------------------------------------------------------------------------
    
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