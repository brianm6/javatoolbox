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

/**
 * ClearToolAdapterTest is responsible for ___.
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
        
        IClearCaseAdapter cc = ClearCaseAdapterFactory.create();
        cc.setViewPath(new File("m:\\x1700_sandbox\\staffplanning"));
        
        List changed = 
            cc.findChangedFiles(
                new Date(), 
                new Date(), 
                new SuffixFileFilter(".java"));
        
        for (Iterator iter = changed.iterator(); iter.hasNext();)
        {
            VersionedFile file = (VersionedFile) iter.next();

            System.out.println("File   : " + file.getName());
            for (Iterator iterator = file.getRevisions().iterator(); iterator.hasNext();)
            {
                Revision revision = (Revision) iterator.next();
                System.out.println("Action : " + revision.getAction());
                System.out.println("User   : " + revision.getUser());
                System.out.println("Comment: " + revision.getComment());
                System.out.println();
            }
        }
        
    }
}
