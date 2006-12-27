package toolbox.plugin.jdbc;

/**
 * Constants for the QueryPlugin.
 */
public interface QueryPluginConstants
{
    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    /**
     * Root preferences element for the query plugin.
     */
    static final String NODE_QUERY_PLUGIN = "QueryPlugin";

    /**
     * Child of QueryPlugin that contains a single "remembered" SQL stmt.
     */
    static final String NODE_HISTORY_ITEM = "HistoryItem";

    //--------------------------------------------------------------------------
    // JavaBean Constants
    //--------------------------------------------------------------------------
    
    /**
     * Boolean property that appends errors to the output console instead of an
     * error dialog box.
     */
    static final String PROP_SEND_ERROR_TO_CONSOLE = "sendErrorToConsole";

    /**
     * Property that continues execution of a batch of SQL statements regardless
     * of any one SQL statement's failure.
     */
    static final String PROP_CONTINUE_ON_ERROR = "continueOnError";

    /**
     * Property that specifies the termination string for a SQL statement.
     */
    static final String PROP_SQL_TERMINATOR = "sqlTerminator";

    /**
     * Property that specifies the number of lines necessary as output for 
     * the console to scroll to the bottom.
     */
    static final String PROP_AUTOSCROLL_THRESHOLD = "autoScrollThreshold";

    /**
     * Attribute of QueryPlugin that stores the max number of entries in the sql
     * history popup menu before getting truncated.
     */
    static final String PROP_MAX_HISTORY = "maxHistory";

    /**
     * Boolean property that controls whether the executed sql statement is
     * shown in the results immediately before the sql statement's output. 
     */
    static final String PROP_SQL_IN_RESULTS = "showSqlInResults";
    
    /**
     * List of javabean properties that are persisted.
     */
    static final String[] SAVED_PROPS = {
        PROP_SEND_ERROR_TO_CONSOLE,
        PROP_CONTINUE_ON_ERROR,
        PROP_SQL_TERMINATOR,
        PROP_AUTOSCROLL_THRESHOLD,
        PROP_MAX_HISTORY,
        PROP_SQL_IN_RESULTS
    };

    //--------------------------------------------------------------------------
    // CardLayout Constants
    //--------------------------------------------------------------------------
    
    /**
     * Name of the card in the results panel associated with the results text 
     * area.
     */
    static final String CARD_TEXTAREA = "resultsTextArea";

    /**
     * Name of the card in the results panel associated with the results table.
     */
    static final String CARD_TABLE = "resultsTable";
}
