package toolbox.util.db;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;

/**
 * SQLFormatter is a pretty printer for SQL statements.
 */
public class SQLFormatter
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    private static final Logger logger_ = Logger.getLogger(SQLFormatter.class);
    
    private static final String MAJOR_WORDS = 
        "|SELECT|FROM|WHERE|ORDER BY|GROUP BY|HAVING|UPDATE|SET|INSERT|INTO" +
        "|VALUES|DELETE|UNION|ALL|MINUS|";
                                              
    
    private static final String MINOR_WORDS = 
        "|COUNT|SUM|AVG|MIN|MAX|DISTINCT|AS|ANY|AND|OR|XOR|NOT|LIKE|IN|EXISTS" +
        "|IS|NULL|";
    
    private static final String FUNCTION_WORDS = "|COUNT|SUM|AVG|MIN|MAX|";
    private static final String SUB_SELECT = "|SELECT|";
    private static final String ESCAPE_TOKEN = "\001";
    private static final String DELIMITERS = "(),";
    
    private static final int MAX_INDENTS = 16;
    private static final int NOTHING = 0;
    private static final int SPACE = 1;
    private static final int NEW_LINE = 2;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private String newLine_;
    private String indent_;
    private boolean capMajor_;
    private boolean capMinor_;
    private boolean capNames_;
    private boolean newLineAnd_;
    private boolean debug_;

    private String escapes_[][] = 
    {
        {"'", "'", "" }, 
        {"\"", "\"", ""}, 
        {"/*", "*/", "1"}, 
        {"--", "\r\n", "2"}
    };
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a SQLFormatter.
     */
    public SQLFormatter()
    {
        newLine_     = System.getProperty("line.separator");
        indent_      = "    ";
        capMajor_    = false;
        capMinor_    = false;
        capNames_    = false;
        newLineAnd_  = true;
        debug_       = false;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Formats a SQL statement.
     * 
     * @param sql SQL statement to format.
     * @return Formatted SQL statement.
     */
    public String format(String sql)
    {
        List vector = new ArrayList();
        
        for (int i = 0; i < sql.length(); i++)
        {
            for (int j = 0; j < escapes_.length; j++)
            {
                String s1 = escapes_[j][0];
                String s3 = escapes_[j][1];
                String s5 = escapes_[j][2];
                
                if (!sql.regionMatches(i, s1, 0, s1.length()))
                    continue;
                
                int j1 = i + s1.length();
                int k1 = sql.indexOf(s3, j1);
                
                if (k1 == -1)
                {
                    k1 = sql.indexOf("\n", j1);
                    
                    if (k1 == -1)
                    {
                        k1 = sql.indexOf("\r", j1);
                        
                        if (k1 == -1)
                            k1 = sql.length() - 1;
                    }
                    
                    k1++;
                } 
                else
                {
                    k1 += s3.length();
                }
                
                String s6 = sql.substring(i, k1);
                
                if (s5.equals("2"))
                    s6 = "/*" + s6.trim().substring(2) + " */";
                
                vector.add(s6);
                String s7 = sql.substring(0, i);
                String s8;
                
                if (k1 < sql.length())
                    s8 = sql.substring(k1);
                else
                    s8 = "";
                
                String s9 = "\001";
                
                if (!s5.equals(""))
                {
                    if (!s7.endsWith(" "))
                        s9 = " " + s9;
                    if (!s8.startsWith(" "))
                        s9 = s9 + " ";
                }
                
                sql = s7 + s9 + s8;
                break;
            }

        }

        List vector1 = new ArrayList();
        
        for (StringTokenizer st = new StringTokenizer(sql);
            st.hasMoreTokens();)
        {
            String s2 = st.nextToken();

            for (StringTokenizer st1 = new StringTokenizer(s2, "(),", true);
                st1.hasMoreTokens();
                vector1.add(st1.nextToken()));
        }

        for (int k = 0; k < vector1.size() - 1; k++)
        {
            String s4 =
                (String) vector1.get(k) + " " + (String) vector1.get(k + 1);

            if (isMajor(s4))
            {
                vector1.set(k, s4);
                vector1.remove(k + 1);
            }
        }

        int l = vector1.size();
        String as[] = new String[l += 2];
        as[0] = "";
        as[l - 1] = "";
        
        for (int i1 = 0; i1 < vector1.size(); i1++)
            as[i1 + 1] = (String) vector1.get(i1);

        int ai[] = new int[l];
        int ai1[] = new int[l];
        
        for (int l1 = 0; l1 < l; l1++)
        {
            boolean flag = false;
            
            if (isMajor(as[l1]))
                flag = capMajor_;
            
            if (isMinor(as[l1]))
                flag = capMinor_;
            
            if (isName(as[l1]))
                flag = capNames_;
            
            if (flag)
                as[l1] = as[l1].toUpperCase();
        }

        for (int i2 = 1; i2 < l - 1; i2++)
        {
            ai[i2] = 1;
            if (isMajor(as[i2]))
            {
                ai[i2 - 1] = 2;
                ai[i2] = 2;
            }
            else if (as[i2].equals(","))
            {
                ai[i2] = 2;
                ai[i2 - 1] = 0;
            }
            else if (as[i2].equals("("))
            {
                ai[i2] = 0;
                if (isFunction(as[i2 - 1]) || isName(as[i2 - 1]))
                    ai[i2 - 1] = 0;
            }
            else if (as[i2].equals(")"))
            {    
                ai[i2 - 1] = 0;
            }
            else if (as[i2].equalsIgnoreCase("AND"))
            {    
                if (newLineAnd_)
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
            if (as[k2].equals(")"))
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
            
            if (isMajor(as[k2]))
                ai1[k2] = j2 * 2;
            else
                ai1[k2] = j2 * 2 + 1;
            
            if (as[k2].equals("("))
            {    
                if (isSubSelect(as[k2 + 1]))
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
                sb.append(StringUtil.repeat(indent_, ai1[l2]));
            
            sb.append(as[l2] + as1[ai[l2]]);
        }

        sql = sb.toString();
        
        for (int i3 = 0; i3 < vector.size(); i3++)
        {
            int j3 = sql.indexOf("\001");
            
            sql = sql.substring(0, j3)
                  + (String) vector.get(i3)
                  + sql.substring(j3 + 1);
        }

        if (debug_)
        {
            StringBuffer sb1 = new StringBuffer();
            sb1.append("Tokens:\n");
            
            for (int k3 = 1; k3 < l - 1; k3++)
                sb1.append(ai1[k3] + " [" + as[k3] + "] " + ai[k3] + "\n");

            sb1.append("Escapes:\n");
            
            for (int l3 = 0; l3 < vector.size(); l3++)
                sb1.append((String) vector.get(l3) + "\n");

            logger_.debug(sb1.toString());
        }
        
        return sql;
    }

    
    /**
     * Sets flag to turn on new lines before an AND.
     * 
     * @param flag True to embed a newline, false otherwise.
     */
    public void setNewLineBeforeAnd(boolean flag)
    {
        newLineAnd_ = flag;
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
     * Used to turn on capitalization of major SQL keywords.
     * 
     * @param flag True to capitalize.
     */
    public void setCapitalizeMajor(boolean flag)
    {
        capMajor_ = flag;
    }

    
    /**
     * Used to turn on capitalization of SQL names.
     * 
     * @param flag True to capitalize.
     */
    public void setCapitalizeNames(boolean flag)
    {
        capNames_ = flag;
    }

    
    /**
     * Used to set the number of spaces for indentation.
     * 
     * @param i Number of spaces to use for indentation.
     */
    public void setIndent(int i)
    {
        if (i < 0)
            indent_ = "\t";
        else
            indent_ = StringUtil.repeat(" ", i);
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
    // Private
    //--------------------------------------------------------------------------
    
    private static boolean isName(String s)
    {
        return !isIn(s, MAJOR_WORDS) && !isIn(s, MINOR_WORDS);
    }

    
    private static boolean isFunction(String s)
    {
        return isIn(s, FUNCTION_WORDS);
    }
    
    
    private static boolean isMinor(String s)
    {
        return isIn(s, MINOR_WORDS);
    }

    
    private static boolean isIn(String s, String s1)
    {
        return s1.indexOf("|" + s.toUpperCase() + "|") > -1;
    }

    
    private static boolean isSubSelect(String s)
    {
        return isIn(s, SUB_SELECT);
    }

    
    public void setCapitalizeMinor(boolean flag)
    {
        capMinor_ = flag;
    }

    
    private static boolean isMajor(String s)
    {
        return isIn(s, MAJOR_WORDS);
    }
}