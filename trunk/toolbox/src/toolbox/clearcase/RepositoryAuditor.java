package toolbox.clearcase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.statcvs.util.FileUtils;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.clearcase.adapter.ClearCaseAdapterFactory;
import toolbox.clearcase.audit.ContainsTabsAudit;
import toolbox.clearcase.audit.MissingCommentAudit;
import toolbox.util.DateUtil;
import toolbox.util.FileUtil;
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
                DateUtil.addDays(new Date(), -3), 
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
        
        for (Iterator iter = finalResults.iterator(); iter.hasNext();)
        {
            IAuditResult result = (IAuditResult) iter.next();
            
            StringBuffer sb = new StringBuffer();
            sb.append(StringUtils.rightPad(getAlias(result.getUsername()), 10));
            sb.append("  ");
            
            sb.append(
                StringUtils.rightPad(
                    FileUtils.getFilenameWithoutPath(
                        result.getFilename()), 15));
            
            sb.append("  ");
            sb.append(StringUtils.rightPad(result.getReason(), 20));
            sb.append("  ");
            sb.append(StringUtils.rightPad(result.getDate(), 20));
            sb.append("  ");
            sb.append(FileUtil.stripFile(result.getFilename()));
            
            System.out.println(sb);
        }
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
    
}
