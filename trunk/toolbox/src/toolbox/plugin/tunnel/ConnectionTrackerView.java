/**
 * 
 */
package toolbox.plugin.tunnel;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.io.MonitoredOutputStream.OutputStreamListener;
import toolbox.util.ui.JSmartLabel;

public class ConnectionTrackerView extends JPanel {
    
    private int bytesTransferred = 0;
    private MonitoredOutputStream incomingSink;
    private MonitoredOutputStream outgoingSink;
    private OutputStreamListener incomingListener;
    private OutputStreamListener outgoingListener;
    
    private JLabel transferredLabel;
    
    
    public ConnectionTrackerView(
        MonitoredOutputStream incomingSink,
        MonitoredOutputStream outgoingSink) {
        
        buildView();
        
        this.incomingSink = incomingSink;
        this.outgoingSink = outgoingSink;
        
        incomingListener = new UniDirectionalListener();
        incomingSink.addOutputStreamListener(incomingListener);
        
        outgoingListener = new UniDirectionalListener();
        outgoingSink.addOutputStreamListener(outgoingListener);
    }

    protected void buildView() {
        setLayout(new FlowLayout());
        transferredLabel = new JSmartLabel("0");
        add(transferredLabel);
    }
    
    
    class UniDirectionalListener implements OutputStreamListener {
    
        public void streamClosed(MonitoredOutputStream stream) {
            transferredLabel.setText(
                "Closed " + (incomingSink.getCount() + outgoingSink.getCount()));
        }
        
        public void streamFlushed(MonitoredOutputStream stream) {
            transferredLabel.setText(
                "" + (incomingSink.getCount() + outgoingSink.getCount()));
        }
    }
}