package toolbox.tree;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.NullWriter;

/**
 * Unit test for {@link toolbox.tree.Tree2}.
 */
public class Tree2Test extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(Tree2Test.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Temporary directory that will serve as the root dir for tests. 
     */
    private File rootTestDir_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(Tree2Test.class);
    }

    //--------------------------------------------------------------------------
    // Overrides junit.framework.TestCase
    //--------------------------------------------------------------------------
    
    /**
     * Create a test directory n the System temp dir to play around in.
     * 
     * @throws IOException on I/O error.
     */
    public void setUp() throws IOException
    {
        rootTestDir_ = FileUtil.createTempDir();
    }

    
    /**
     * Removes the test directory.
     */
    public void tearDown() throws IOException
    {
        FileUtils.deleteDirectory(rootTestDir_);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests the constructors.
     * 
     * @throws Exception on error.
     */
    public void testConstructors() throws Exception
    {
        logger_.info("Running testConstructors...");
        
        Tree2 t = new Tree2(FileUtil.getTempDir());
        assertNotNull(t);
        
        Tree2 t2 = new Tree2(FileUtil.getTempDir(), true);
        assertNotNull(t2);
        
        Tree2 t3 = new Tree2(FileUtil.getTempDir(), new StringWriter());
        assertNotNull(t3);
        
        Tree2 t4 = new Tree2(FileUtil.getTempDir(), new StringWriter());        
        assertNotNull(t4);
        
        Tree2 t5 = new Tree2(FileUtil.getTempDir(), true, true);
        assertNotNull(t5);
        
        Tree2 t6 = new Tree2(FileUtil.getTempDir(), 
            true, true, Tree2.SORT_NAME);
        
        assertNotNull(t6);

        Tree2 t7 = new Tree2(FileUtil.getTempDir(), 
            true, true, true, false, Tree2.SORT_NAME, 5, ".*");
        
        assertNotNull(t7);
    }

    
    /**
     * Tests for a simple cascading structure.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeSimpleCascade() throws Exception
    {
        logger_.info("Running testShowTreeSimpleCascade...");
        logger_.debug("Tree with cascading dirs: \n");
        
        File a = new File(rootTestDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (b, "c");
        assertTrue(c.mkdir());
        
        File d = new File(c, "d");
        assertTrue(d.mkdir());
        
        Tree2 tree = new Tree2(rootTestDir_);
        tree.showTree();
        
        printNativeTree(rootTestDir_);
    }

    
    /**
     * Tests for a simple flat structure.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeSimpleFlat() throws Exception
    {
        logger_.info("Running testShowTreeSimpleFlat...");
        logger_.debug("Tree with flat struct at level 2: \n");
        
        File a = new File(rootTestDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (a, "c");
        assertTrue(c.mkdir());
        
        File d = new File(a, "d");
        assertTrue(d.mkdir());
        
        Tree2 tree = new Tree2(rootTestDir_);
        tree.showTree();
        
        printNativeTree(rootTestDir_);
    }

    
    /**
     * Tests for an extension bar.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeExtensionBar() throws Exception
    {
        logger_.info("Running testShowTreeExtensionBar...");
        logger_.debug("Tree with an extension bar: \n");
                
        // Create rootDir_\a\b\c\d 
        File a = new File(rootTestDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (b, "c");
        assertTrue(c.mkdir());
        
        File d = new File(a, "d");
        assertTrue(d.mkdir());
        
        Tree2 tree = new Tree2(rootTestDir_);
        tree.showTree();
        
        printNativeTree(rootTestDir_);
    }

    
    /**
     * Tests for more then one dir in the root.
     * <pre> 
     *
     *   rootDir_
     *   |
     *   +---a
     *   |   +---b
     *   |   
     *   +---c
     *       +---d
     *
     * </pre>
     * 
     * @throws Exception on error.
     */
    public void testShowTreeManyInRoot() throws Exception
    {
        logger_.info("Running testShowTreeManyInRoot...");
        logger_.debug("Tree with > 1 dir in root: \n");
                
        File a = new File(rootTestDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (rootTestDir_, "c");
        assertTrue(c.mkdir());
        
        File d = new File(c, "d");
        assertTrue(d.mkdir());
        
        Tree2 tree = new Tree2(rootTestDir_);
        tree.showTree();
        
        printNativeTree(rootTestDir_);
    }

    
    /**
     * Tests for an empty root directory.
     * <pre> 
     *
     *   rootDir_
     *
     * </pre>
     * 
     * @throws Exception on error.
     */
    public void testShowTreeEmptyRoot() throws Exception
    {
        logger_.info("Running testShowTreeEmptyRoot...");
        logger_.debug("Tree with an empty root: \n");
        Tree2 tree = new Tree2(rootTestDir_);
        tree.showTree();
        
        printNativeTree(rootTestDir_);
    }

    
    /**
     * Tests tree with only one folder.
     * <pre> 
     *
     *   rootDir_
     *   |
     *   +---a
     *
     * </pre>
     * 
     * @throws Exception on error.
     */
    public void testShowTreeOneDir() throws Exception
    {
        logger_.info("Running testShowTreeOneDir...");
        logger_.debug("Tree with a single directory: \n");

        File a = new File(rootTestDir_, "a");
        assertTrue(a.mkdir());
        
        Tree2 tree = new Tree2(rootTestDir_);
        tree.showTree();
        
        printNativeTree(rootTestDir_);
    }

    
    /**
     * Tests for a large directory structure.
     * 
     * @throws Exception on error.
     */
    public void xtestShowTreeLargeTree() throws Exception
    {
        logger_.info("Running testShowTreeLargeTree...");
        Tree2 tree = new Tree2(new File("/"));
        tree.showTree();
        
        // printNativeTree(rootDir_);
    }


    /**
     * Tests for a simple cascading structure with one file.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeSimpleCascadeFile() throws Exception
    {
        logger_.debug("Running testShowTreeSimpleCascadeFile...");
        logger_.debug("Tree2 with cascading dirs and one file: \n");
        
        createFile(rootTestDir_);
        
        File a = new File(rootTestDir_, "a");
        assertTrue(a.mkdir());
        createFile(a);
                
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        createFile(b);
        
        File c = new File (b, "c");
        assertTrue(c.mkdir());
        createFile(c);
        
        File d = new File(c, "d");
        assertTrue(d.mkdir());
        createFile(d);
               
        Tree2 tree = new Tree2(rootTestDir_, true);
        tree.showTree();
        
        printNativeFileTree(rootTestDir_);
    }

    
    /**
     * Tests for an empty tree with files in the root only.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeRootFiles() throws Exception
    {
        logger_.info("Running testShowTreeRootFiles...");
        logger_.debug("Tree with cascading dirs and one file: \n");
        
        createFile(rootTestDir_);
        createFile(rootTestDir_);
        createFile(rootTestDir_);
        createFile(rootTestDir_);
        createFile(rootTestDir_);
               
        Tree2 tree = new Tree2(rootTestDir_, true);
        tree.showTree();
        
        printNativeFileTree(rootTestDir_);
    }


    /**
     * Shows tree with file sizes.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeFileSizes() throws Exception
    {
        logger_.info("Running testShowTreeFileSizes...");
        
        createFile(rootTestDir_);
        createFile(rootTestDir_);
        createFile(rootTestDir_);
        createFile(rootTestDir_);
        createFile(rootTestDir_);
               
        Tree2 tree = new Tree2(rootTestDir_, true, true);
        tree.showTree();
        
        printNativeFileTree(rootTestDir_);
    }
    

    /**
     * Shows tree with file dates
     * 
     * @throws Exception on error.
     */
    public void testShowTreeFileDates() throws Exception
    {
        logger_.info("Running testShowTreeFileDates...");
        
        createFile(rootTestDir_);
        createFile(rootTestDir_);
        createFile(rootTestDir_);
        createFile(rootTestDir_);
        createFile(rootTestDir_);
               
        Tree2 tree = new Tree2(
            rootTestDir_, 
            true, 
            false, 
            true, 
            false, 
            Tree2.SORT_DATE, 
            ".*", 
            Integer.MAX_VALUE,
            new OutputStreamWriter(System.out));
        
        tree.showTree();
        
        printNativeFileTree(rootTestDir_);
    }
    
    
    /**
     * Tests toString(); 
     */
    public void testToString()
    {
        logger_.info("Running testToString...");
        
        Tree2 t = new Tree2(FileUtil.getTempDir());
        String s = t.toString();
        logger_.debug(s);
        assertNotNull(s);
    }
    
    
    /**
     * Tests printing the help/usage information.
     * 
     * @throws Exception on error.
     */
    public void testPrintUsage() throws Exception
    {
        logger_.info("Running testPrintUsage...");
        
        // Send in an invalid flag so usage information is shown
        Tree2.main(new String[] {"-xyz"});
    }
    
    
    /**
     * Tests execution via main().
     * 
     * @throws Exception on error.
     */
    public void testMain() throws Exception
    {
        // TODO: Fix me to use options
        
        logger_.info("Running testMain...");
        
        Tree2.main(new String[] {FileUtil.getTempDir().getAbsolutePath()});
    }
    

    /**
     * Tests the max depth switch.
     * 
     * @throws Exception on error.
     */
    public void testMaxDepth() throws Exception
    {
        logger_.info("Running testMaxDepth...");
        
        // Setup ===============================================================
        
        File depth1 = new File(rootTestDir_, "depth1");
        assertTrue(depth1.mkdir());

        File depth2 = new File(depth1, "depth2");
        assertTrue(depth2.mkdir());

        File depth3 = new File(depth2, "depth3");
        assertTrue(depth3.mkdir());

        // Depth = 1 ===========================================================
        
        StringWriter sw = new StringWriter();
        
        Tree2 tree = new Tree2(
            rootTestDir_, 
            false, 
            false, 
            false, 
            false, 
            Tree2.SORT_NONE, 
            "", 
            1, 
            sw);
        
        tree.showTree();
        
        logger_.debug(
            "Max depth should be 1:" 
            + StringUtil.banner(sw.toString()));

        assertTrue(sw.toString().indexOf("depth1") >= 0);
        assertTrue(sw.toString().indexOf("depth2") < 0);
        assertTrue(sw.toString().indexOf("depth3") < 0);

        // Depth = 2 ===========================================================
        
        sw = new StringWriter();
        
        tree = new Tree2(
            rootTestDir_, 
            false, false, false, false, Tree2.SORT_NONE, "", 2, sw);
        
        tree.showTree();
        
        logger_.debug(
            "Max depth should be 2:" 
            + StringUtil.banner(sw.toString()));
        
        assertTrue(sw.toString().indexOf("depth1") >= 0);
        assertTrue(sw.toString().indexOf("depth2") >= 0);
        assertTrue(sw.toString().indexOf("depth3") < 0);
        
        // Depth = unlimited ===================================================
        
        sw = new StringWriter();
        
        tree = new Tree2(
            rootTestDir_, false, false, false, false, Tree2.SORT_NONE, "", 99,
            sw);
        
        tree.showTree();
        
        logger_.debug(
            "Max depth should be unlimited:" 
            + StringUtil.banner(sw.toString()));
            
        assertTrue(sw.toString().indexOf("depth1") >= 0);
        assertTrue(sw.toString().indexOf("depth2") >= 0);
        assertTrue(sw.toString().indexOf("depth3") >= 0);
    }
    
    
    /**
     * Tests the max depth switch for an invalid value.
     * 
     * @throws Exception on error.
     */
    public void testMaxDepthInvalid() throws Exception
    {
        logger_.info("Running testMaxDepthInvalid...");
        
        try 
        {
        	new Tree2(
                rootTestDir_, 
                false, 
                false, 
                false, 
                false, 
                Tree2.SORT_NONE, 
                "", 
                0, 
                new NullWriter());
         
            fail("Max depth of zero should have choked");
        }
        catch (IllegalArgumentException iae)
        {
        	// Success
        }
    }

    
    /**
     * Tests the fullpath (-p) switch
     * 
     * @throws Exception on error.
     */
    public void testShowFullPath() throws Exception
    {
        logger_.info("Running testShowFullPath...");
        
        // Setup ===============================================================
        
        File dir1 = new File(rootTestDir_, "dir1");
        assertTrue(dir1.mkdir());
        createFile(dir1);
        StringWriter sw = new StringWriter();
        
        // Tree with fullpath ==================================================
        
        Tree2 tree = new Tree2(
            rootTestDir_, 
            true,  // show files 
            false, false, 
            true,  // fulll path
            Tree2.SORT_NONE, "", Integer.MAX_VALUE, sw);
        
        tree.showTree();

        logger_.debug(
            "Full paths should be showing:" 
            + StringUtil.banner(sw.toString()));

        // Verify ==============================================================
        
        // Root dir substring should occur in of the following: 
        //  1. root
        //  2. dir1
        //  3. file1
        
        assertEquals(3, 
            StringUtils.countMatches(
                sw.toString(), 
                rootTestDir_.getAbsolutePath()));
    }
    
    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------

    /**
     * Creates a temp file in the given directory.
     * 
     * @param dir Dir to create file in.
     * @return Temp file.
     * @throws IOException on I/O error.
     */
    protected File createFile(File dir) throws IOException
    {
        File f = FileUtil.createTempFile(dir);
        FileUtil.setFileContents(f, 
            RandomUtil.nextString(RandomUtil.nextInt(100, 10000)), false);
        return f;
    }

    
    /**
     * Executes the native version of tree to use as a comparison.
     * 
     * @param dir Directory.
     * @throws IOException on error.
     */
    public void printNativeTree(File dir) throws IOException
    {
        /*
        Process p = Runtime.getRuntime().exec(
            "tree.com /a " + dir.getAbsolutePath());
            
        InputStream is = p.getInputStream();
        String output = StreamUtil.asString(is);
        
        output = output.substring(
            output.indexOf("\n", output.indexOf("\n") + 1));
            
        System.out.println(output);
        */
    }

    
    /**
     * Executes the native version of tree to use as a comparison.
     * 
     * @param dir Directory.
     * @throws IOException on IO error.
     */
    public void printNativeFileTree(File dir) throws IOException
    {
        /*
        Process p = Runtime.getRuntime().exec(
            "tree.com /a /f " + dir.getAbsolutePath());
            
        InputStream is = p.getInputStream();
        String output = StreamUtil.asString(is);
        
        output = output.substring(
            output.indexOf("\n", output.indexOf("\n") + 1));
            
        System.out.println(output);
        */
    }
}