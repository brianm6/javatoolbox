package toolbox.util.db;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * SQLFormatter is a pretty printer for SQL statements.
 */
public class SQLFormatter
{
    // TODO: Change formatter to leave "select *" on the same line. Apply to all
    
    private static final Logger logger_ = Logger.getLogger(SQLFormatter.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Major SQL keywords.
     */
    private static final String MAJOR_WORDS = 
        "|SELECT|FROM|WHERE|ORDER BY|GROUP BY|HAVING|UPDATE|SET|INSERT|INTO" +
        "|VALUES|DELETE|UNION|ALL|MINUS|";
                                              
    /**
     * Minor SQL keywords.
     */
    private static final String MINOR_WORDS = 
        "|COUNT|SUM|AVG|MIN|MAX|DISTINCT|AS|ANY|AND|OR|XOR|NOT|LIKE|IN|EXISTS" +
        "|IS|NULL|";
    
    /**
     * SQL function names.
     */
    private static final String FUNCTION_WORDS = "|COUNT|SUM|AVG|MIN|MAX|";
    
    /**
     * SQL subselect.
     */
    private static final String SUB_SELECT = "|SELECT|";
    
    /**
     * SQL delimiters.
     */
    private static final String DELIMITERS = "(),";
    
    /**
     * Maximum number of indents.
     */
    private static final int MAX_INDENTS = 16;
    
    /**
     * Code for nothing.
     */
    private static final int NOTHING = 0;
    
    /**
     * Code for a space.
     */
    private static final int SPACE = 1;
    
    /**
     * Code for a new line character.
     */
    private static final int NEW_LINE = 2;
    
    //--------------------------------------------------------------------------
    // Escape Characters
    //--------------------------------------------------------------------------

    /**
     * Escape characters.
     */
    private static final String ESCAPES[][] = {
        {"'", "'", "" }, 
        {"\"", "\"", ""}, 
        {"/*", "*/", "1"}, 
        {"--", "\n" /*"\r\n"*/, "2"}
    };

    /**
     * SQL escape token.
     */
    private static final String ESCAPE_TOKEN = "\001";

    //--------------------------------------------------------------------------
    // 2nd dimension indices into ESCAPES
    //--------------------------------------------------------------------------
    
    /**
     * Index into ESCAPES[] of the escape open string.
     */
    private static final int INDEX_ESCAPE_OPEN  = 0;
    
    /**
     * Index into ESCAPES[] of the escape close string.
     */
    private static final int INDEX_ESCAPE_CLOSE = 1;
    
    /**
     * Index into ESCAPES[] of the misc string.
     */
    private static final int INDEX_ESCAPE_MISC  = 2;
    
    //--------------------------------------------------------------------------
    // ESCAPES values for INDEX_ESCAPE_MISC 
    //--------------------------------------------------------------------------
    
    /**
     * Value in ESCAPES[i][INDEX_ESCAPE_MISC] for a single quote escape.
     */
    private static final String ESCAPE_MISC_SINGLE_QUOTE = "";
    
    /**
     * Value in ESCAPES[i][INDEX_ESCAPE_MISC] for a double quote escape.
     */
    private static final String ESCAPE_MISC_DOUBLE_QUOTE = "";
    
    /**
     * Value in ESCAPES[i][INDEX_ESCAPE_MISC] for a single line comment escape.
     */
    private static final String ESCAPE_MISC_SINGLE_LINE_COMMENT = "2";
    
    /**
     * Value in ESCAPES[i][INDEX_ESCAPE_MISC] for a multiline comment escape.
     */
    private static final String ESCAPE_MISC_COMMENT = "1";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Newline string.
     */
    private String newLine_;
    
    /**
     * Indentation string.
     */
    private String indent_;
    
    /**
     * Caps mode for major sql keywords.
     */
    private CapsMode majorCapsMode_;

    /**
     * Caps mode for minor sql keywords.
     */
    private CapsMode minorCapsMode_;

    /**
     * Caps mode for tables name, column names, etc.
     */
    private CapsMode namesCapsMode_;

    /**
     * Flag to insert a newline before a SQL "and" keyword.
     */
    private boolean newLineBeforeAnd_;
    
    /**
     * Flag to send debug output to the logger.
     */
    private boolean debug_;

    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a SQLFormatter with the following defaults.
     * <ul>
     *  <li>Newline is \n
     *  <li>Indent is 4 spaces
     *  <li>All capitalization modes are set to preserve case.
     *  <li>New lines are inserted before an AND
     *  <li>Debug is set to false
     * </ul> 
     */
    public SQLFormatter()
    {
        setNewLine("\n");
        setIndent(4);
        setMajorCapsMode(CapsMode.PRESERVE);
        setMinorCapsMode(CapsMode.PRESERVE);
        setNamesCapsMode(CapsMode.PRESERVE);
        setNewLineBeforeAnd(true);
        setDebug(false);
    }

    
    //--------------------------------------------------------------------------
    // Accessors
    //--------------------------------------------------------------------------
    
    /**
     * Returns the string used for indentation.
     * 
     * @return String
     */
    public String getIndent()
    {
        return indent_;
    }
    
    
    /**
     * Returns true if debugging is turned on, false otherwise.
     * 
     * @return boolean
     */
    public boolean isDebug() 
    {
        return debug_;
    }
    
    //--------------------------------------------------------------------------
    // Mutators
    //--------------------------------------------------------------------------
    
    /**
     * Set the number of spaces used for indentation.
     * 
     * @param i Number of spaces to use for indentation.
     */
    public void setIndent(int i)
    {
        if (i < 0)
            indent_ = "\t";
        else
            indent_ = StringUtils.repeat(" ", i);
    }

    
    /**
     * Set the flag to turn on new lines before an AND.
     * 
     * @param flag True to embed a newline, false otherwise.
     */
    public void setNewLineBeforeAnd(boolean flag)
    {
        newLineBeforeAnd_ = flag;
    }

    
    /**
     * Sets the new line string (The default value is the same as that of
     * the current operating system).
     * 
     * @param s String representing this platforms newline character.
     */
    public void setNewLine(String s)
    {
        for (int i = 0; i < ESCAPES.length; i++)
        {
            for (int j = 0; j < ESCAPES[0].length; j++)
                if (ESCAPES[i][j].equals(newLine_))
                    ESCAPES[i][j] = s;
        }

        newLine_ = s;
    }

    
    /**
     * Sets the debug flag.
     * 
     * @param flag Debug flag.
     */
    public void setDebug(boolean flag)
    {
        debug_ = flag;
    }

    /**
     * Returns the minorCapsMode.
     * 
     * @return CapsMode
     */
    public CapsMode getMinorCapsMode()
    {
        return minorCapsMode_;
    }
    
    
    /**
     * Sets the value of minorCapsMode.
     * 
     * @param minorCapsMode The minorCapsMode to set.
     */
    public void setMinorCapsMode(CapsMode minorCapsMode)
    {
        minorCapsMode_ = minorCapsMode;
    }
    
    
    /**
     * Returns the namesCapsMode.
     * 
     * @return CapsMode
     */
    public CapsMode getNamesCapsMode()
    {
        return namesCapsMode_;
    }
    
    
    /**
     * Sets the value of namesCapsMode.
     * 
     * @param namesCapsMode The namesCapsMode to set.
     */
    public void setNamesCapsMode(CapsMode namesCapsMode)
    {
        namesCapsMode_ = namesCapsMode;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns a formatted sql statement.
     * 
     * @param sql SQL statement to format.
     * @return Formatted SQL statement.
     */
    public String format(String sql)
    {
        List list = new ArrayList();
        
        for (int sqlIndex = 0, n = sql.length(); sqlIndex < n; sqlIndex++)
        {
            for (int j = 0, m = ESCAPES.length; j < m; j++)
            {
                String escapeOpen  = ESCAPES[j][INDEX_ESCAPE_OPEN];
                String escapeClose = ESCAPES[j][INDEX_ESCAPE_CLOSE];
                String escapeType  = ESCAPES[j][INDEX_ESCAPE_MISC];
                
                if (!sql.regionMatches(
                    sqlIndex, escapeOpen, 0, escapeOpen.length()))
                    continue;
                
                // Index of the char after the escape open
                int afterEscapeOpen = sqlIndex + escapeOpen.length();
                
                // Search for the escape closing that occurs after the escape open 
                int escapeCloseIndex = sql.indexOf(escapeClose, afterEscapeOpen);
                
                // If closing character for escape sequence not found in the
                // entire statement
                if (escapeCloseIndex == -1) 
                {
                    // Search for a newline in the statement after the escaope open
                    escapeCloseIndex = sql.indexOf("\n", afterEscapeOpen);
                    
                    // If newlne not found in statement
                    if (escapeCloseIndex == -1) 
                    {
                        // Search for a carraige return in the statement after
                        // the escape open
                        escapeCloseIndex = sql.indexOf("\r", afterEscapeOpen);
                        
                        // If carraige return not found in statement
                        if (escapeCloseIndex == -1)
                        {
                            // Set close index to the last char in the statement
                            escapeCloseIndex = sql.length() - 1;
                        }
                    }
                    
                    escapeCloseIndex++;
                } 
                else
                {
                    escapeCloseIndex += escapeClose.length();
                }
                
                // Extract the text between the escape open/close chars
                String escapeText = sql.substring(sqlIndex, escapeCloseIndex);
                
                // If a single line comment starting with a --
                if (escapeType.equals(ESCAPE_MISC_SINGLE_LINE_COMMENT))
                {
                    // Embed the single line comment in a multi-line comment
                    escapeText = 
                        "/*" 
                        + escapeText.trim().substring(2 /* length of -- */) 
                        + " */";
                }
                
                list.add(escapeText);
                
                // String of the sql we have parsed up to this point
                String sqlParsed = sql.substring(0, sqlIndex);
                
                // ???
                String sqlUnparsed;
                
                // If there are more characters to parse after the escape's
                // closing string
                if (escapeCloseIndex < sql.length())
                {
                    // Extract the unparsed remainder of the sql statement
                    sqlUnparsed = sql.substring(escapeCloseIndex);
                }
                else
                {
                    // Nothing left to parse
                    sqlUnparsed = "";
                }
                
                String s9 = "\001";
                
                // If the escape type is not a quote or double quote
                if (!escapeType.equals(""))
                {
                    // If the last character parsed is not a space
                    if (!sqlParsed.endsWith(" "))
                    {
                        // Prefix with a space
                        s9 = " " + s9;
                    }
                    
                    // If unparsed sql starts with a space
                    if (!sqlUnparsed.startsWith(" "))
                    {
                        // Suffix with a space
                        s9 = s9 + " ";
                    }
                }
                
                sql = sqlParsed + s9 + sqlUnparsed;
                break;
            }

        }

        //=====================================
        // Function tokenizeSQL()
        //=====================================
        
        List sqlTokens = new ArrayList();
        
        // Tokenize into words using space delimiter
        for (StringTokenizer st = new StringTokenizer(sql); st.hasMoreTokens();)
        {
            String sqlToken = st.nextToken();

            // Tokenize into words, (, ), and commas 
            for (StringTokenizer st1 = new StringTokenizer(sqlToken, "(),", true);
                st1.hasMoreTokens();
                sqlTokens.add(st1.nextToken()));
        }

        //=====================================
        // Function consolidateMajorKeywords()
        //=====================================
        
        // Find out of two concatted tokens qualify as a major keyword
        for (int k = 0; k < sqlTokens.size() - 1; k++)
        {
            // Concat current token and next token together
            String neighbors = sqlTokens.get(k) + " " + sqlTokens.get(k + 1);

            if (isMajor(neighbors))
            {
                // Keyword found, merge the two place back in the list
                sqlTokens.set(k, neighbors);
                sqlTokens.remove(k + 1);
            }
        }

        //=====================================
        // Function copyTokensToArray()
        //=====================================
        
        int numTokens = sqlTokens.size();
        
        // Create array of tokens. Pad first and last tokens as empty string
        String keywords[] = new String[numTokens += 2];
        keywords[0] = "";
        keywords[numTokens - 1] = "";
        
        // Copy tokens into keywords array
        for (int i1 = 0; i1 < sqlTokens.size(); i1++)
            keywords[i1 + 1] = (String) sqlTokens.get(i1);

        //=====================================
        // Function applyCapsMode()
        //=====================================
        
        for (int keywordIndex = 0; keywordIndex < numTokens; keywordIndex++)
        {
            if (debug_) 
                logger_.debug(keywords[keywordIndex]);
            
            CapsMode capsMode = CapsMode.PRESERVE;
            
            if (isMajor(keywords[keywordIndex]))
                capsMode = getMajorCapsMode();
            
            if (isMinor(keywords[keywordIndex]))
                capsMode = getMinorCapsMode();
            
            if (isName(keywords[keywordIndex]))
                capsMode = getNamesCapsMode();
           
            if (capsMode == CapsMode.LOWERCASE)
                keywords[keywordIndex] = keywords[keywordIndex].toLowerCase();
            else if (capsMode == CapsMode.UPPERCASE)
                keywords[keywordIndex] = keywords[keywordIndex].toUpperCase();
            else if (capsMode == CapsMode.PRESERVE)
                ; // No op
            else
                throw new IllegalArgumentException(
                    "Unsupported caps mode " + capsMode);
            
            if (debug_)
                logger_.debug(keywords[keywordIndex]);
        }

        //=====================================
        // Function scanMajors()
        //=====================================
        
        int newlineFlags[] = new int[numTokens];
        
        // Loop over keywords leaving out first and last indices (empty strings)
        for (int current = 1; current < numTokens - 1; current++)
        {
            String keyword = keywords[current]; // Current keyword
            int prev = current - 1;             // Previous keyword index
            newlineFlags[current] = 1;                 // Init current keyword idx to 1
            
            if (isMajor(keyword))
            {
                // Major keyword -> keyword idx = prev idx = 2
                newlineFlags[prev] = 2;
                newlineFlags[current] = 2;
            }
            else if (keyword.equals(","))
            {
                // Keyword is a comma --> flags[current] = 2  flags[prev] = 0
                newlineFlags[current] = 2;
                newlineFlags[prev] = 0;
            }
            else if (keyword.equals("("))
            {
                newlineFlags[current] = 0;
                
                if (isFunction(keywords[prev]) || isName(keywords[prev]))
                    newlineFlags[prev] = 0;
            }
            else if (keywords[current].equals(")"))
            {    
                newlineFlags[prev] = 0;
            }
            else if (keywords[current].equalsIgnoreCase("AND"))
            {    
                if (newLineBeforeAnd_)
                    newlineFlags[prev] = 2;
                else
                    newlineFlags[current] = 2;
            }
        }

        //=====================================
        // Function scanSubSelects()
        //=====================================
        
        newlineFlags[numTokens - 2] = 2;
        int subIdx = 0;
        int indentFlags[] = new int[numTokens];
        int subFlags[] = new int[16];

        // Iterate over sql tokens
        for (int current = 0; current < numTokens; current++)
        {
            String keyword = keywords[current];
            int prev = current - 1;
            int next = current + 1;
            
            if (keyword.equals(")"))
            {    
                if (subFlags[subIdx] == 0)
                {
                    subIdx--;
                    
                    if (current > 0)
                        newlineFlags[prev] = 2;
                }
                else
                {
                    subFlags[subIdx]--;
                }
            }
            
            if (isMajor(keyword))
                indentFlags[current] = subIdx * 2;
            else
                indentFlags[current] = subIdx * 2 + 1;
            
            if (keyword.equals("("))
            {    
                if (isSubSelect(keywords[next]))
                {
                    if (subIdx < 16)
                        subIdx++;
                    
                    subFlags[subIdx] = 0;
                }
                else
                {
                    subFlags[subIdx]++;
                }
            }
        }

        //=====================================
        // Function ????
        //=====================================
        
        String as1[] = new String[] {"", " ", newLine_};
        StringBuffer sb = new StringBuffer();

        // Loop over tokens except first and last
        for (int current = 1; current < numTokens - 1; current++)
        {
            // Indent
            if (newlineFlags[current - 1] == 2)
                sb.append(StringUtils.repeat(indent_, indentFlags[current]));
            
            sb.append(keywords[current] + as1[newlineFlags[current]]);
        }

        sql = sb.toString();
        
        for (int i3 = 0; i3 < list.size(); i3++)
        {
            int j3 = sql.indexOf("\001");
            
            sql = sql.substring(0, j3)
                  + (String) list.get(i3)
                  + sql.substring(j3 + 1);
        }

        if (debug_)
        {
            StringBuffer sb1 = new StringBuffer();
            sb1.append("Tokens:\n");
            
            for (int k3 = 1; k3 < numTokens - 1; k3++)
                sb1.append(indentFlags[k3] + " [" + keywords[k3] + "] " + newlineFlags[k3] + "\n");

            sb1.append("Escapes:\n");
            
            for (int l3 = 0; l3 < list.size(); l3++)
                sb1.append((String) list.get(l3) + "\n");

            logger_.debug(sb1.toString());
        }
        
        return sql;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if the string is a name, false otherwise.
     * 
     * @param s SQL name.
     * @return boolean
     */
    protected static boolean isName(String s)
    {
        return !isIn(s, MAJOR_WORDS) && !isIn(s, MINOR_WORDS);
    }

    
    /**
     * Returns true if the string is a SQL function, false otherwise.
     * 
     * @param s SQL function. 
     * @return boolean
     */
    protected static boolean isFunction(String s)
    {
        return isIn(s, FUNCTION_WORDS);
    }
    
    
    /**
     * Returns true if the string is a minor SQL keyword.
     * 
     * @param s SQL keyword.
     * @return boolean
     */
    protected static boolean isMinor(String s)
    {
        return isIn(s, MINOR_WORDS);
    }

    
    /**
     * Returns true if token s is in string s1.
     * 
     * @param token Token to search for.
     * @param tokens String with embedded tokens.
     * @return boolean
     */
    protected static boolean isIn(String token, String tokens)
    {
        return tokens.indexOf("|" + token.toUpperCase() + "|") > -1;
    }

    
    /**
     * Returns true if the string is a subselect.
     * 
     * @param s SQL subselect.
     * @return boolean
     */
    protected static boolean isSubSelect(String s)
    {
        return isIn(s, SUB_SELECT);
    }

    
    /**
     * Returns true if the string is a major SQL keyword.
     * 
     * @param s SQL keyword.
     * @return boolean
     */
    protected static boolean isMajor(String s)
    {
        return isIn(s, MAJOR_WORDS);
    }
    
    
    /**
     * Returns the majorCapsMode.
     * 
     * @return CapsMode
     */
    public CapsMode getMajorCapsMode()
    {
        return majorCapsMode_;
    }
    
    
    /**
     * Sets the value of majorCapsMode.
     * 
     * @param majorCapsMode The majorCapsMode to set.
     */
    public void setMajorCapsMode(CapsMode majorCapsMode)
    {
        majorCapsMode_ = majorCapsMode;
    }
}