package toolbox.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Counter that wraps around given a finite range [a..b]
 */
public class RollingCounter
{
    /** start of range **/
    private int  start_;
    
    /** end of range **/
    private int  end_;
    
    /** current position in range **/
    private int  cnt_; 
    
    /** list of counter listeners **/
    private List listeners_;
    
    /**
     * Creates a counter that wraps around
     * 
     * @param  start         Starting value of range
     * @param  end           Ending value of range
     * @param  initialValue  Initial value of counter 
     */        
    public RollingCounter(int start, int end, int initialValue)
    {
        start_     = start;
        end_       = end;
        cnt_       = initialValue;
        listeners_ = new ArrayList();
    }


    /**
     * Increments the counter by 1
     *
     * @param  Counter value after increment
     */
    public int increment()
    {   
        if (cnt_ == end_)
            fireBeforeRoll();
        
        ++cnt_;

        if (cnt_ > end_)
            cnt_ = start_;

        if (cnt_ == start_)
            fireAfterRoll();            
            
        return cnt_;
    }

    
    /**
     * Returns the current counter value
     * 
     * @return  Current counter value
     */
    public int getCount()
    {
        return cnt_;
    }


    /**
     * @return  True if the counter is at the end of the range
     */
    public boolean isAtEnd()
    {
        return (cnt_ == end_);
    }

    
    /**
     * @return  True if the counter is at the beginning of the range
     */
    public boolean isAtStart()
    {
        return (cnt_ == start_);
    }

    
    /**
     * Fires event for before a roll occurs
     */
    public void fireBeforeRoll()
    {
        for(int i=0; i<listeners_.size(); i++)
            ((IRollingCounterListener)listeners_.get(i)).beforeRoll(this);
    }

    
    /**
     * Fires event for after a roll occurs
     */
    public void fireAfterRoll()
    {
        for(int i=0; i<listeners_.size(); i++)
            ((IRollingCounterListener)listeners_.get(i)).afterRoll(this);
    }

    
    /**
     * Adds a listener
     *
     * @param  listener  Listener
     */
    public void addRollingCounterListener(IRollingCounterListener listener)
    {
        listeners_.add(listener);
    }

    
    /**
     * Dumps to string
     */
    public String toString()
    {
        return "{cnt=" + cnt_ + ", start=" + start_ + ", end=" + end_ + "}";
    }
    
    
    /**
     * Interface for notification of rolling events
     */    
    public interface IRollingCounterListener
    {
        /**
         * Called before a roll is about to happen
         * 
         * @param  rc  Counter 
         */
        public void beforeRoll(RollingCounter rc);
        
        /**
         * Called after a roll has happened
         * 
         * @param  rc  Counter
         */
        public void afterRoll(RollingCounter rc);
    }
}

