package toolbox.plugin.jtail.config.xom;

/**
 * XML configuration constants.
 */
public interface XMLConstants
{
    // JTail XML element
    String NODE_JTAIL = "JTail";
        
    // Defaults XML element
    String NODE_DEFAULTS = "Defaults";

    // Tail XML element
    String NODE_TAIL          = "Tail";
    String   ATTR_AUTOSCROLL  = "autoScroll";
    String   ATTR_LINENUMBERS = "showLineNumbers";
    String   ATTR_ANTIALIAS   = "antiAlias";
    String   ATTR_AUTOSTART   = "autoStart";
    String   NODE_FILE        = "File";
    String     ATTR_FILENAME  = "name";
    
    // Font XML element
    String NODE_FONT = "Font";

    // Regular expression XML element
    String NODE_REGULAR_EXPRESSION = "Regex";
    String ATTR_EXPRESSION = "expression";
    String ATTR_MATCH_CASE = "matchCase";
    
    // Cut expression XML element
    String NODE_CUT_EXPRESSION = "Cut";
}