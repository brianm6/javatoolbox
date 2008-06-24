package toolbox.plugin.jtail.filter;

import java.util.List;

import toolbox.util.service.Enableable;

/**
 * Filters a single line of text by mutating the contents and applying styles
 * to define rendering.
 */
public interface ILineFilter extends Enableable
{
    /**
     * Filters the line of text.
     * 
     * @param line Line of text to filter.
     * @return True if line is valid, false otherwise.
     */
    boolean filter(StringBuffer line);
 
    
    /**
     * Experimental support for styled segments so that filters can apply
     * styles (colors, fonts, styles) to the line of text.
     * 
     * @param line Line of text to filter.
     * @param segments Array of StyledSegments that define any styles to be 
     *        applied to the line of text.
     * @return True if the line is valid, false otherwise.
     */
    boolean filter(StringBuffer line, List segments);
}