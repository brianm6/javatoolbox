package toolbox.showclasspath;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.StringTokenizer;

import toolbox.util.DateUtil;
import toolbox.util.StringUtil;
import toolbox.util.TimeUtil;

/**
 * Shows the current classpath along with detailed info as reported by 
 * System property <code>java.class.path</code>. Also, detects redundant
 * and invalid classpath entries such as jars/dirs that don't exist. 
 * 
 * <p>
 * Example:
 * <pre>
 * 
 * ========================================================================
 * JAR/Directory                             Size          Date    Time
 * ========================================================================
 * \toolbox\classes                                    06-25-2002  12:32a
 * \toolbox\resources                                  05-15-2002  11:54p
 * \toolbox\lib\junit.jar                   117,522    04-21-2002  05:57p
 * \toolbox\lib\log4j.jar                   158,892    04-21-2002  05:57p
 * \toolbox\lib\jakarta-regexp-1.2.jar       29,871    05-14-2002  06:58p
 * 
 * </pre>
 * 
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

    /** length format **/
    private static final 
        DecimalFormat LENGTH_FORMAT = new DecimalFormat("###,###,###");
        
    
    /**
     * Entry point
     * 
     * @param   args    command line params
     */
    public static void main(String[] args)
    {
        Main m = new Main();
        PrintWriter pw = new PrintWriter(System.out);
        m.showClasspath(pw);
    }


    /**
     * Shows the classpath
     */
    public void showClasspath(PrintWriter pw)
    {
        String classPath = System.getProperty("java.class.path");
         
        StringTokenizer st = 
            new StringTokenizer(classPath,System.getProperty("path.separator"));
            
        int max = 0;

        // Find longest classpath entry for formatting
        while (st.hasMoreElements())
        {
            String path = st.nextToken();

            if (path.length() > max)
                max = path.length();
        }

        max++;
        
        st = new StringTokenizer(
            classPath, System.getProperty("path.separator"));

        // Header row
        if (st.countTokens() > 0)
        {
            int rowLength = max + MAX_SIZE_LEN + 
                            MAX_DATE_LEN + MAX_TIME_LEN;
                            
            pw.println(StringUtil.repeat("=", rowLength));
            pw.print(COL_ARCHIVE);
            pw.print(repeatSpace(max - COL_ARCHIVE.length()));
            pw.print(repeatSpace(MAX_SIZE_LEN - COL_SIZE.length()));
            pw.print(COL_SIZE);
            pw.print(repeatSpace(MAX_DATE_LEN - COL_DATE.length()));
            pw.print(COL_DATE);
            pw.print(repeatSpace(MAX_TIME_LEN - COL_TIME.length()));
            pw.print(COL_TIME);
            pw.println();
            pw.println(StringUtil.repeat("=", rowLength));
        }

        // Loop through classpath
        while (st.hasMoreElements())
        {
            String path = st.nextToken();
            pw.print(path);

            for (int i = 0; i < (max - path.length()); i++)
                pw.print(" ");

            File f = new File(path);

            // If archive, get more info
            if (isArchive(path))
            {
                if (f.exists() && f.isFile() && f.canRead())
                {
                    Date lastModified = new Date(f.lastModified());
                    String date = DateUtil.format(lastModified);
                    String time = TimeUtil.format(lastModified);
                    String length = formatLength(f.length());
                    
                    pw.print(repeatSpace(MAX_SIZE_LEN - length.length()));
                        
                    pw.print(length);
                    pw.print(repeatSpace(MAX_DATE_LEN - date.length()));
                    pw.print(date);
                    pw.print(repeatSpace(MAX_TIME_LEN - time.length()));
                    pw.print(time);
                }
                else
                {
                    if (!f.exists())
                        pw.print("Does not exist!");
                    else if (!f.isFile())
                        pw.print("Is not a file!");
                    else if (!f.canRead())
                        pw.print("Cannot open file!");
                    else
                        pw.print("Unknown error opening file!");
                }
            }
            else if (f.isDirectory())
            {
                Date lastModified = new Date(f.lastModified());
                String date = DateUtil.format(lastModified);
                String time = TimeUtil.format(lastModified);
                pw.print(repeatSpace(MAX_SIZE_LEN));
                pw.print(repeatSpace(MAX_DATE_LEN - date.length()));
                pw.print(date);
                pw.print(repeatSpace(MAX_TIME_LEN - time.length()));
                pw.print(time);
            }
            else if (!f.exists())
            {
                pw.print("Does not exist!");
            }

            pw.println();
        }
        
        pw.flush();
    }


    /**
     * Formats the file size length to include commas: 1,233,276
     * 
     * @param   l   file length
     * @return      formatted length
     */
    protected String formatLength(long length)
    {
        return LENGTH_FORMAT.format(length);
    }


    /**
     * Determines if a file is a java archive
     * 
     * @param   s   absolute name of file
     * @return  true if file is a java archive, false otherwise
     */
    protected boolean isArchive(String s)
    {
        s = s.toUpperCase();

        if (s.endsWith(".JAR") || s.endsWith(".ZIP"))
            return true;
        else
            return false;
    }


    /**
     * Builds a string with a given number of spaces
     * 
     * @param   l   number of spaces
     * @return      string containing given number of spaces
     */
    protected String repeatSpace(int l)
    {
        return StringUtil.repeat(" ", l);
    }
}