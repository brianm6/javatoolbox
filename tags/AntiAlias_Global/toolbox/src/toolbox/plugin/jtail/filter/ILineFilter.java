package toolbox.jtail.filter;

/**
 * Filter for a single line of text
 */
public interface ILineFilter
{
    /**
     * Filters the line of text
     * 
     * @param line  Line of text to filter
     * @return Mutated line of text as processed by the filter.
     */
    public String filter(String line);
    
    
    /**
     * Is the filter enabled?
     * 
     * @return True if the filter is enabled, false otherwise
     */
    public boolean isEnabled();
    
    
    /**
     * Enables/disables the filter
     * 
     * @param b True to enable the filter, false to disable the filter
     */
    public void setEnabled(boolean b);
}
