package toolbox.jtail.config.xom;

/**
 * XML configuration constants
 */
public interface XMLConstants
{
    // JTail XML element
    String NODE_JTAIL    = "JTail";
    String ATTR_HEIGHT   = "height";
    String ATTR_WIDTH    = "width";
    String ATTR_X        = "x";
    String ATTR_Y        = "y";
    String ATTR_DIR      = "dir";
        
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
    String NODE_FONT        = "Font";
    String ATTR_FAMILY      = "family";
    String ATTR_STYLE       = "style";        
    String ATTR_SIZE        = "size";

    // Regular expression XML element
    String NODE_REGULAR_EXPRESSION = "Regex";
    String ATTR_EXPRESSION = "expression";
    String ATTR_MATCH_CASE = "matchCase";
    
    // Cut expression XML element
    String NODE_CUT_EXPRESSION = "Cut";
}
