package toolbox.clearcase;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;

/**
 * ClearToolBridgeTest is responsible for ___.
 */
public class ClearToolBridgeTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ClearToolBridgeTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(ClearToolBridgeTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testFindChangedFiles() throws Exception
    {
        logger_.info("Running testFindChangedFiles...");
        
        ClearCaseBridge cc = ClearCaseBridgeFactory.create();
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
