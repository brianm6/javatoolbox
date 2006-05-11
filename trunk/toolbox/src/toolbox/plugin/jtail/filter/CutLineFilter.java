package toolbox.plugin.jtail.filter;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Filter that cuts a specified number of columns from a line of text based on 
 * a simple cut expression. The expression is as follows: beginColumn-endColumn.
 */
public class CutLineFilter extends AbstractLineFilter
{
    // TODO: Update cut expression to support "x-y,a-b,..."
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /** 
     * Cut expression.
     */
    private String cut_;
    
    /** 
     * Beginning column number. 
     */
    private int begin_;
    
    /** 
     * Ending column number.
     */
    private int end_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CutLineFilter.
     */
    public CutLineFilter()
    {
        begin_ = 0;
        end_ = 0;
    }

    //--------------------------------------------------------------------------
    // ILineFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Filters a line by cutting out a column range.
     *  
     * @see toolbox.plugin.jtail.filter.ILineFilter#filter(java.lang.StringBuffer)
     */
    public boolean filter(StringBuffer line)
    {
        boolean NOOP = true;
        
        if (!isEnabled())
            return NOOP;
            
        if (line.length() == 0)
            return NOOP;
        
        int localBegin = begin_;
        int localEnd = end_;
        int len = line.length();
        
        if (localBegin >= len)
            return NOOP;
            
        if (localEnd > len)
            localEnd = len;
        
        line.delete(localBegin, localEnd);
        return true;
    }
    
    /*
     * @see toolbox.plugin.jtail.filter.ILineFilter#filter(java.lang.StringBuffer, java.util.List)
     */
    public boolean filter(StringBuffer line, List segments)
    {
        return filter(line);
    }
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the cut expression.
     * 
     * @param cut Cut expression.
     * @throws IllegalArgumentException on bad cut expression.
     */
    public void setCut(String cut) throws IllegalArgumentException
    {
        cut_ = cut;

        if (StringUtils.isBlank(cut_))
            setEnabled(false);
        else
            parseCut();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Parses the cut expression.
     * 
     * @throws IllegalArgumentException if the expression is not valid.
     */    
    protected void parseCut()
    {
        StringTokenizer st = new StringTokenizer(cut_, "-");        
        
        Validate.isTrue(
            st.countTokens() == 2, 
            "Cut expression should be of form x-y. " + cut_);
        
        begin_ = Integer.parseInt(st.nextToken()) - 1;
        end_   = Integer.parseInt(st.nextToken());

        Validate.isTrue(
            begin_ != end_,
            "Begin cannot be equal to end. " + cut_);
            
        Validate.isTrue (
            begin_ < end_,
            "Begin cannot be greater than end. " + cut_);            
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /*
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}