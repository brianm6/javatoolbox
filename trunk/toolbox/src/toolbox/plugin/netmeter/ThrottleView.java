package toolbox.plugin.netmeter;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import toolbox.util.io.Bandwidth;

/**
 * ThrottleView is a UI pasted on top of Bandwidth.
 */
public class ThrottleView extends JPanel
{
    private JSlider throttleSlider_;
    
    private Bandwidth bandwidth_;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ThrottleView.
     * 
     * 
     */
    public ThrottleView()
    {
        buildView();
    }
    
    /**
     * Creates a ThrottleView.
     */
    public ThrottleView(Bandwidth bandwidth)
    {
        buildView();
        setBandwidth(bandwidth);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        throttleSlider_ = new JSlider(1, 100, 10);
        
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, throttleSlider_);
        
        throttleSlider_.addChangeListener(new MyChangeListener());
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the bandwidth.
     * 
     * @return Bandwidth
     */
    public Bandwidth getBandwidth()
    {
        return bandwidth_;
    }
    
    
    /**
     * Sets the bandwidth.
     * 
     * @param bandwidth The bandwidth to set.
     */
    public void setBandwidth(Bandwidth bandwidth)
    {
        bandwidth_ = bandwidth;
        
        throttleSlider_.setValue((int) (bandwidth.getBandwidth() / 1000));
    }
    
    //--------------------------------------------------------------------------
    // MyChangeListener
    //--------------------------------------------------------------------------
    
    class MyChangeListener implements ChangeListener
    {
        /**
         * @see javax.swing.event.ChangeListener#stateChanged(
         *      javax.swing.event.ChangeEvent)
         */
        public void stateChanged(ChangeEvent e)
        {
            JSlider slider = (JSlider) e.getSource();
            int value = slider.getValue();
            bandwidth_.updateBandwidth(value * 1000);
        }
    }
}
