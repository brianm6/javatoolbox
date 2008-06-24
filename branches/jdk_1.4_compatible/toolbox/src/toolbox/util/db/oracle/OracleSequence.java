package toolbox.util.db.oracle; 

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Javabean that represents an Oracle sequence.
 */
public class OracleSequence 
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private String owner_;
    private String name_;
    private int minValue_;
    private int maxValue_;
    private int incrementBy_;
    private boolean cycleFlag_;
    private boolean orderFlag_;
    private int cacheSize_;
    private int lastNumber_;
    
    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------
    
    /**
     * getCacheSize
     * 
     * @return int
     */
    public int getCacheSize()
    {
        return cacheSize_;
    }


    /**
     * isCycleFlag
     * 
     * @return boolean
     */
    public boolean isCycleFlag()
    {
        return cycleFlag_;
    }


    /**
     * getIncrementBy
     * 
     * @return int
     */
    public int getIncrementBy()
    {
        return incrementBy_;
    }


    /**
     * getLastNumber
     * 
     * @return int
     */
    public int getLastNumber()
    {
        return lastNumber_;
    }


    /**
     * getMaxValue
     * 
     * @return int
     */
    public int getMaxValue()
    {
        return maxValue_;
    }


    /**
     * getMinValue
     * 
     * @return int
     */
    public int getMinValue()
    {
        return minValue_;
    }


    /**
     * getName
     * 
     * @return String
     */
    public String getName()
    {
        return name_;
    }


    /**
     * isOrderFlag
     * 
     * @return boolean
     */
    public boolean isOrderFlag()
    {
        return orderFlag_;
    }


    /**
     * getOwner
     * 
     * @return String
     */
    public String getOwner()
    {
        return owner_;
    }


    /**
     * setCacheSize
     * 
     * @param i Cache size.
     */
    public void setCacheSize(int i)
    {
        cacheSize_ = i;
    }


    /**
     * setCycleFlag
     * 
     * @param b Cycle flag.
     */
    public void setCycleFlag(boolean b)
    {
        cycleFlag_ = b;
    }


    /**
     * setIncrementBy
     * 
     * @param i Increment size.
     */
    public void setIncrementBy(int i)
    {
        incrementBy_ = i;
    }


    /**
     * setLastNumber
     * 
     * @param i Last number in sequence.
     */
    public void setLastNumber(int i)
    {
        lastNumber_ = i;
    }


    /**
     * setMaxValue
     * 
     * @param i Sequence max value.
     */
    public void setMaxValue(int i)
    {
        maxValue_ = i;
    }


    /**
     * setMinValue
     * 
     * @param i Sequence min value.
     */
    public void setMinValue(int i)
    {
        minValue_ = i;
    }


    /**
     * setName
     * 
     * @param string Sequeunce name.
     */
    public void setName(String string)
    {
        name_ = string;
    }


    /**
     * setOrderFlag
     * 
     * @param b Order flag.
     */
    public void setOrderFlag(boolean b)
    {
        orderFlag_ = b;
    }


    /**
     * setOwner
     * 
     * @param string Owner
     */
    public void setOwner(String string)
    {
        owner_ = string;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Dumps all attributes to a string.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() 
    {
        return ToStringBuilder.reflectionToString(this, 
            ToStringStyle.MULTI_LINE_STYLE);
    }
}