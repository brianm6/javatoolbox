package toolbox.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import toolbox.util.io.WrappingWriter;

/**
 * String utility methods
 */
public final class StringUtil
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(StringUtil.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Static class...prevent construction
     */
    private StringUtil()
    { 
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Left justifies a string representing an integer with a given width.
     * The num is converted to a string which is padded with space characters 
     * on the right to the given width. If the string representing the integer 
     * is greater than width then the string is returned. A space is used by
     * default as the padding character.
     *
     * @param    num     Number to format
     * @param    width   Width of string
     * @return   Left justified string
     */
    public static String left(int num, int width)
    {
        return left(Integer.toString(num), width);
    }

    /**
     * Left justifies a string within the given width. The string is padded with
     * space characters on the right to given width. Strings longer than the 
     * width are returned unaltered.
     *
     * @param   str    String to left
     * @param   width  Max width
     * @return  Left justified string
     */
    public static String left(String str, int width)
    {
        return left(str, width, ' ');
    }

    /**
     * Left justifies a string to a given width using a pad character. Strings 
     * longer than the width are returned unaltered.
     *
     * @param   str      String to justify
     * @param   width    Width of resulting screen
     * @param   padChar  Character to use for padding
     * @return  String of length width containing the given string on the left
     */
    public static String left(String str, int width, char padChar)
    {
        return left( str, width, padChar, false );
    }
    
    /**
     * Left justifies a string to a given width using a pad character. Strings 
     * longer than the width are returned unaltered if 'trunc' is false; else 
     * they are truncated.
     *
     * @param   str         String to justify
     * @param   width       Width of resulting screen
     * @param   padChar     Character to use for padding
     * @param   doTruncate  If true, truncate; if false, leave unaltered
     * @return  String of length width containing the given string on the left
     */
    public static String left(String str, int width, char padChar, 
        boolean doTruncate)
    {
        final int strLen = str.length();
        String justStr = str;

        if( strLen > width )
        {
            if( doTruncate )
            {
                justStr = str.substring( 0, width );
            }
        }
        else if( strLen < width )
        {
            StringBuffer bf = new StringBuffer( width );
            bf.append( str );
            for( int idx = strLen; idx < width; ++idx )
            {
                bf.append( padChar );
            }
            justStr = bf.toString();
        }

        return justStr;
    }
    
    /**
     * Right justifies a string representing an integer within the given width.
     * The num is converted to a string which is padded with space characters
     * on the left to the given width. If the string representing the integer 
     * is greater than width then the unaltered string is returned.
     *
     * @param   num    Number to right justify
     * @param   width  Max width
     * @return  Right justified string
     */
    public static String right(int num, int width)
    {
        return right(Integer.toString(num), width);
    }


    /**
     * Right justifies a string to the given width using spaces. If the string 
     * is longer than the width then the string is returned unaltered.
     *
     * @param   str     String to right justify
     * @param   width   Width of justified string
     * @return  Right justified string
     */
    public static String right(String str, int width)
    {
        return right(str, width, ' ');
    }

    /**
     * Right justifies a string to the given width and pad character. If the 
     * string is longer than the width, the string is returned unalteded.
     *
     * @param    str      String to right justify
     * @param    width    Width of justified string
     * @param    padChar  Pad character
     * @return   Right justified string
     */
    public static String right(String str, int width, char padChar )
    {
        return right( str, width, padChar, false );
    }
   
    /**
     * Right justifies a string to a given width using a pad character. If the
     * string is longer than the width and doTruncate is false, then the 
     * string is returned unaltered otherwise the string is truncated to the
     * width specified.
     *
     * @param   str         String to justify
     * @param   width       Width of resulting screen
     * @param   padChar     Character to use for padding
     * @param   doTruncate  If true, truncate; if false, leave unaltered
     * @return  String of length width containing the given string on the left
     */
    public static String right(String str, int width, char padChar,
        boolean doTruncate)
    {
        int strLen = str.length();
        String justStr = str;

        if( strLen > width )
        {
            if( doTruncate )
            {
                justStr = str.substring( 0, width );
            }
        }
        else if( strLen < width )
        {
            StringBuffer bf = new StringBuffer( width );
            for( int idx = strLen; idx < width; ++idx )
            {
                bf.append( padChar );
            }
            bf.append( str );
            justStr = bf.toString();
        }

        return justStr;
    }

    /**
     * Return the given list as a debug string
     * 
     * @param   list   List to convert to a string
     * @return  List converted to a string
     */
    public static String toString(List list)
    {
        StringBuffer buf = new StringBuffer( 20 );
        Iterator iter = list.iterator();
        int index = 0;
        
        while( iter.hasNext() )
        {
            buf.append( " [" ).append( index++ )
               .append( "] = " ).append( iter.next() );
        }

        return buf.toString();
    }
    
    /**
     * Convenience method to check if a string is null or of zero length
     *
     * @param   s   String to check
     * @return  True if the string is null or empty, false otherwise
     */
    public static final boolean isNullOrEmpty(String s) 
    {
        return (s == null || s.length() == 0);
    }
    
    /**
     * Convenience method to check if a string is null, empty, or blank
     * (contains only spaces)
     * 
     * @param   s   String to check
     * @return  True if not null, empty, or blank; false otherwise
     */
    public static final boolean isNullEmptyOrBlank(String s)
    {
        return (isNullOrEmpty(s) || s.trim().length() == 0);
    }
    
    /**
     * Generates a numbered ruler underneath a string for character counting 
     * purposes. For example:
     * <pre>
     * 
     * Input : getStringRuler("abcdef") 
     * Output: abcdef
     *         123456
     * 
     * Input : getStringRuler("this is long") 
     * Output: this is long
     *         123456789012
     *                  111 <- read upwards
     * </pre> 
     * 
     * @param   s    String to generate a ruler for
     * @return  String containing the original string with the ruler appended
     */
    public static final String getStringRuler(String s)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(s);
        sb.append("\n");
        
        int len      = s.length();
        int maxLines = (len+"").length();
        
        for(int i=0; i<maxLines; i++)
        {
            for(int j=1; j<=len; j++)
            {
                String num = left(j+"", maxLines);
                sb.append(num.charAt(i));
            }
            
            if (i != maxLines-1)
                sb.append("\n");
        }

        return sb.toString();
    }
    
    /**
     * Truncates a string to the given length. If s.length() <= n, returns s.
     * Else, returns the first n characters of s.
     * 
     * @param   s   String to truncate
     * @param   n   Length to truncate to
     * @return  Truncated string
     */
    public static String truncate(String s, int n)
    {
        if (s.length() <= n)
            return s;
        else
            return s.substring(0, n);    
    }  
    
    /**
     * Repeats a string a specified number of times
     * 
     * @param   s         String to repeat
     * @param   numTimes  Number of times to repeat the string
     * @return  String containing numTimes concatenated instances of s
     */
    public static final String repeat(String s, int numTimes)
    {
        StringBuffer sb = new StringBuffer();
        
        for(int i=0; i<numTimes; i++)
            sb.append(s);
            
        return sb.toString();
    }
    
    /**
     * Wraps a string to a default width of 80
     * 
     * @param   s  String to wrap
     * @return  Wrapped string
     */    
    public static String wrap(String s)
    {
        return wrap(s, 80);
    }
    
    /** 
     * Wraps a string to a default width of 80. The beginnning of line and
     * end of line are decorated with brackets to create a box effect if
     * the border flag is set.
     * 
     * <pre>
     * [some text here]
     * [more text here]
     * </pre>
     * 
     * @param   s        String to wrap
     * @param   border   True to enclose wrapped text in brackets
     * @return  Wrapped string with box decoration
     */
    public static String wrap(String s, boolean border)
    {
        return wrap(s, 80, border);
    }
    
    /**
     * Wraps a string to a given width. The beginning of line and end of line
     * are decorated with brackets to create a box effect if the border flag
     * is set.
     * 
     * @param   s       String to wrap
     * @param   width   Width to wrap the string
     * @param   border  Should the wrapped text be decorated with a border?
     * @return  Wrapped string
     */
    public static String wrap(String s, int width, boolean border)
    {
        if (border)
            return wrap(s, width, "[", "]");    
        else
            return wrap(s, width, "", "");
    }
    
    /**
     * Wraps a string to a given width
     * 
     * @param   s      String to wrap
     * @param   width  Width to wrap the string
     * @return  Wrapped string
     */
    public static String wrap(String s, int width)
    {
        return wrap(s, width, "", "");    
    }
    
    /**
     * Wraps a string to the specified criteria
     * 
     * @param   s       String to wrap
     * @param   width   Width to wrap the string
     * @param   prefix  Prefix before each line
     * @param   suffix  Suffix after each line
     * @return  Wrapped string
     */
    public static String wrap(String s, int width, String prefix, 
        String suffix)
    {
        String wrapped = null;
        
        try
        {
            StringWriter sw = new StringWriter();
            WrappingWriter w = new WrappingWriter(sw, width, prefix, suffix);
            w.write(s);
            w.close();
            wrapped = sw.toString();
        }
        catch (IOException e)
        {
            logger_.error(e);
        }
        
        return wrapped;
    }
    
    /**
     * Replace all occurences of a string within another string
     *
     * @param   text  Text to search and replace in
     * @param   repl  String to search for
     * @param   with  String to replace with
     * @return  String with replacements
     */
    public static String replace(String text, String repl, String with)
    {
        return replace(text, repl, with, -1);
    }

    /**
     * Replace a string with another string inside a larger string, for the 
     * first max values of the search string.
     *
     * @param   text  Text to search and replace in
     * @param   repl  String to search for
     * @param   with  String to replace with
     * @param   max   Maximum number of values to replace, or -1 if no maximum
     * @return  String with replacements
     */
    public static String replace(String text, String repl, String with,
        int max)
    {
        StringBuffer buf = new StringBuffer(text.length());
        
        int start = 0, end = 0;
        
        while ((end = text.indexOf(repl, start)) != -1)
        {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0)
                break;
        }
        
        buf.append(text.substring(start));
        return buf.toString();
    }
    
    /**
     * Returns arary of individual tokens from a string
     * 
     * @param   s          String to tokenize
     * @param   delimiter  Delimiter used for separate tokens
     * @return  Array of string tokens
     */  
    public static String[] tokenize(String s, String delimiter)
    {
        return tokenize(s, delimiter, false);
    }

    /**
     * Returns arary of individual tokens from a string
     * 
     * @param   s          String to tokenize
     * @param   delimiter  Delimiter used for separate tokens
     * @return  Array of string tokens
     */  
    public static String[] tokenize(String s, String delimiter, boolean saveDelims)
    {
        StringTokenizer st = new StringTokenizer(s, delimiter, saveDelims);
        String[] tokens = new String[st.countTokens()];   
        for(int i=0; st.hasMoreTokens(); tokens[i++] = st.nextToken());
        return tokens;
    }

    /**
     * Trims leading and trailing characters from a string
     * 
     * @param   s    String to trim
     * @param   ch   Character to trim from string
     * @return  Trimmed string
     */     
    public static String trim(String s, char ch)
    {
        int len = s.length();
        
        // empty string
        if (len == 0)
            return s;
        
        // nothing to trim
        if (!s.startsWith(ch+"") && !s.endsWith(ch+""))
            return s;

        // trim on both sides        
        int startPos = 0;
        int endPos   = len - 1;
        
        while (startPos < len && s.charAt(startPos) == ch)
            startPos++;                
        
        while (endPos >= startPos && s.charAt(endPos) == ch)
            endPos--;
        
        return s.substring(startPos, endPos+1);
    }
    
    /**
     * Determines if a string is multiline (contains one or more carraige
     * returns).
     * 
     * @param   s  String to inspect
     * @return  True if a multiline string, false otherwise.
     */
    public static boolean isMultiline(String s)
    {
        return s.indexOf("\n") >= 0;
    }
}