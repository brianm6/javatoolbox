package toolbox.clearcase.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import toolbox.clearcase.IAudit;
import toolbox.clearcase.IAuditResult;
import toolbox.clearcase.domain.Revision;
import toolbox.clearcase.domain.VersionedFile;

/**
 * MissingCommentAudit is responsible for ___.
 */
public class MissingCommentAudit implements IAudit
{
    /**
     * Creates a MissingCommentAudit.
     */
    public MissingCommentAudit()
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
        
        Collection matches = 
            CollectionUtils.select(versionedFiles, new NoCommentFilter());
        
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
            result.setReason("Missing comment");
            results.add(result);
        }

        return results;
    }

    //--------------------------------------------------------------------------
    // NoCommentFilter
    //--------------------------------------------------------------------------
    
    class NoCommentFilter implements Predicate
    {
        /**
         * @see org.apache.commons.collections.Predicate#evaluate(
         *      java.lang.Object)
         */
        public boolean evaluate(Object object)
        {
            VersionedFile file = (VersionedFile) object;
            Revision rev = (Revision) file.getRevisions().iterator().next();
            return StringUtils.isBlank(rev.getComment());
        }
    }
}
