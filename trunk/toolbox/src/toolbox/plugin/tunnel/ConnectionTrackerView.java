/**
 * 
 */
package toolbox.plugin.tunnel;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.io.MonitoredOutputStream.OutputStreamListener;
import toolbox.util.ui.JSmartLabel;

public class ConnectionTrackerView extends JPanel {
    
    private int bytesTransferred = 0;
    private MonitoredOutputStream incomingSink;
    private MonitoredOutputStream outgoingSink;
    private OutputStreamListener incomingListener;
    private OutputStreamListener outgoingListener;
    
    private JLabel incomingLabel;
    private JLabel outgoingLabel;
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
        setLayout(new GridLayout(3,2));
        
        add(new JLabel("Incoming bytes:"));
        add(incomingLabel = new JLabel("0"));
        add(new JLabel("Outgoing bytes:"));
        add(outgoingLabel = new JLabel("0"));
        add(new JLabel("Total butes:"));
        add(transferredLabel = new JSmartLabel("0"));
        
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }
    
    
    class UniDirectionalListener implements OutputStreamListener {
    
        public void streamClosed(MonitoredOutputStream stream) {
            long in = incomingSink.getCount();
            long out = outgoingSink.getCount();
            
            incomingLabel.setText("" + in);
            outgoingLabel.setText("" + out);
            transferredLabel.setText(
                "Closed " + (in + out));
        }
        
        public void streamFlushed(MonitoredOutputStream stream) {
            long in = incomingSink.getCount();
            long out = outgoingSink.getCount();
            
            incomingLabel.setText("" + in);
            outgoingLabel.setText("" + out);
            transferredLabel.setText("" + (in + out));
        }
    }
}