package toolbox.jsourceview;

/**
 * Source code parser state machine constants.
 */
public interface MachineConstants
{
    /** Start state */
    int STARTSTATE = 0;
    
    /** State 1 */
    int STATE1 = 1;
    
    /** State 2 */
    int STATE2 = 2;
    
    /** State 3 */
    int STATE3 = 3;
    
    /** State 4 */
    int STATE4 = 4;
    
    /** State 5 */
    int STATE5 = 5;
    
    /** State 6 */
    int STATE6 = 6;
    
    /** State 7 */
    int STATE7 = 7;
    
    /** State 8 */
    int STATE8 = 8;
    
    /** EndState */
    int ENDSTATE = 9;
    
    /** Any character */
    int ANY_CHAR = 0;
    
    /** End of line */
    int EOL = 1;
    
    /** Line comment */
    int LINE_COMMENT = 2;
    
    /** Begin of comment */
    int COMMENT_BEGIN = 3;
    
    /** End of comment */
    int COMMENT_END = 4;
    
    /** Line comment string */
    String STR_LINE_COMMENT = "//";
    
    /** Comment begin string */
    String STR_COMMENT_BEGIN = "/*";
    
    /** Comment end string */
    String STR_COMMENT_END = "*/";
}