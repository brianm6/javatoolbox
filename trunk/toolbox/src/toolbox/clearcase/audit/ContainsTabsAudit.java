package toolbox.clearcase.audit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.lang.StringUtils;

import toolbox.clearcase.IAudit;
import toolbox.clearcase.IAuditResult;
import toolbox.clearcase.domain.Revision;
import toolbox.clearcase.domain.VersionedFile;
import toolbox.util.FileUtil;

/**
 * Audits a collection of VersionedFiles to determine if they contain one or 
 * more tab characters.
 * 
 * @see toolbox.clearcase.RepositoryAuditor
 */
public class ContainsTabsAudit implements IAudit
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ContainsTabsAudit.
     */
    public ContainsTabsAudit()
    {
    }

    //--------------------------------------------------------------------------
    // IAudit Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.clearcase.IAudit#audit(java.util.List)
     */
    public List audit(List versionedFiles)
    {
        List results = new ArrayList();
        Map tabCountMap = new HashMap();
        
        Collection matches = 
            CollectionUtils.select(
                versionedFiles, 
                new AndPredicate(
                    new SuffixFilter(), 
                    new ContainsTabsFilter(tabCountMap)));
        
        for (Iterator iter = matches.iterator(); iter.hasNext();)
        {
            VersionedFile file = (VersionedFile) iter.next();

            //System.out.println("File   : " + file.getName());
            
            for (Iterator iterator = file.getRevisions().iterator(); 
                 iterator.hasNext();)
            {
                Revision revision = (Revision) iterator.next();
                
                String user = revision.getUser();
                
                //if (userMap_.containsKey(user))
                //    user = userMap_.get(user).toString();
                
                //System.out.println("Action : " + revision.getAction());
                //System.out.println("User   : " + user);
                //System.out.println("Comment: " + revision.getComment());
                //System.out.println();
            }
            
            IAuditResult result = new AuditResult(file);
            result.setReason("Contains " + tabCountMap.get(file) + " tabs");
            results.add(result);
        }

        return results;        
    }

    //--------------------------------------------------------------------------
    // SuffixFilter
    //--------------------------------------------------------------------------

    /**
     * SuffixFilter allows use to filter the list of files down to only those
     * with a particular suffix.
     */
    class SuffixFilter implements Predicate
    {
        /**
         * @see org.apache.commons.collections.Predicate#evaluate(
         *      java.lang.Object)
         */
        public boolean evaluate(Object object)
        {
            VersionedFile file = (VersionedFile) object;
            return file.getName().endsWith(".java");
        }
    }
    
    //--------------------------------------------------------------------------
    // ContainsTabsFilter
    //--------------------------------------------------------------------------
    
    /**
     * Filters out files which contain a tab.
     */
    class ContainsTabsFilter implements Predicate
    {
        private Map tabCountMap_;
        
        
        public ContainsTabsFilter(Map tabCountMap) 
        {
            tabCountMap_ = tabCountMap;
        }

        
        /**
         * @see org.apache.commons.collections.Predicate#evaluate(
         *      java.lang.Object)
         */
        public boolean evaluate(Object object)
        {
            VersionedFile file = (VersionedFile) object;
            try
            {
                String s = FileUtil.getFileContents(file.getName());
                int numTabs = StringUtils.countMatches(s, "\t");
                tabCountMap_.put(file, new Integer(numTabs));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
            Revision rev = (Revision) file.getRevisions().iterator().next();
            return StringUtils.isBlank(rev.getComment());
        }
    }
}