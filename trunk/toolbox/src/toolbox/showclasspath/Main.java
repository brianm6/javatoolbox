package toolbox.showclasspath;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Shows the current classpath along with detailed info as reported by 
 * System property <code>java.class.path</code>
 */
public class Main
{

    /** max length for size column **/
    private static final int MAX_SIZE_LEN = 12;
    
    /** max length for date column **/
    private static final int MAX_DATE_LEN = 14;
    
    /** max length for time column **/
    private static final int MAX_TIME_LEN = 8;

    /** column heading for archive **/
    private static final String COL_ARCHIVE = "JAR/Directory";
    
    /** column heading for date **/
    private static final String COL_DATE = "Date";
    
    /** column heading for size **/
    private static final String COL_SIZE = "Size";
    
    /** column heading for time **/    
    private static final String COL_TIME = "Time";


    /**
     * Formats date to specific format. e.g.  01/01/1980  12/31/1999
     * 
     * @param   d   Date to format
     * @return      Formatted date
     */
    static String formatDate(Date d)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        return dateFormat.format(d);
    }


    /**
     * Formats the file size length to include commas. e.g.  1,233,276
     * 
     * @param   l   file length
     * @return      formatted length
     */
    static String formatLength(long l)
    {
        StringBuffer sbuf = new StringBuffer(l + "");

        for (int i = sbuf.length() - 3; i > 0; i -= 3)
            sbuf.insert(i, ",");

        return new String(sbuf);
    }


    /**
     * Formats time to specific format. e.g.  12:47a  01:07p
     * 
     * @param   t   Date to format
     * @return      Formatted time
     */
    static String formatTime(Date t)
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");
        SimpleDateFormat timeFormat2 = new SimpleDateFormat("hh:mm");
        String timeString = timeFormat.format(t);

        if (timeString.endsWith("AM"))
            timeString = timeFormat2.format(t) + "a";
        else
            timeString = timeFormat2.format(t) + "p";

        return timeString;
    }


    /**
     * Determines if a file is a java archive
     * 
     * @param   s   absolute name of file
     * @return  true if file is a java archive, false otherwise
     */
    static boolean isArchive(String s)
    {
        s = s.toUpperCase();

        if (s.endsWith(".JAR") || s.endsWith(".ZIP"))
            return true;
        else
            return false;
    }


    /**
     * Entry point
     * 
     * @param   args    command line params
     */
    public static void main(String[] args)
    {

        // stuffed everything info main()
        String classPath = System.getProperty("java.class.path");
        
        StringTokenizer st = 
            new StringTokenizer(classPath,System.getProperty("path.separator"));
            
        int max = 0;

        // find longest for formatting
        while (st.hasMoreElements())
        {
            String path = st.nextToken();

            if (path.length() > max)
                max = path.length();
        }

        max++;
        st = new StringTokenizer(classPath, 
                                 System.getProperty("path.separator"));

        // header row
        if (st.countTokens() > 0)
        {
            int rowLength = max + MAX_SIZE_LEN + 
                            MAX_DATE_LEN + MAX_TIME_LEN;
            System.out.println(repeatString(rowLength, "="));
            System.out.print(COL_ARCHIVE);
            System.out.print(repeatSpace(max - COL_ARCHIVE.length()));
            System.out.print(repeatSpace(MAX_SIZE_LEN - COL_SIZE.length()));
            System.out.print(COL_SIZE);
            System.out.print(repeatSpace(MAX_DATE_LEN - COL_DATE.length()));
            System.out.print(COL_DATE);
            System.out.print(repeatSpace(MAX_TIME_LEN - COL_TIME.length()));
            System.out.print(COL_TIME);
            System.out.println();
            System.out.println(repeatString(rowLength, "="));
        }

        // loop through classpath
        while (st.hasMoreElements())
        {
            String path = st.nextToken();
            System.out.print(path);

            for (int i = 0; i < (max - path.length()); i++)
                System.out.print(" ");

            File f = new File(path);

            // if archive, get more info
            if (isArchive(path))
            {
                if (f.exists() && f.isFile() && f.canRead())
                {
                    Date lastModified = new Date(f.lastModified());
                    String date = formatDate(lastModified);
                    String time = formatTime(lastModified);
                    String length = formatLength(f.length());
                    
                    System.out.print(
                        repeatSpace(MAX_SIZE_LEN - length.length()));
                        
                    System.out.print(length);
                    System.out.print(repeatSpace(MAX_DATE_LEN - date.length()));
                    System.out.print(date);
                    System.out.print(repeatSpace(MAX_TIME_LEN - time.length()));
                    System.out.print(time);
                }
                else
                {
                    if (!f.exists())
                        System.out.print("Does not exist!");
                    else if (!f.isFile())
                        System.out.print("Is not a file!");
                    else if (!f.canRead())
                        System.out.print("Cannot open file!");
                    else
                        System.out.print("Unknown error opening file!");
                }
            }
            else if (f.isDirectory())
            {
                Date lastModified = new Date(f.lastModified());
                String date = formatDate(lastModified);
                String time = formatTime(lastModified);
                System.out.print(repeatSpace(MAX_SIZE_LEN));
                System.out.print(repeatSpace(MAX_DATE_LEN - date.length()));
                System.out.print(date);
                System.out.print(repeatSpace(MAX_TIME_LEN - time.length()));
                System.out.print(time);
            }
            else if (!f.exists())
            {
                System.out.print("Does not exist!");
            }

            System.out.println();
        }
    }


    /**
     * Builds a string with a given number of spaces
     * 
     * @param   l   number of spaces
     * @return      string containing given number of spaces
     */
    static String repeatSpace(int l)
    {
        return repeatString(l, " ");
    }


    /**
     * Builds a string with a given string repeated a given number of times
     * 
     * @param   l   number of times to repeat a given character
     * @param   s   string to repeat
     * @return      string with given character repeated given number of times
     */
    static String repeatString(int l, String s)
    {
        StringBuffer sbuf = new StringBuffer("");

        for (int i = 0; i < l; i++)
            sbuf.append(s);

        return new String(sbuf);
    }
}