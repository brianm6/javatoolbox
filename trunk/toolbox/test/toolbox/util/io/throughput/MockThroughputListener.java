package toolbox.util.io.throughput;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.log4j.Logger;

import toolbox.util.io.throughput.ThroughputEvent;
import toolbox.util.io.throughput.ThroughputListener;

/** 
 * Listener used to make sure event notification is working correctly.
 */    
public class MockThroughputListener implements ThroughputListener
{
    private static final Logger logger_ = 
        Logger.getLogger(MockThroughputListener.class);
    
    private NumberFormat df_ = DecimalFormat.getInstance();
    
    //----------------------------------------------------------------------
    // ThroughputListener Interface
    //----------------------------------------------------------------------

    /**
     * @see toolbox.util.io.throughput.ThroughputListener#currentThroughput(
     *      toolbox.util.io.throughput.ThroughputEvent)
     */
    public void currentThroughput(ThroughputEvent event)
    {
        String thruput = df_.format(event.getThroughput());
        logger_.info("Transferred " + thruput + " bytes/second");    
    }
}