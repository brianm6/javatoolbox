package toolbox.clearcase;

import java.io.File;
import java.io.IOException;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.clearcase.adapter.ClearCaseAdapterFactory;
import toolbox.clearcase.audit.ContainsTabsAudit;
import toolbox.clearcase.audit.MissingCommentAudit;
import toolbox.util.DateUtil;
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
    // Constants
    //--------------------------------------------------------------------------
    
    private static final String PROPS_FILE_CLEARCASE = "clearcase.properties";

    private static Map userMap_;
    
    static
    {
        userMap_ = new HashMap();
        userMap_.put("x1700", "Semir");
        userMap_.put("e74254", "Clay");
        userMap_.put("e68041", "Bob");
        userMap_.put("e47457", "Rama");
        userMap_.put("e63591", "Alan");
    }
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        RepositoryAuditor auditor = new RepositoryAuditor();
        
        try
        {
            auditor.run();
        }
        catch (IOException e)
        {
            logger_.error(e);
        }
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a RepositoryAuditor.
     */
    public RepositoryAuditor()
    {
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    public void run() throws IOException 
    {
        IClearCaseAdapter cc = ClearCaseAdapterFactory.create();
        cc.setViewPath(new File("m:\\x1700_sandbox\\staffplanning"));
        
        List changedFiles = 
            cc.findChangedFiles(
                DateUtil.addDays(new Date(), -30), 
                new Date(), 
                new SuffixFileFilter(".java"));

        
        List audits = new ArrayList();
        audits.add(new MissingCommentAudit());
        audits.add(new ContainsTabsAudit());
        List finalResults = new ArrayList();
        
        for (Iterator iter = audits.iterator(); iter.hasNext();)
        {
            IAudit audit = (IAudit) iter.next();
            finalResults.addAll(audit.audit(changedFiles));
        }

        Comparator sortByUser = new ObjectComparator("username", "filename");
        Collections.sort(finalResults, sortByUser);
        
        int maxUsername = getMaxLength(finalResults, "username");
        int maxFilename = getMaxLength(finalResults, "fileOnly");
        int maxReason   = getMaxLength(finalResults, "reason");
        int maxDate     = getMaxLength(finalResults, "date");

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
        
        
        for (ListIterator iter = finalResults.listIterator(); iter.hasNext();)
        {
            IAuditResult result = (IAuditResult) iter.next();
        
            sb.append(StringUtils.rightPad(
                getAlias(result.getUsername()), maxUsername));
            
            sb.append(StringUtils.rightPad(result.getFileOnly(), maxFilename));
            sb.append(StringUtils.rightPad(result.getReason(), maxReason));
            sb.append(StringUtils.rightPad(result.getDate(), maxDate));
            sb.append(FilenameUtils.getPath(result.getFilename()));
            sb.append("\n");
        }
        
        System.out.println(sb);
    }

    
    /**
     * 
     * @param coll
     * @param propName
     * @return
     */
    private int getMaxLength(Collection coll, String propName)
    {
        MaxStringLengthFinder finder = new MaxStringLengthFinder(propName);
        CollectionUtils.forAllDo(coll, finder);
        return finder.getMaxLength() + 2;
    }
    
    
    /**
     * @param username
     * @return
     */
    private String getAlias(String username)
    {
        String result = (String) userMap_.get(username);
        if (result == null)
            result = username;
        
        return result;
    }
    
    //--------------------------------------------------------------------------
    // StringLengthClosure
    //--------------------------------------------------------------------------
    
    class MaxStringLengthFinder implements Closure
    {
        String propName_;
        int max_;
        
        public MaxStringLengthFinder(String propName)
        {
            propName_ = propName;
            max_ = 0;
        }
        
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
        
        
        public int getMaxLength()
        {
            return max_;
        }
    }
}
