package toolbox.util.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import toolbox.util.ResourceUtil;

/**
 * General SQL utility methods. <p>
 * Includes the following behavior:
 * <ul>
 *  <li>Parses SQL statements from a {@link java.io.File}, 
 *      {@link java.lang.String}, or {@link java.io.InputStream} (optionally
 *      including comments).
 * </ul>
 */
public class SQLUtil
{
    /**
     * Loads a file containing semicolon terminated sql statements into an
     * array of strings. Strips out comments which are identified by a '--' as
     * the first two charactes of a line.
     * 
     * @param file File to load sql statements from.
     * @return String[] containing individual sql statements.
     * @throws IOException on I/O error.
     */
    public static String[] parseSQL(File file) throws IOException {
        return parseSQL(ResourceUtil.getResource(file.getName()));
    }

    
    /**
     * Loads a stream containing semicolon terminated sql statements into an
     * array of strings. Strips out comments which are identified by a '--' as
     * the first two charactes of a.
     * 
     * @param is InputStream to read sql statements from.
     * @return String[] containing individual sql statements.
     * @throws IOException on I/O error. 
     */
    public static String[] parseSQL(InputStream is) throws IOException {
        return parseSQL(IOUtils.toString(is, "UTF-8"));
    }

    
    /**
     * Loads a file containing semicolon terminated sql statements into an
     * array of strings. Strips out comments which are identified by a '-- ' as
     * the first two charactes of a line.
     * 
     * @param sqlStatements String containing one or more sql statements.
     * @return String[] containing individual sql statements.
     * @throws IOException on I/O error.
     */
    public static String[] parseSQL(String sqlStatements) throws IOException {
        
        List results = new ArrayList();
        
        // Filter out comments first then parse for sql statements
        String[] withComments = StringUtils.split(sqlStatements, '\n');
        StringBuffer withOutComments = new StringBuffer();
        
        for (int i = 0; i < withComments.length; i++)
            if (!withComments[i].startsWith("--"))
                withOutComments.append(withComments[i] + "\n");
        
        // Split up into individual sql statements
        for (StringTokenizer st = 
                new StringTokenizer(withOutComments.toString(), ";"); 
             st.hasMoreTokens(); ) {
            
            String stmt = st.nextToken().trim();
            if (!StringUtils.isBlank(stmt))
                results.add(stmt + ";");
        }
        
        return (String[]) results.toArray(new String[0]);
    }
}