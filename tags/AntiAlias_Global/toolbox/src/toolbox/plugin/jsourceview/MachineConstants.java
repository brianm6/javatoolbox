package toolbox.jsourceview;

/**
 * Machine constants
 */
public interface MachineConstants
{
    /** Start state */
    public static final int STARTSTATE = 0;
    
    /** State 1 */
    public static final int STATE1 = 1;
    
    /** State 2 */
    public static final int STATE2 = 2;
    
    /** State 3 */
    public static final int STATE3 = 3;
    
    /** State 4 */
    public static final int STATE4 = 4;
    
    /** State 5 */
    public static final int STATE5 = 5;
    
    /** State 6 */
    public static final int STATE6 = 6;
    
    /** State 7 */
    public static final int STATE7 = 7;
    
    /** State 8 */
    public static final int STATE8 = 8;
    
    /** EndState */
    public static final int ENDSTATE = 9;
    
    /** Any character */
    public static final int ANY_CHAR = 0;
    
    /** End of line */
    public static final int EOL = 1;
    
    /** Line comment */
    public static final int LINE_COMMENT = 2;
    
    /** Begin of comment */
    public static final int COMMENT_BEGIN = 3;
    
    /** End of comment */
    public static final int COMMENT_END = 4;
    
    /** Line comment string */
    public static final String STR_LINE_COMMENT = "//";
    
    /** Comment begin string */
    public static final String STR_COMMENT_BEGIN = "/*";
    
    /** Comment end string */
    public static final String STR_COMMENT_END = "*/";
}