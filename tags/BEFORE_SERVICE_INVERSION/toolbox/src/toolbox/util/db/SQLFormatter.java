package toolbox.util.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.PreferencedUtil;
import toolbox.util.XOMUtil;
import toolbox.util.formatter.AbstractFormatter;
import toolbox.workspace.IPreferenced;

/**
 * SQLFormatter is a pretty printer for SQL statements.
 * <p>
 * 
 * <b>Example:</b>
 * <pre class="snippet">
 * SQLFormatter fmt = new SQLFormatter();
 * fmt.setIndent(4);
 * System.out.println(fmt.format(
 *     "select name, age from person where name = 'joe' and age > 25"));
 * </pre>
 * 
 * <b>Output:</b>
 * <pre class="snippet">
 * select
 *     name,
 *     age
 * from
 *     person
 * where
 *     name = 'joe'
 *     and age > 25
 * </pre>
 */
public class SQLFormatter extends AbstractFormatter implements IPreferenced
{
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
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    /**
     * Root preferences node.
     */
    private static final String NODE_SQLFORMATTER = "SQLFormatter";

    /**
     * Attributes of NODE_SQLFORMATTER.
     */
    public static final String[] SAVED_PROPS = new String[] {
        "debug", 
        "indent", 
        "newLineBeforeAnd", 
        //"newLine",
        "majorCapsMode",
        "minorCapsMode", 
        "namesCapsMode"
    };
    
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
        super("SQL Formatter");
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
     * Returns number of spaces to use for indentation.
     * 
     * @return int
     */
    public int getIndent()
    {
        return indent_.length();
    }
    

    /**
     * Returns newline string.
     * 
     * @return String
     */
    public String getNewLine()
    {
        return newLine_;
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
     * Returns the namesCapsMode.
     * 
     * @return CapsMode
     */
    public CapsMode getNamesCapsMode()
    {
        return namesCapsMode_;
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
     * Returns the newLineBeforeAnd.
     * 
     * @return boolean
     */
    public boolean isNewLineBeforeAnd()
    {
        return newLineBeforeAnd_;
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
     * Sets the value of minorCapsMode.
     * 
     * @param minorCapsMode The minorCapsMode to set.
     */
    public void setMinorCapsMode(CapsMode minorCapsMode)
    {
        minorCapsMode_ = minorCapsMode;
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

    
    /**
     * Sets the value of majorCapsMode.
     * 
     * @param majorCapsMode The majorCapsMode to set.
     */
    public void setMajorCapsMode(CapsMode majorCapsMode)
    {
        majorCapsMode_ = majorCapsMode;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.formatter.Formatter#format(java.io.InputStream,
     *      java.io.OutputStream)
     */
    public void format(InputStream input, OutputStream output) throws Exception
    {
        List escapeList = new ArrayList();
        String sql = IOUtils.toString(input);
        sql = escape(sql, escapeList);
        
        List sqlTokens = tokenize(sql);
        mergeKeywords(sqlTokens);
        
        String[] keywords = toPaddedArray(sqlTokens);
        applyCaps(keywords);

        int[] breakFlags = flagBreaks(keywords);
        int[] indentFlags = flagSubSelects(keywords, breakFlags);

        sql = reconstruct(keywords, breakFlags, indentFlags);
        sql = unescape(sql, escapeList);

        if (debug_)
            dumpFlags(keywords, escapeList, breakFlags, indentFlags);
        
        output.write(sql.getBytes());
        output.flush();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Flags any subselect statements in the sql statement. Returns an array
     * of indices into the SQL statement.
     * 
     * @param keywords List of keywords.
     * @param breakFlags List of break flags.
     * @return int[]
     */
    protected int[] flagSubSelects(String[] keywords, int[] breakFlags)
    {
        int numTokens = keywords.length;
        int subIdx = 0;
        int indentFlags[] = new int[numTokens];
        int subFlags[] = new int[MAX_INDENTS];

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
                        breakFlags[prev] = 2;
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
                    if (subIdx < MAX_INDENTS)
                        subIdx++;
                    
                    subFlags[subIdx] = 0;
                }
                else
                {
                    subFlags[subIdx]++;
                }
            }
        }
        return indentFlags;
    }


    /**
     * Each index of the returned array will specify a code that maps to an
     * empty string, space, or a newline as the break character for the given 
     * token at that index. Returns an array of indices into the sql statement
     * of the break locations.
     * 
     * @param keywords SQL keywords.
     * @return int[]
     */
    protected int[] flagBreaks(String[] keywords)
    {
        // TODO: Replace 0, 1, and 2 with break constants.
        
        int breakFlags[] = new int[keywords.length];
        
        // Loop over keywords leaving out first and last indices (empty strings)
        for (int current = 1; current < keywords.length - 1; current++)
        {
            String keyword = keywords[current]; // Current keyword
            int prev = current - 1;             // Previous keyword index
            breakFlags[current] = 1;            // Init current keyword idx to 1
            
            if (isMajor(keyword))
            {
                // Major keyword -> keyword idx = prev idx = 2
                breakFlags[prev] = 2;
                breakFlags[current] = 2;
            }
            else if (keyword.equals(","))
            {
                // Keyword is a comma --> flags[current] = 2  flags[prev] = 0
                breakFlags[current] = 2;
                breakFlags[prev] = 0;
            }
            else if (keyword.equals("("))
            {
                breakFlags[current] = 0;
                
                if (isFunction(keywords[prev]) || isName(keywords[prev]))
                    breakFlags[prev] = 0;
            }
            else if (keywords[current].equals(")"))
            {    
                breakFlags[prev] = 0;
            }
            else if (keywords[current].equalsIgnoreCase("AND"))
            {    
                if (newLineBeforeAnd_)
                    breakFlags[prev] = 2;
                else
                    breakFlags[current] = 2;
            }
        }
        
        // Always set next to last break char as a newline
        breakFlags[keywords.length - 2] = 2;
        
        return breakFlags;
    }

    
    /**
     * Finds all escape sequences in the sql statement and replaces them with
     * a special token that will be used as a marker to unescape them later. An
     * escape sequence includes comments (single and multi line), single quoted
     * string and double quoted strings. Returns the sql statement with an 
     * embedded ESCAPE_TOKEN for each escape sequence found.
     * 
     * @param sql SQL statement to escape.
     * @param escapeList List of escaped text found in the sql statement. Has a
     *        1-1 mapping with the ESCAPE_TOKENs in the returned statement as
     *        parsed sequentially from beginning to end.
     * @return String
     * @see #unescape(String, List)
     */
    protected String escape(String sql, List escapeList)
    {
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
                
                // Search for the escape closing that occurs after open 
                int escapeCloseIndex = 
                    sql.indexOf(escapeClose, afterEscapeOpen);
                
                // If closing character for escape sequence not found in the
                // entire statement
                if (escapeCloseIndex == -1) 
                {
                    // Search for a newline in the statement after open
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
                
                escapeList.add(escapeText);
                
                // String of the sql we have parsed up to this point
                String sqlParsed = sql.substring(0, sqlIndex);
                
                // Whatever is after the escapeed text.
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
                
                String escapeToken = ESCAPE_TOKEN;
                
                // If the escape type is not a quote or double quote
                if (!escapeType.equals(ESCAPE_MISC_SINGLE_QUOTE) &&
                    !escapeType.equals(ESCAPE_MISC_DOUBLE_QUOTE))
                {
                    // If the last character parsed is not a space
                    if (!sqlParsed.endsWith(" "))
                    {
                        // Prefix with a space
                        escapeToken = " " + escapeToken;
                    }
                    
                    // If unparsed sql starts with a space
                    if (!sqlUnparsed.startsWith(" "))
                    {
                        // Suffix with a space
                        escapeToken = escapeToken + " ";
                    }
                }
                
                // Rebuild the sql with the embedded escape token so we can
                // undo this process in the unescape() method.
                sql = sqlParsed + escapeToken + sqlUnparsed;
                break;
            }
        }
        return sql;
    }


    /**
     * Reconstructs the formatted sql statement based on the information
     * gathered about keywords, indentation, and flags.
     * 
     * @param keywords Array of sql keywords.
     * @param newlineFlags Newline flags.
     * @param indentFlags Indentations flags.
     * @return String
     */
    protected String reconstruct(
        String[] keywords, 
        int[] newlineFlags, 
        int[] indentFlags)
    {
        String choices[] = new String[] {"", " ", newLine_};
        StringBuffer sql = new StringBuffer();

        // Loop over tokens except first and last
        for (int current = 1; current < keywords.length - 1; current++)
        {
            // Indent
            if (newlineFlags[current - 1] == 2)
                sql.append(StringUtils.repeat(indent_, indentFlags[current]));
            
            sql.append(keywords[current] + choices[newlineFlags[current]]);
        }
        
        return sql.toString();
    }


    /**
     * Reinserts the escaped sequences back into the sql statement.
     * 
     * @param sql SQL statement.
     * @param escapeList List of escapes.
     * @return String
     */
    protected String unescape(String sql, List escapeList)
    {
        for (int i3 = 0; i3 < escapeList.size(); i3++)
        {
            int j3 = sql.indexOf("\001");
            
            sql = sql.substring(0, j3)
                  + (String) escapeList.get(i3)
                  + sql.substring(j3 + 1);
        }
        return sql;
    }


    /**
     * Applies the appropriate capitalization to major keywords, minor keywords,
     * and names.
     * 
     * @param keywords Array of keywords contained in the SQL statement.
     */
    protected void applyCaps(String[] keywords)
    {
        for (int i = 0; i < keywords.length; i++)
        {
            if (debug_) 
                logger_.debug(keywords[i]);
            
            CapsMode capsMode = CapsMode.PRESERVE;
            
            if (isMajor(keywords[i]))
                capsMode = getMajorCapsMode();
            
            if (isMinor(keywords[i]))
                capsMode = getMinorCapsMode();
            
            if (isName(keywords[i]))
                capsMode = getNamesCapsMode();
           
            if (capsMode == CapsMode.LOWERCASE)
                keywords[i] = keywords[i].toLowerCase();
            else if (capsMode == CapsMode.UPPERCASE)
                keywords[i] = keywords[i].toUpperCase();
            else if (capsMode == CapsMode.PRESERVE)
                ; // No op
            else
                throw new IllegalArgumentException(
                    "Unsupported caps mode " + capsMode);
            
            if (debug_)
                logger_.debug(keywords[i]);
        }
    }


    /**
     * Creates a first and last element padded array from the list of sql
     * tokens.
     * 
     * @param sqlTokens List of sql tokens.
     * @return Array of padded sql tokens.
     */
    protected String[] toPaddedArray(List sqlTokens)
    {
        // Create array of tokens. Pad first and last tokens as empty string
        int numTokens = sqlTokens.size() + 2;
        String keywords[] = new String[numTokens];
        keywords[0] = "";
        keywords[numTokens - 1] = "";
        
        // Copy tokens into keywords array
        for (int i = 0, n = sqlTokens.size(); i < n; i++)
            keywords[i + 1] = (String) sqlTokens.get(i);
        
        return keywords;
    }
    
    
    /**
     * Merges contiguous tokens into a keyword if a valid keyword is
     * constructed. The newly merged keyword replaces both tokens in the list.
     * 
     * @param sqlTokens List of sql tokens to merge.
     */
    protected void mergeKeywords(List sqlTokens)
    {
        // Find out of two concatted tokens qualify as a major keyword
        for (int i = 0; i < sqlTokens.size() - 1; i++)
        {
            // Concat current token and next token together
            String neighbors = sqlTokens.get(i) + " " + sqlTokens.get(i + 1);

            if (isMajor(neighbors))
            {
                // Keyword found, merge the two place back in the list
                sqlTokens.set(i, neighbors);
                sqlTokens.remove(i + 1);
            }
        }
    }


    /**
     * Tokenizes the given sql statment.
     * 
     * @param sql SQL statement.
     * @return List of String tokens.
     */
    protected List tokenize(String sql)
    {
        List tokens = new ArrayList();
        
        // Tokenize into words using whitespace as the delimiter.
        for (StringTokenizer st = new StringTokenizer(sql); st.hasMoreTokens();)
        {
            String token = st.nextToken();

            // Tokenize into words, parens, and commas 
            for (StringTokenizer st1 = new StringTokenizer(token, "(),", true);
                st1.hasMoreTokens();
                tokens.add(st1.nextToken()));
        }
        
        return tokens;
    }


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

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = 
            XOMUtil.getFirstChildElement(
                prefs, 
                NODE_SQLFORMATTER,
                new Element(NODE_SQLFORMATTER));
     
        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_SQLFORMATTER);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // Debug
    //--------------------------------------------------------------------------
    
    /**
     * Dumps the flags as debug output.
     * 
     * @param keywords SQL keywords.
     * @param escapeList List of escape sequences.
     * @param newlineFlags Newline flags.
     * @param indentFlags Indentation flags.
     */
    protected void dumpFlags(
        String[] keywords, 
        List escapeList,
        int[] newlineFlags, 
        int[] indentFlags)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Tokens:\n");
        
        for (int i = 1; i < keywords.length - 1; i++)
            sb.append(
                indentFlags[i] 
                + " [" 
                + keywords[i] 
                + "] " 
                + newlineFlags[i] 
                + "\n");

        sb.append("Escapes:\n");
        
        for (int j = 0, n = escapeList.size(); j < n; j++)
            sb.append(escapeList.get(j) + "\n");

        logger_.debug(sb.toString());
    }
}