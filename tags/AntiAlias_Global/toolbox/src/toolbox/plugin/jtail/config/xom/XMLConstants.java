package toolbox.jtail.config.xom;

/**
 * XML configuration constants
 */
public interface XMLConstants
{
    // JTail XML element
    public static final String NODE_JTAIL    = "JTail";
    public static final String ATTR_HEIGHT   = "height";
    public static final String ATTR_WIDTH    = "width";
    public static final String ATTR_X        = "x";
    public static final String ATTR_Y        = "y";
    public static final String ATTR_DIR      = "dir";
        
    // Defaults XML element
    public static final String NODE_DEFAULTS = "Defaults";

    // Tail XML element
    public static final String NODE_TAIL          = "Tail";
    public static final String   ATTR_AUTOSCROLL  = "autoScroll";
    public static final String   ATTR_LINENUMBERS = "showLineNumbers";
    public static final String   ATTR_ANTIALIAS   = "antiAlias";
    public static final String   ATTR_AUTOSTART   = "autoStart";
    public static final String   NODE_FILE        = "File";
    public static final String     ATTR_FILENAME  = "name";
    
    // Font XML element
    public static final String NODE_FONT        = "Font";
    public static final String ATTR_FAMILY      = "family";
    public static final String ATTR_STYLE       = "style";        
    public static final String ATTR_SIZE        = "size";

    // Regular expression XML element
    public static final String NODE_REGULAR_EXPRESSION = "Regex";
    public static final String ATTR_EXPRESSION = "expression";
    public static final String ATTR_MATCH_CASE = "matchCase";
    
    // Cut expression XML element
    public static final String NODE_CUT_EXPRESSION = "Cut";
}