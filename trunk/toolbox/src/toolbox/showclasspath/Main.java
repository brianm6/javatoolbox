package toolbox.showclasspath;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import toolbox.util.ClassUtil;

/**
 * Shows the current classpath along with detailed info as reported by
 * System property <code>java.class.path</code>. Also, detects redundant
 * and invalid classpath entries such as jars/dirs that don't exist.
 *
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 * ========================================================================
 * JAR/Directory                               Size          Date    Time
 * ========================================================================
 * \toolbox\classes                           [DIR]    06-25-2002  12:32a
 * \toolbox\resources                         [DIR]    05-15-2002  11:54p
 * \toolbox\lib\junit.jar                   117,522    04-21-2002  05:57p
 * \toolbox\lib\log4j.jar                   158,892    04-21-2002  05:57p
 * \toolbox\lib\jakarta-regexp-1.2.jar       29,871    05-14-2002  06:58p
 * </pre>
 */
public final class Main
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * Max length for the jar file size column.
     */
    private static final int MAX_SIZE_LEN = 12;

    /**
     * Max length for the jar file/directory date column.
     */
    private static final int MAX_DATE_LEN = 14;

    /**
     * Max length for the jar file/directory time column.
     */
    private static final int MAX_TIME_LEN = 8;

    /**
     * Column heading for the archive/path classpath elements.
     */
    private static final String COL_ARCHIVE = "JAR/Directory";

    /**
     * Column heading for the jar file/directory date.
     */
    private static final String COL_DATE = "Date";

    /**
     * Column heading for the jar file size.
     */
    private static final String COL_SIZE = "Size";

    /**
     * Column heading for the jar file/directory time.
     */
    private static final String COL_TIME = "Time";

    /**
     * Formatter for dates.
     */
    private static final SimpleDateFormat dateFormat_ =
        new SimpleDateFormat("MM-dd-yyyy");

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entry point.
     *
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        showPath(System.out);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Writes the classpath to the given output stream.
     *
     * @param out OutputStream to send classpath listing to.
     */
    public static void showPath(OutputStream out)
    {
        PrintStream ps = new PrintStream(out, true);
        String classPath = ClassUtil.getClasspath();

        StringTokenizer st =
            new StringTokenizer(
                classPath,
                System.getProperty("path.separator"));

        int max = 0;

        // Find longest path/archive for formatting
        while (st.hasMoreElements())
            max = Math.max(st.nextToken().length(), max);

        max++;

        st = new StringTokenizer(
                classPath,
                System.getProperty("path.separator"));

        // Header row
        if (st.countTokens() > 0)
        {
            int rowLength =
                max +
                MAX_SIZE_LEN +
                MAX_DATE_LEN +
                MAX_TIME_LEN;

            ps.println(StringUtils.repeat("=", rowLength));
            ps.print(StringUtils.rightPad(COL_ARCHIVE, max));
            ps.print(StringUtils.leftPad(COL_SIZE, MAX_SIZE_LEN));
            ps.print(StringUtils.leftPad(COL_DATE, MAX_DATE_LEN));
            ps.print(StringUtils.leftPad(COL_TIME, MAX_TIME_LEN));
            ps.println();
            ps.println(StringUtils.repeat("=", rowLength));
        }

        // loop through classpath
        while (st.hasMoreElements())
        {
            String path = st.nextToken();
            ps.print(StringUtils.rightPad(path, max));
            File f = new File(path);

            // If archive, get more info
            if (ClassUtil.isArchive(path))
            {
                if (f.exists() && f.isFile() && f.canRead())
                {
                    Date lastModified = new Date(f.lastModified());

                    ps.print(StringUtils.leftPad(
                        NumberFormat.getIntegerInstance().format(f.length()),
                        MAX_SIZE_LEN));

                    ps.print(StringUtils.leftPad(
                        dateFormat_.format(lastModified), MAX_DATE_LEN));

                    ps.print(StringUtils.leftPad(
                        formatTime(lastModified), MAX_TIME_LEN));
                }
                else
                {
                    if (!f.exists())
                    {
                        ps.print(StringUtils.leftPad("[Missing]", MAX_SIZE_LEN));
                    }
                    else if (!f.isFile())
                    {
                        ps.print(StringUtils.leftPad(
                            "[Not File]", MAX_SIZE_LEN));
                    }
                    else if (!f.canRead())
                    {
                        ps.print(StringUtils.leftPad(
                            "[ReadOnly]", MAX_SIZE_LEN));
                    }
                    else
                    {
                        ps.print(StringUtils.leftPad("[Error]", MAX_SIZE_LEN));
                    }
                }
            }
            else if (f.isDirectory())
            {
                Date lastModified = new Date(f.lastModified());
                ps.print(StringUtils.leftPad("[DIR]", MAX_SIZE_LEN));

                ps.print(StringUtils.leftPad(
                    dateFormat_.format(lastModified), MAX_DATE_LEN));

                ps.print(StringUtils.leftPad(
                    formatTime(lastModified), MAX_TIME_LEN));
            }
            else if (!f.exists())
            {
                ps.print(StringUtils.leftPad("[Missing]", MAX_SIZE_LEN));
            }

            ps.println();
        }
    }

    //--------------------------------------------------------------------------
    // Package Protected
    //--------------------------------------------------------------------------

    /**
     * Formats time to specific format:  12:47a  01:07p
     *
     * @param t Date to format.
     * @return Formatted time.
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
}