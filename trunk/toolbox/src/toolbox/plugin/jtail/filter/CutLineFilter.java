package toolbox.jtail.filter;

import java.util.StringTokenizer;

import toolbox.util.Assert;
import toolbox.util.AssertionException;
import toolbox.util.StringUtil;


/**
 * Filter that cuts a specified number of columns from a line
 */
public class CutLineFilter extends AbstractLineFilter
{
    /**
     * Cut expression
     */
    private String cut_;
    
    private int begin_ = 0;
    private int end_   = 0;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for CutFilter.
     */
    public CutLineFilter()
    {
        cut_ = null;    
    }

    //--------------------------------------------------------------------------
    //  ILineFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Filters a line by cutting 
     * 
     * @param line  Line to cut
     * @return Line after cut operation
     */
    public String filter(String line)
    {
        if (!isEnabled())
            return line;
            
        if (line == null || line.length() == 0)
            return line;
        
        if (begin_ < 0 || end_ < 0)
            return line;
        
        StringBuffer sb = new StringBuffer(line);
        
        int localBegin = begin_;
        int localEnd   = end_;
        
        int len = line.length();
        
        if (localBegin >= len)
            return line;
            
        if (localEnd > len)
            localEnd = len;
        
        sb.delete(localBegin, localEnd);
        
        return sb.toString();
    }
    
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the cut expression
     * 
     * @param  cut  Cut expression
     * @throws IllegalArgumentException on bad cut expression
     */
    public void setCut(String cut) throws IllegalArgumentException
    {
        cut_ = cut;

        if (StringUtil.isNullOrEmpty(cut))
            setEnabled(false);
        else
        {        
            try
            {
                parseCut();
            }
            catch (AssertionException ae)
            {
                throw new IllegalArgumentException(ae.getMessage());
            }
        }
    }

    
    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Parses the cut expression
     */    
    protected void parseCut()
    {
        StringTokenizer st = new StringTokenizer(cut_, "-");        
        
        Assert.equals(st.countTokens(), 2, 
            "Cut expression should be of form x-y");
        
        begin_ = Integer.parseInt((String)st.nextToken()) - 1;
        end_   = Integer.parseInt((String)st.nextToken());

        if (begin_ == end_) 
            throw new IllegalArgumentException("Begin cannot be equal to end");
            
        if (begin_ > end_)
            throw new IllegalArgumentException(
                "Begin cannot be greater than end");            
    }
}
