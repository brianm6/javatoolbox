package toolbox.plugin.tunnel;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.io.MonitoredOutputStream.OutputStreamListener;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartLabel;

/**
 * View that shows the number of incoming, outgoing, and total transferred
 * bytes over two (one in each direction) 
 * {@link toolbox.util.io.MonitoredOutputStream}s.
 */
public class ConnectionTrackerView extends JPanel {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private int bytesTransferred_ = 0;
    private MonitoredOutputStream incomingSink_;
    private MonitoredOutputStream outgoingSink_;
    private OutputStreamListener incomingListener_;
    private OutputStreamListener outgoingListener_;
    
    private JLabel incomingLabel_;
    private JLabel outgoingLabel_;
    private JLabel transferredLabel_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public ConnectionTrackerView(
        MonitoredOutputStream incomingSink,
        MonitoredOutputStream outgoingSink) {
        
        buildView();
        
        incomingSink_ = incomingSink;
        outgoingSink_ = outgoingSink;
        
        incomingListener_ = new UniDirectionalListener();
        incomingSink.addOutputStreamListener(incomingListener_);
        
        outgoingListener_ = new UniDirectionalListener();
        outgoingSink.addOutputStreamListener(outgoingListener_);
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------
    
    protected void buildView() {
        setLayout(new GridLayout(3,2));
        
        add(new JLabel("Incoming bytes:"));
        add(incomingLabel_ = new JLabel("0"));
        add(new JLabel("Outgoing bytes:"));
        add(outgoingLabel_ = new JLabel("0"));
        add(new JLabel("Total butes:"));
        add(transferredLabel_ = new JSmartLabel("0"));
        
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }
    
    // -------------------------------------------------------------------------
    // UniDirectionalListener 
    // -------------------------------------------------------------------------
    
    class UniDirectionalListener implements OutputStreamListener {
    
        public void streamClosed(MonitoredOutputStream stream) {
            long in = incomingSink_.getCount();
            long out = outgoingSink_.getCount();
            
            incomingLabel_.setText("" + in);
            outgoingLabel_.setText("" + out);
            transferredLabel_.setText((in + out) + "");
            transferredLabel_.setIcon(ImageCache.getIcon(ImageCache.IMAGE_DELETE));
            
            // Cleanup
            incomingSink_.removeOutputStreamListener(incomingListener_);
            outgoingSink_.removeOutputStreamListener(outgoingListener_);
        }
        
        public void streamFlushed(MonitoredOutputStream stream) {
            long in = incomingSink_.getCount();
            long out = outgoingSink_.getCount();
            
            incomingLabel_.setText("" + in);
            outgoingLabel_.setText("" + out);
            transferredLabel_.setText("" + (in + out));
        }
    }
}