package toolbox.findclass.test;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.findclass.FindClass;
import toolbox.findclass.FindClassListener;
import toolbox.findclass.FindClassResult;
import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;
import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.concurrent.Mutex;

/**
 * Unit test for Findclass.
 */
public class FindClassTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(FindClassTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FindClassTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test finding a system class.
     * 
     * @throws Exception on error
     */
    public void testFindClassSystem() throws Exception
    {
        logger_.info("Running testFindClassSystem...");
        
        String searchFor = "java.lang.Object";
        FindClass finder = new FindClass();
        
        String results = 
            ArrayUtil.toString(
                finder.findClass(searchFor, false), true);

        logger_.info("Results: " + results);
        assertTrue("Couldn't find " + searchFor, 
                results.indexOf(searchFor) >= 0);
    }
    
    
    /**
     * Test class not found.
     * 
     * @throws Exception on error
     */
    public void testFindClassNotFound() throws Exception
    {
        logger_.info("Running testFindClassNotFound...");
        
        String searchFor = "java.lang.Bogus";
        FindClass finder = new FindClass();
        FindClassResult[] results = finder.findClass(searchFor, false);
        assertEquals("Class should not have been found", 0, results.length);
    }

    
    /**
     * Test case sensetivity.
     * 
     * @throws Exception on error
     */
    public void testFindClassCaseSensetivity() throws Exception
    {
        logger_.info("Running testFindClassCaseSensetivity...");
        
        String searchFor = "java.lang.object"; // <== o is lowercase
        FindClass finder = new FindClass();
        FindClassResult[] results = finder.findClass(searchFor, false);
        assertEquals("Class should not have been found", 0, results.length);
        
        results = finder.findClass(searchFor, true); // Ignore case
        assertEquals("Class should have been found", 1, results.length);
    }
    
    
    /**
     * Test finding an archive class.
     * 
     * @throws Exception on error
     */
    public void xtestFindClassInArchive() throws Exception
    {
        logger_.info("Running testFindClassInArchive...");
        
        String searchFor = getClass().getName();
        FindClass finder = new FindClass();
        FindClassResult[] results = finder.findClass(searchFor, false);

        logger_.info("Results: " + ArrayUtil.toString(results));
        assertEquals("Couldn't find " + searchFor, 1, results.length);
    }
    
    
    /**
     * Test adding/removing search targets.
     * 
     * @throws Exception on error
     */
    public void testSearchTargets() throws Exception
    {
        logger_.info("Running testSearchTargets...");
        
        FindClass finder = new FindClass();
        
        // Get the default targets
        List targets = finder.getSearchTargets();
        assertTrue(targets.size() > 0);
        
        // Remove the first one
        String target = (String) targets.get(0);
        finder.removeSearchTarget(target);
        
        // Verify it was removed
        List removed = finder.getSearchTargets();
        assertFalse(removed.contains(target));
        
        // Add it back in
        finder.addSearchTarget(target);
        
        // Verify it was added
        List added = finder.getSearchTargets();
        assertTrue(added.contains(target));
        
        // Add search targets as both file and directory
        finder.addSearchTarget(new File("."));
        
        for (Iterator i = added.iterator(); i.hasNext();)
        {    
            String s = (String) i.next();
            
            logger_.info("finding jar: " + s);
            
            if (ClassUtil.isArchive(s))
            {    
                finder.addSearchTarget(new File(s));
                break;
            }
        }

        // Remove all
        finder.removeSearchTargets();
        List cleared = finder.getSearchTargets();
        assertTrue(cleared.isEmpty());
    }

    
    /**
     * Test getArchivesInDir()
     * 
     * @throws Exception on error
     */
    public void testGetArchivesInDir() throws Exception
    {
        logger_.info("Running testGetArchivesInDir..."); 
        
        FindClass finder = new FindClass();
        List archives = finder.getArchivesInDir(FileUtil.getTempDir());
        
        logger_.info(archives.size() + " found in temp dir.");
    }
     
    
    /**
     * Test canceling a search.
     * 
     * @throws Exception on error
     */
    public void testCancelSearch() throws Exception
    {
        logger_.info("Running testCancelSearch...");
        
        FindClass finder = new FindClass();
        SearchListener listener = new SearchListener();
        finder.addSearchListener(listener);
        
        logger_.info("Before search...");
        
        ThreadUtil.run(
                finder, 
                "findClass", 
                new Object[] { "xxx", new Boolean(false) });
        
        logger_.info("Giving finder a head start..");
        ThreadUtil.sleep(5000); 
        
        logger_.info("Before cancel..");
        finder.cancelSearch();
        logger_.info("After cancelled..");
        
        listener.waitForCancel();
        
        finder.removeSearchListener(listener);
        finder.removeSearchListeners();
    }       
    
    //--------------------------------------------------------------------------
    // SearchListener 
    //--------------------------------------------------------------------------
    
    class SearchListener implements FindClassListener
    {
        Mutex cancel = new Mutex();
        Mutex first = new Mutex();
        
        public SearchListener() throws InterruptedException
        {
            cancel.acquire();
            first.acquire();
        }
        
        public void waitForCancel() throws InterruptedException
        {
            cancel.acquire();
        }
        
        public void waitForFirst() throws InterruptedException
        {
            first.acquire();
        }
        
        /**
		 * @see toolbox.findclass.FindClassListener#searchCancelled()
		 */
        public void searchCancelled()
        {
            logger_.info("SearchCanceled");
            cancel.release();
        }

        /**
		 * @see toolbox.findclass.FindClassListener#searchCompleted(
         *      java.lang.String)
		 */
        public void searchCompleted(String search)
        {
            logger_.info("SearchCompleted " + search);
        }

        /**
		 * @see toolbox.findclass.FindClassListener#classFound(
         *      toolbox.findclass.FindClassResult)
		 */
        public void classFound(FindClassResult searchResult)
        {
            logger_.info("ClassFound " + searchResult.getClassFQN());
        }

        /**
		 * @see toolbox.findclass.FindClassListener#searchingTarget(
         *      java.lang.String)
		 */
        public void searchingTarget(String target)
        {
            logger_.info("SearchingTarget " + target);
            first.release();
        }
    }
}