package toolbox.util;

import java.util.Iterator;
import java.util.List;

/**
 * Utility methods for manipulating strings.
 */
public final class StringUtil
{
    /**
     *  Prevent construction
     */
    private StringUtil()
    {
        super();
    }

    /**
     * Left justify a string representing an integer with a given width.
     * The num is converted to a string which is padded with space characters 
     * on the right to the given width. If the string representing the integer 
     * is greater than width then the string is returned.
     *
     * @param      num     Number to format
     * @param      width   Width of string
     * @return     String left justified
     */
    public static String left(int num, int width)
    {
        return left(Integer.toString(num), width);
    }

    /**
     * justify string left within given width.
     * <P>the string is padded with space characters on the right to given width
     * strings longer than width are returned unaltered
     *
     * @param  str   String to left
     * @param  width Max width
     * @return a string of length width containing the given string on the left
     */
    public static String left(String str, int width)
    {
        return left(str, width, ' ');
    }

    /**
     * Left justify a string to a given width using a pad character.
     * Strings longer than the width are returned unaltered.
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
     * Left justify a string to a given width using a pad character.
     * Strings longer than the width are returned unaltered if 'trunc'
     * is false; else they are truncated
     *
     * @param   str      String to justify
     * @param   width    Width of resulting screen
     * @param   padChar  Character to use for padding
     * @param   doTruncate  if true, truncate; if false, leave unaltered
     * @return  String of length width containing the given string on the left
     */
    public static String left(String str, final int width, final char padChar,
        final boolean doTruncate)
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
     * justify right a string representing the integer within given width.
     * <P>the num is converted to a string
     * which is padded with space characters on the left to given width
     * if the string representing the integer is greater than width then 
     * this string is returned
     *
     * @param  num    Number to right
     * @param  width  Max width
     * @return a string representation of an int of length width right justified
     */
    public static String right(int num, int width)
    {
        return right(Integer.toString(num), width);
    }

    /**
     * Right justifies a string to the given width using spaces.
     * String longer than the width are returned as is.
     *
     * @param    str      String to right justify
     * @param    width    Width of justified string
     * @return   Right justified string
     */
    public static String right(String str, int width)
    {
        return right(str, width, ' ');
    }

    /**
     * Right justifies a string to the given width and fill character.
     * String longer than the width are returned as is.
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
     * Right justify a string to a given width using a pad character.
     * Strings longer than the width are returned unaltered if 'trunc'
     * is false; else they are truncated
     *
     * @param   str         String to justify
     * @param   width       Width of resulting screen
     * @param   padChar     Character to use for padding
     * @param   doTruncate  if true, truncate; if false, leave unaltered
     * @return  String of length width containing the given string on the left
     */
    public static String right(String str, final int width, final char padChar,
        final boolean doTruncate)
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
     * @param  theList  The list to convert to a string
     * @return List converted to a string
     */
    public static String toString( List theList )
    {
        StringBuffer buf = new StringBuffer( 20 );
        Iterator iter = theList.iterator();
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
     * @param  s The string to check
     * @return   True if the string is null or empty, false otherwise
     */
    public static final boolean isNullOrEmpty(String s) 
    {
        if (s == null || s.length() == 0)
            return true;
        else
            return false;
    }
    
    /**
     * Generates a ruler underneath a string for character counting purposes
     * 
     * @param   s    String
     * @return  String
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
     * Truncate a string to the given length.
     * If s.length() <= n, returns s.
     * Else, returns the first n characters of s.
     * 
     * @param  s   String to truncate
     * @param  n   Length to truncate to
     * @return String
     */
    public static String truncate(String s, int n)
    {
        if (s.length() <= n)
            return s;
        else
            return s.substring(0, n);    
    }    
}