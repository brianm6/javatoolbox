package toolbox.tree;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;

/**
 * Unit test for Tree.
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
    private File rootDir_;

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
     * Create a temp directory to play around in.
     * 
     * @throws IOException on I/O error.
     */
    public void setUp() throws IOException
    {
        System.out.println(StringUtils.repeat("=", 80));
        rootDir_ = FileUtil.createTempDir();
    }

    
    /**
     * Removes the temp directory.
     */
    public void tearDown()
    {
        FileUtil.removeDir(rootDir_);
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
        
        Tree2 t6 = new Tree2(FileUtil.getTempDir(), true, true, Tree2.SORT_NAME);
        assertNotNull(t6);

        Tree2 t7 = new Tree2(
            FileUtil.getTempDir(), true, true, true, Tree2.SORT_NAME, ".*");
        
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
        logger_.info("Tree with cascading dirs: \n");
        
        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (b, "c");
        assertTrue(c.mkdir());
        
        File d = new File(c, "d");
        assertTrue(d.mkdir());
        
        Tree2 tree = new Tree2(rootDir_);
        tree.showTree();
        
        printNativeTree(rootDir_);
    }

    
    /**
     * Tests for a simple flat structure.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeSimpleFlat() throws Exception
    {
        logger_.info("Running testShowTreeSimpleFlat...");
        logger_.info("Tree with flat struct at level 2: \n");
        
        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (a, "c");
        assertTrue(c.mkdir());
        
        File d = new File(a, "d");
        assertTrue(d.mkdir());
        
        Tree2 tree = new Tree2(rootDir_);
        tree.showTree();
        
        printNativeTree(rootDir_);
    }

    
    /**
     * Tests for an extension bar.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeExtensionBar() throws Exception
    {
        logger_.info("Running testShowTreeExtensionBar...");
        logger_.info("Tree with an extension bar: \n");
                
        // Create rootDir_\a\b\c\d 
        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (b, "c");
        assertTrue(c.mkdir());
        
        File d = new File(a, "d");
        assertTrue(d.mkdir());
        
        Tree2 tree = new Tree2(rootDir_);
        tree.showTree();
        
        printNativeTree(rootDir_);
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
        logger_.info("Tree with > 1 dir in root: \n");
                
        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (rootDir_, "c");
        assertTrue(c.mkdir());
        
        File d = new File(c, "d");
        assertTrue(d.mkdir());
        
        Tree2 tree = new Tree2(rootDir_);
        tree.showTree();
        
        printNativeTree(rootDir_);
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
        logger_.info("Tree with an empty root: \n");
        Tree2 tree = new Tree2(rootDir_);
        tree.showTree();
        
        printNativeTree(rootDir_);
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
        logger_.info("Tree with a single directory: \n");

        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        Tree2 tree = new Tree2(rootDir_);
        tree.showTree();
        
        printNativeTree(rootDir_);
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
        logger_.info("Running testShowTreeSimpleCascadeFile...");
        logger_.info("Tree2 with cascading dirs and one file: \n");
        
        createFile(rootDir_);
        
        File a = new File(rootDir_, "a");
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
               
        Tree2 tree = new Tree2(rootDir_, true);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }

    
    /**
     * Tests for an empty tree with files in the root only.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeRootFiles() throws Exception
    {
        logger_.info("Running testShowTreeRootFiles...");
        logger_.info("Tree with cascading dirs and one file: \n");
        
        createFile(rootDir_);
        createFile(rootDir_);
        createFile(rootDir_);
        createFile(rootDir_);
        createFile(rootDir_);
               
        Tree2 tree = new Tree2(rootDir_, true);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
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
        FileUtil.setFileContents(f, "testing", false);
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