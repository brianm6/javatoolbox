package toolbox.clearcase;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.DateUtil;

/**
 * RepositoryAuditor for a clearcase repository.
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
        ClearCaseBridge cc = ClearCaseBridgeFactory.create();
        cc.setViewPath(new File("m:\\x1700_sandbox\\staffplanning"));
        
        List changedFiles = 
            cc.findChangedFiles(
                DateUtil.addDays(new Date(), -1), 
                new Date(), 
                new SuffixFileFilter(".java"));

        
        CollectionUtils.filter(changedFiles, new NoCommentFilter());
        
        
        for (Iterator iter = changedFiles.iterator(); iter.hasNext();)
        {
            VersionedFile file = (VersionedFile) iter.next();

            System.out.println("File   : " + file.getName());
            for (Iterator iterator = file.getRevisions().iterator(); iterator.hasNext();)
            {
                Revision revision = (Revision) iterator.next();
                
                String user = revision.getUser();
                
                if (userMap_.containsKey(user))
                    user = userMap_.get(user).toString();
                
                System.out.println("Action : " + revision.getAction());
                System.out.println("User   : " + user);
                System.out.println("Comment: " + revision.getComment());
                System.out.println();
            }
        }
    }
    
    class NoCommentFilter implements Predicate
    {
        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        public boolean evaluate(Object object)
        {
            VersionedFile file = (VersionedFile) object;
            Revision rev = (Revision) file.getRevisions().iterator().next();
            return StringUtils.isBlank(rev.getComment());
        }
    }
}
