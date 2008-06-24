package toolbox.plugin.jdbc.action;

import org.apache.commons.lang.StringUtils;

import toolbox.plugin.jdbc.QueryPlugin;

/**
 * ResultFormatter is responsible for pre and post formatting of the results
 * of a SQL statement.
 */
public class ResultFormatter implements QueryPlugin.IResultFormatter
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Reference to the query plugin.
     */
    private QueryPlugin plugin_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ResultFormatter.
     * 
     * @param plugin Query plugin reference.
     */
    public ResultFormatter(QueryPlugin plugin)
    {
        plugin_ = plugin;
    }
    
    //--------------------------------------------------------------------------
    // QueryPlugin.IResult Interface
    //--------------------------------------------------------------------------
    
    /**
     * If the 'show sql in results' flag is set to true, then the actual sql
     * that was executed is prepended to the sql output.
     * 
     * @see toolbox.plugin.jdbc.QueryPlugin.IResultFormatter#preFormat(
     *      java.lang.String, java.lang.String)
     */
    public String preFormat(String sql, String results)
    {
        return plugin_.isShowSqlInResults()
            ? StringUtils.repeat("=", results.indexOf('\n')) + "\n" + sql + "\n"
            : "";
    }
    
    
    /**
     * Doesn't do anything for right now.
     * 
     * @see toolbox.plugin.jdbc.QueryPlugin.IResultFormatter#postFormat(
     *      java.lang.String, java.lang.String)
     */
    public String postFormat(String sql, String results)
    {
        return "";
    }
}