package toolbox.plugin.jtail.filter;

import toolbox.util.service.Enableable;

/**
 * Filter for a single line of text.
 */
public interface ILineFilter extends Enableable
{
    /**
     * Filters the line of text.
     * 
     * @param line  Line of text to filter.
     * @return Mutated line of text as processed by the filter.
     */
    String filter(String line);
}
