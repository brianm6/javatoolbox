package toolbox.plugin.jtail;

import java.io.IOException;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;

/**
 * Specialization of a JSmartTabbedPane that handles the tabname for aggregated
 * tails and appends an asterisk to tab with the most recent tail activity.
 */
public class JTailTabbedPane extends JSmartTabbedPane
    implements TailViewListener
{
    private static final Logger logger_ =
        Logger.getLogger(JTailTabbedPane.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JTailTabbedPane.
     */
    public JTailTabbedPane()
    {
        init();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Inits the tabbed pane.
     */
    protected void init()
    {
    }

    //--------------------------------------------------------------------------
    // TailViewListener Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.plugin.jtail.TailViewListener#newDataAvailable(toolbox.plugin.jtail.TailPane)
     */
    public void newDataAvailable(TailPane tailPane)
    {
        // TODO: Do something useful here -- visual cue.
        
        //int index = indexOfComponent(tailPane);
        //setTitleAt(index, "* " + getTitleAt(index));
    }


    /*
     * @see toolbox.plugin.jtail.TailViewListener#tailAggregated(toolbox.plugin.jtail.TailPane)
     */
    public void tailAggregated(TailPane tailPane)
    {
        int index = indexOfComponent(tailPane);

        try
        {
            setTitleAt(index, JTail.makeTabLabel(tailPane.getConfiguration()));

            setToolTipTextAt(
                index,
                JTail.makeTabToolTip(tailPane.getConfiguration()));
        }
        catch (IOException e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
    }
}