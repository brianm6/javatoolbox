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
    // TODO: Change capitalize options to uppercase/lowercase/preservecase
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
     * SQL escape token.
     */
    private static final String ESCAPE_TOKEN = "\001";
    
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
     * Flag to capitalize major sql keywords.
     */
    private boolean capitalizeMajor_;
    
    /**
     * Flag to capitalize minor sql keywords.
     */
    private boolean capitalizeMinor_;
    
    /**
     * Flag to capitalize sql names.
     */
    private boolean capitalizeNames_;
    
    /**
     * Flag to insert a newline before a SQL "and" keyword.
     */
    private boolean newLineBeforeAnd_;
    
    /**
     * Flag to send debug output to the logger.
     */
    private boolean debug_;

    /**
     * Escape characters.
     */
    private String escapes_[][] = 
    {
        {"'", "'", "" }, 
        {"\"", "\"", ""}, 
        {"/*", "*/", "1"}, 
        {"--", "\n" /*"\r\n"*/, "2"}
    };
    
    // Indexes into escapes_
    private static final int INDEX_ESCAPE_OPEN  = 0;
    private static final int INDEX_ESCAPE_CLOSE = 1;
    private static final int INDEX_ESCAPE_MISC  = 2;
    
    private static final String ESCAPE_TYPE_SINGLE_QUOTE = "";
    private static final String ESCAPE_TYPE_DOUBLE_QUOTE = "";
    private static final String ESCAPE_TYPE_SINGLE_LINE_COMMENT = "2";
    private static final String ESCAPE_TYPE_COMMENT = "1";
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a SQLFormatter with the following defaults.
     * <ul>
     *  <li>Newline is \n
     *  <li>Indent is 4 spaces
     *  <li>Major and minor sql keywords are not capitalized
     *  <li>SQL names are not capitalized
     *  <li>New lines are inserted before an AND
     *  <li>Debug is set to false
     * </ul> 
     */
    public SQLFormatter()
    {
        setNewLine("\n");
        setIndent(4);
        setCapitalizeMajor(false);
        setCapitalizeMinor(false);
        setCapitalizeNames(false);
        setNewLineBeforeAnd(true);
        setDebug(false);
    }

    
    //--------------------------------------------------------------------------
    // Accessors
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if capitalization of major sql keywords is activated, false
     * otherwise.
     * 
     * @return boolean
     */
    public boolean isCapitalizeMajor()
    {
        return capitalizeMajor_;
    }
    
    
    /**
     * Returns true if capitalization of minor sql keywords is activated, false
     * otherwise.
     * 
     * @return boolean
     */
    public boolean isCapitalizeMinor()
    {
        return capitalizeMinor_;
    }
    
    
    /**
     * Returns true if capitalization of sql names is activated, false 
     * otherwise.
     * 
     * @return boolean
     */
    public boolean isCapitalizeNames()
    {
        return capitalizeNames_;
    }
    
    
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
     * Sets the capitalization of major SQL keywords.
     * 
     * @param flag True to capitalize, false otherwise.
     */
    public void setCapitalizeMajor(boolean flag)
    {
        capitalizeMajor_ = flag;
    }
    
    
    /**
     * Sets the capitalization of minor sql keywords.
     * 
     * @param flag True to capitalize, false otherwise.
     */
    public void setCapitalizeMinor(boolean flag)
    {
        capitalizeMinor_ = flag;
    }

    
    /**
     * Sets the capitalization of SQL names.
     * 
     * @param flag True to capitalize, false otherwise.
     */
    public void setCapitalizeNames(boolean flag)
    {
        capitalizeNames_ = flag;
    }
    
    
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
        for (int i = 0; i < escapes_.length; i++)
        {
            for (int j = 0; j < escapes_[0].length; j++)
                if (escapes_[i][j].equals(newLine_))
                    escapes_[i][j] = s;
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
            for (int j = 0, m = escapes_.length; j < m; j++)
            {
                String escapeOpen  = escapes_[j][INDEX_ESCAPE_OPEN];
                String escapeClose = escapes_[j][INDEX_ESCAPE_CLOSE];
                String escapeType  = escapes_[j][INDEX_ESCAPE_MISC];
                
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
                if (escapeType.equals(ESCAPE_TYPE_SINGLE_LINE_COMMENT))
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

        List list2 = new ArrayList();
        
        for (StringTokenizer st = new StringTokenizer(sql);
            st.hasMoreTokens();)
        {
            String sqlToken = st.nextToken();

            for (StringTokenizer st1 = new StringTokenizer(sqlToken, "(),", true);
                st1.hasMoreTokens();
                list2.add(st1.nextToken()));
        }

        for (int k = 0; k < list2.size() - 1; k++)
        {
            String s4 =
                (String) list2.get(k) + " " + (String) list2.get(k + 1);

            if (isMajor(s4))
            {
                list2.set(k, s4);
                list2.remove(k + 1);
            }
        }

        int l = list2.size();
        String keywords[] = new String[l += 2];
        keywords[0] = "";
        keywords[l - 1] = "";
        
        for (int i1 = 0; i1 < list2.size(); i1++)
            keywords[i1 + 1] = (String) list2.get(i1);

        int ai[] = new int[l];
        int ai1[] = new int[l];
        
        for (int keywordIndex = 0; keywordIndex < l; keywordIndex++)
        {
            boolean capitalize = false;
            
            if (isMajor(keywords[keywordIndex]))
                capitalize = capitalizeMajor_;
            
            if (isMinor(keywords[keywordIndex]))
                capitalize = capitalizeMinor_;
            
            if (isName(keywords[keywordIndex]))
                capitalize = capitalizeNames_;
            
            if (capitalize)
                keywords[keywordIndex] = keywords[keywordIndex].toUpperCase();
        }

        for (int i2 = 1; i2 < l - 1; i2++)
        {
            ai[i2] = 1;
            if (isMajor(keywords[i2]))
            {
                ai[i2 - 1] = 2;
                ai[i2] = 2;
            }
            else if (keywords[i2].equals(","))
            {
                ai[i2] = 2;
                ai[i2 - 1] = 0;
            }
            else if (keywords[i2].equals("("))
            {
                ai[i2] = 0;
                if (isFunction(keywords[i2 - 1]) || isName(keywords[i2 - 1]))
                    ai[i2 - 1] = 0;
            }
            else if (keywords[i2].equals(")"))
            {    
                ai[i2 - 1] = 0;
            }
            else if (keywords[i2].equalsIgnoreCase("AND"))
            {    
                if (newLineBeforeAnd_)
                    ai[i2 - 1] = 2;
                else
                    ai[i2] = 2;
            }
        }

        ai[l - 2] = 2;
        int j2 = 0;
        int ai2[] = new int[16];
        
        for (int k2 = 0; k2 < l; k2++)
        {
            if (keywords[k2].equals(")"))
            {    
                if (ai2[j2] == 0)
                {
                    j2--;
                    
                    if (k2 > 0)
                        ai[k2 - 1] = 2;
                }
                else
                {
                    ai2[j2]--;
                }
            }
            
            if (isMajor(keywords[k2]))
                ai1[k2] = j2 * 2;
            else
                ai1[k2] = j2 * 2 + 1;
            
            if (keywords[k2].equals("("))
            {    
                if (isSubSelect(keywords[k2 + 1]))
                {
                    if (j2 < 16)
                        j2++;
                    
                    ai2[j2] = 0;
                }
                else
                {
                    ai2[j2]++;
                }
            }
        }

        String as1[] = new String[3];
        as1[0] = "";
        as1[1] = " ";
        as1[2] = newLine_;
        
        StringBuffer sb = new StringBuffer();
        
        for (int l2 = 1; l2 < l - 1; l2++)
        {
            if (ai[l2 - 1] == 2)
                sb.append(StringUtils.repeat(indent_, ai1[l2]));
            
            sb.append(keywords[l2] + as1[ai[l2]]);
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
            
            for (int k3 = 1; k3 < l - 1; k3++)
                sb1.append(ai1[k3] + " [" + keywords[k3] + "] " + ai[k3] + "\n");

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
     * @param s Token to search for.
     * @param s1 String with embedded tokens.
     * @return boolean
     */
    protected static boolean isIn(String s, String s1)
    {
        return s1.indexOf("|" + s.toUpperCase() + "|") > -1;
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
}