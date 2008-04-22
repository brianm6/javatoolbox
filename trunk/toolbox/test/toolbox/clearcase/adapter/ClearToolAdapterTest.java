package toolbox.clearcase.adapter;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;

import toolbox.clearcase.IClearCaseAdapter;
import toolbox.clearcase.domain.Revision;
import toolbox.clearcase.domain.VersionedFile;
import toolbox.util.ClassUtil;

/**
 * Unit test for {@link toolbox.clearcase.adapter.ClearToolAdapter}. 
 */
public class ClearToolAdapterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ClearToolAdapterTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(ClearToolAdapterTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testFindChangedFiles() throws Exception
    {
    	logger_.info("Running testFindChangedFiles...");

    	// Skip if cleartool executable not found
    	if ((ClassUtil.findInPath("cleartool") == null) &&
    	   (ClassUtil.findInPath("cleartool.exe") == null)) {
    		logger_.debug("Skipping testFindChangedFiles...");
    		return;
    	}
    	
        IClearCaseAdapter cc = ClearCaseAdapterFactory.create();
        cc.setViewPath(new File("c:\\clearcase\\myDynamicView"));
        
        List changed = 
            cc.findChangedFiles(
                new Date(), 
                new Date(), 
                new SuffixFileFilter(".java"));
        
        for (Iterator iter = changed.iterator(); iter.hasNext();)
        {
            VersionedFile file = (VersionedFile) iter.next();
            logger_.debug("File   : " + file.getName());
            
            for (Iterator iterator = file.getRevisions().iterator(); 
                 iterator.hasNext();)
            {
                Revision revision = (Revision) iterator.next();
                logger_.debug("Action : " + revision.getAction());
                logger_.debug("User   : " + revision.getUser());
                logger_.debug("Comment: " + revision.getComment());
                logger_.debug("");
            }
        }
    }
}