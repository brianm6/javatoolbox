package toolbox.clearcase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.clearcase.adapter.ClearCaseAdapterFactory;
import toolbox.clearcase.audit.ContainsTabsAudit;
import toolbox.clearcase.audit.MissingCommentAudit;
import toolbox.util.DateOnlyUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.collections.ObjectComparator;

/**
 * RepositoryAuditor for a clearcase repository.
 * 
 * @see toolbox.clearcase.IAudit
 */
public class RepositoryAuditor
{
    private static final Logger logger_ = 
        Logger.getLogger(RepositoryAuditor.class);
    
    //--------------------------------------------------------------------------
    // Properties Constants
    //--------------------------------------------------------------------------

    /** 
     * Properties file to read configuration from.
     */
    private static final String FILENAME_CLEARCASE_PROPS = 
        "clearcase.properties";
    
    /**
     * Property for the path to the clearcase view.
     */
    private static final String PROP_VIEW_PATH = "clearcase.view.path";
    
    /**
     * Property for the number of days of history to include in the audit.
     */
    private static final String PROP_HISTORY_DAYS = "clearcase.audit.span.days";
    
    /**
     * Maps usernames to real names.
     */
    private static Map userMap_;

    //--------------------------------------------------------------------------
    // Static Blocks
    //--------------------------------------------------------------------------
    
    static
    {
        userMap_ = new HashMap();
        userMap_.put("x1234", "John Doe");
    }
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Configuration properties for the audit.
     */
    private Properties props_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Launches the RepositoryAuditor. The only argument supported is the name
     * of the properties file containing configuration information. If no
     * file name is specified, then clearcase.properties is used as the default.
     * 
     * @param args Name of the properties file to use for configuration. Should
     *        exist on the classpath.
     */
    public static void main(String[] args)
    {
        // Hardcode config defaults
        Properties props = new Properties();
        props.setProperty(PROP_VIEW_PATH, "m:\\x1700_sandbox\\staffplanning");
        props.setProperty(PROP_HISTORY_DAYS, "15");

        String propsFile = 
            args.length == 0 ? FILENAME_CLEARCASE_PROPS : args[0];
        
        try
        {
            // Override with settings from props file if available
            InputStream is = ResourceUtil.getResource(propsFile);

            if (is != null)
            {
                props.load(is);
                IOUtils.closeQuietly(is);
            }

            RepositoryAuditor auditor = new RepositoryAuditor(props);
            auditor.run();
        }
        catch (IOException e)
        {
            logger_.error("main", e);
        }
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a RepositoryAuditor.
     */
    public RepositoryAuditor(Properties props)
    {
        props_ = props;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    public void run() throws IOException 
    {
        IClearCaseAdapter cc = ClearCaseAdapterFactory.create();
        cc.setViewPath(new File(getViewPath()));
        
        // Find changed files between now and 'history days' back.
        List changedFiles = 
            cc.findChangedFiles(
                DateOnlyUtil.addDays(new Date(), -getHistoryDays()), 
                new Date(), 
                new SuffixFileFilter("*"));

        // Build up the list of audits to run...
        List audits = new ArrayList();
        audits.add(new MissingCommentAudit());
        audits.add(new ContainsTabsAudit());
        List finalResults = new ArrayList();
        
        // Run the audits
        for (Iterator iter = audits.iterator(); iter.hasNext();)
        {
            IAudit audit = (IAudit) iter.next();
            finalResults.addAll(audit.audit(changedFiles));
        }

        // Sort the audit results
        Comparator sortByUser = new ObjectComparator("username", "filename");
        Collections.sort(finalResults, sortByUser);
        
        // Find the max length of the data in the results so the output looks
        // nice and lined up
        int maxUsername = getMaxLength(finalResults, "username");
        int maxFilename = getMaxLength(finalResults, "fileOnly");
        int maxReason   = getMaxLength(finalResults, "reason");
        int maxDate     = getMaxLength(finalResults, "date");

        // Create header row
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtils.repeat("=", 80));
        sb.append("\n");
        sb.append(StringUtils.rightPad("User", maxUsername));
        sb.append(StringUtils.rightPad("File", maxFilename));
        sb.append(StringUtils.rightPad("Reason", maxReason));
        sb.append(StringUtils.rightPad("Timestamp", maxDate));
        sb.append("Dir\n");
        sb.append(StringUtils.repeat("=", 80));
        sb.append("\n");

        // Format the audit results
        for (ListIterator iter = finalResults.listIterator(); iter.hasNext();)
        {
            IAuditResult result = (IAuditResult) iter.next();
        
            sb.append(StringUtils.rightPad(
                getAlias(result.getUsername()), maxUsername));
            
            sb.append(StringUtils.rightPad(result.getFileOnly(), maxFilename));
            sb.append(StringUtils.rightPad(result.getReason(), maxReason));
            sb.append(StringUtils.rightPad(result.getDate(), maxDate));
            sb.append(FilenameUtils.getFullPathNoEndSeparator(result.getFilename()));
            sb.append("\n");
        }
        
        System.out.println(sb);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Returns the number of days of change control history to include in the
     * audit starting from today.
     * 
     * @return int
     */
    protected int getHistoryDays()
    {
        return Integer.parseInt(props_.getProperty(PROP_HISTORY_DAYS));
    }

    
    /**
     * Returns the absolute path of the clearcase view.
     * 
     * @return String
     */
    protected String getViewPath()
    {
        return props_.getProperty(PROP_VIEW_PATH);
    }

    
    /**
     * Returns the maximum number of columns occupied by a given string java
     * bean property on a collection of objects. Adds two to length to account
     * for the gap between columns.
     * 
     * @param objects Collection of like objects.
     * @param propName Name of javabean property containing a string.
     * @return String
     */
    protected int getMaxLength(Collection objects, String propName)
    {
        MaxStringLengthFinder finder = new MaxStringLengthFinder(propName);
        CollectionUtils.forAllDo(objects, finder);
        return finder.getMaxLength() + 2;
    }
    
    
    /**
     * Maps a username to a real name. Returns the username if a mapping for the
     * real name does not exist.
     * 
     * @param username Username to get the real name for.
     * @return String
     */
    protected String getAlias(String username)
    {
        String result = (String) userMap_.get(username);
        if (result == null)
            result = username;
        
        return result;
    }
    
    //--------------------------------------------------------------------------
    // MaxStringLengthFinder
    //--------------------------------------------------------------------------
    
    /**
     * Finds the max length of a string bean property given the java bean
     * property name.
     */
    class MaxStringLengthFinder implements Closure
    {
        //----------------------------------------------------------------------
        // Fields
        //----------------------------------------------------------------------
        
        /**
         * Javabean property name.
         */
        private String propName_;
        
        /**
         * Maximum string length found so far.
         */
        private int max_;
        
        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        /**
         * Creates a MaxStringLengthFinder.
         * 
         * @param propName Javabean property name of the field to find the
         *        string length of.
         */
        public MaxStringLengthFinder(String propName)
        {
            propName_ = propName;
            max_ = 0;
        }

        //----------------------------------------------------------------------
        // Closure Interface
        //----------------------------------------------------------------------
        
        /**
         * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
         */
        public void execute(Object input)
        {
            try
            {
                String value = BeanUtils.getProperty(input, propName_);
                max_ = Math.max(max_, value.length());
            }
            catch (Exception e)
            {
                logger_.error(e);
            }
        }
        
        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------
        
        /**
         * Returns the maximum string length across all invocations of
         * execute().
         * 
         * @return int
         */
        public int getMaxLength()
        {
            return max_;
        }
    }
}