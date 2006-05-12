package toolbox.tree;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;
import toolbox.util.file.FileComparator;

/**
 * Unit test for {@link toolbox.tree.Tree}.
 */
public class TreeTest extends TestCase {
    
    private static final Logger logger_ = Logger.getLogger(TreeTest.class);
    
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
    
    public static void main(String[] args) {
        TestRunner.run(TreeTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides junit.framework.TestCase
    //--------------------------------------------------------------------------
    
    /**
     * Create a temp directory to play around in.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws IOException{
        System.out.println(StringUtils.repeat("=", 80));
        rootDir_ = FileUtil.createTempDir();
    }

    
    /**
     * Removes the temp directory.
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(rootDir_);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests for a simple cascading structure.
     * 
     * @throws Exception on error.
     */
    public void testShowTree_Cascade_Dirs() throws Exception {
        logger_.info("Running testShowTree_Cascade_Dirs...");
        logger_.debug("Tree with cascading dirs: \n");
        
        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (b, "c");
        assertTrue(c.mkdir());
        
        File d = new File(c, "d");
        assertTrue(d.mkdir());
        
        Tree tree = new Tree(rootDir_);
        tree.showTree();
        
        printNativeTree(rootDir_);
    }

    
    /**
     * Tests for a simple flat structure.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeSimpleFlat() throws Exception {
        logger_.info("Running testShowTreeSimpleFlat...");
        logger_.debug("Tree with flat struct at level 2: \n");
        
        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (a, "c");
        assertTrue(c.mkdir());
        
        File d = new File(a, "d");
        assertTrue(d.mkdir());
        
        Tree tree = new Tree(rootDir_);
        tree.showTree();
        
        printNativeTree(rootDir_);
    }

    
    /**
     * Tests for an extension bar.
     * 
     * @throws Exception on error.
     */
    public void testShowTreeExtensionBar() throws Exception {
        logger_.info("Running testShowTreeExtensionBar...");
        logger_.debug("Tree with an extension bar: \n");
                
        // Create rootDir_\a\b\c\d 
        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (b, "c");
        assertTrue(c.mkdir());
        
        File d = new File(a, "d");
        assertTrue(d.mkdir());
        
        Tree tree = new Tree(rootDir_);
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
    public void testShowTree_Root_Dirs_Many() throws Exception {
        logger_.info("Running testShowTree_Root_Dirs_Many...");
        logger_.debug("Tree with > 1 dir in root: \n");
                
        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        
        File c = new File (rootDir_, "c");
        assertTrue(c.mkdir());
        
        File d = new File(c, "d");
        assertTrue(d.mkdir());
        
        Tree tree = new Tree(rootDir_);
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
    public void testShowTree_Root_Dirs_Zero() throws Exception {
        logger_.info("Running testShowTree_Root_Dirs_Zero...");
        logger_.debug("Tree with an empty root: \n");
        Tree tree = new Tree(rootDir_);
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
    public void testShowTree_Root_Dirs_One() throws Exception {
        logger_.info("Running testShowTree_Root_Dirs_One...");
        logger_.debug("Tree with a single directory: \n");

        File a = new File(rootDir_, "a");
        assertTrue(a.mkdir());
        
        Tree tree = new Tree(rootDir_);
        tree.showTree();
        
        printNativeTree(rootDir_);
    }

    
    /**
     * Tests for a large directory structure.
     * 
     * @throws Exception on error.
     */
    public void xtestShowTreeLargeTree() throws Exception {
        logger_.info("Running xtestShowTreeLargeTree...");
        Tree tree = new Tree(new File("/"));
        tree.showTree();
        
        // printNativeTree(rootDir_);
    }


    /**
     * Tests for a simple cascading structure with one file.
     * 
     * @throws Exception on error.
     */
    public void testShowTree_Cascade_DirAndFile() throws Exception {
        logger_.info("Running testShowTree_Cascade_DirAndFile...");
        logger_.debug("Tree with cascading dirs and one file: \n");
        
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
        
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }

    
    /**
     * Tests for an empty tree with files in the root only.
     * 
     * @throws Exception on error.
     */
    public void testShowTree_Root_Files_Many() throws Exception {
        logger_.info("Running testShowTree_Root_Files_Many...");
        logger_.debug("Tree with cascading dirs and one file: \n");
        
        createFile(rootDir_);
        createFile(rootDir_);
        createFile(rootDir_);
        createFile(rootDir_);
        createFile(rootDir_);
               
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }

    
    /**
     * Tests printing the help/usage information.
     * 
     * @throws Exception on error.
     */
    public void testPrintUsage() throws Exception {
        logger_.info("Running testPrintUsage...");
        
        // Send in an invalid flag so usage information is shown
        Tree.main(new String[] {"-xyz"});
    }
    
    
    /**
     * Tests execution via main().
     * 
     * @throws Exception on error.
     */
    public void xxxtestMain() throws Exception {
        // TODO: Fix me to use options when upgrading to cli2
        
        logger_.info("Running testMain...");
        
        Tree.main(new String[] {"-os", FileUtil.getTempDir().getAbsolutePath()});
    }
    

    public void testShowTree_Show_FileSizes() throws Exception {
        logger_.info("Running testShowTree_Show_FileSizes...");
        logger_.debug("Tree with file sizes: \n");
        
        createFile(rootDir_);
        createRandomTree(rootDir_);
        
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        config.setShowFilesize(true);
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }

    public void testShowTree_Show_FileDates() throws Exception {
        logger_.info("Running testShowTree_Show_FileDates...");
        logger_.debug("Tree with file dates: \n");
        
        createFile(rootDir_);
        createRandomTree(rootDir_);
        
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        config.setShowFileDate(true);
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }

    public void testShowTree_Show_FileDateAndSize() throws Exception {
        logger_.info("Running testShowTree_Show_FileDateAndSize...");
        logger_.debug("Tree with file dates and size: \n");
        
        createFile(rootDir_);
        createRandomTree(rootDir_);
        
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        config.setShowFileDate(true);
        config.setShowFilesize(true);
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }

    public void testShowTree_Show_FileDateAndSize_Sort_Size() throws Exception {
        logger_.info("Running testShowTree_Show_FileDateAndSize_Sort_Size...");
        logger_.debug("Tree with file dates and size sorted by size: \n");
        
        createFile(rootDir_);
        createRandomTree(rootDir_);
        
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        config.setShowFileDate(true);
        config.setShowFilesize(true);
        config.setSortBy(FileComparator.COMPARE_SIZE);
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }
    
    public void testShowTree_Sort_FileSize() throws Exception {
        logger_.info("Running testShowTree_Sort_FileSize...");
        logger_.debug("Tree sorted by file size: \n");
        
        createFile(rootDir_);
        createRandomTree(rootDir_);
        
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        config.setShowFilesize(true);
        config.setSortBy(FileComparator.COMPARE_SIZE);
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }

    public void testShowTree_Sort_FileName() throws Exception {
        logger_.info("Running testShowTree_Sort_FileName...");
        logger_.debug("Tree sorted by file name: \n");
        
        createFile(rootDir_);
        createRandomTree(rootDir_);
        
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        config.setShowFilesize(true);
        config.setSortBy(FileComparator.COMPARE_NAME);
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }

    public void testShowTree_Sort_Date() throws Exception {
        logger_.info("Running testShowTree_Sort_Date...");
        logger_.debug("Tree sorted by file date: \n");
        
        createFile(rootDir_);
        createRandomTree(rootDir_);
        
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        config.setShowFilesize(true);
        config.setSortBy(FileComparator.COMPARE_DATE);
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }

    public void testShowTree_Regex_Match() throws Exception {
        logger_.info("Running testShowTree_Regex_Match...");
        logger_.debug("Tree matching regex - files containing 2: \n");
        
        createFile(rootDir_);
        createRandomTree(rootDir_);
        
        TreeConfig config = new TreeConfig();
        config.setShowFiles(true);
        config.setRegexFilter("2");
        Tree tree = new Tree(rootDir_, config);
        tree.showTree();
        
        printNativeFileTree(rootDir_);
    }
    
    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------

    private void createRandomTree(File root) throws IOException{
        File a = new File(root, "a");
        assertTrue(a.mkdir());
        createSomeFiles(a, 5);
                
        File b = new File(a, "b");
        assertTrue(b.mkdir());
        createSomeFiles(b, 5);
        
        File c = new File (b, "c");
        assertTrue(c.mkdir());
        createSomeFiles(c, 5);
        
        File d = new File(c, "d");
        assertTrue(d.mkdir());
        createSomeFiles(d, 5);
    }
    
    private void createSomeFiles(File a, int max) throws IOException {
        int n = RandomUtil.nextInt(1, max);
        for (int i = 0; i < n; i++)
            createFile(a);
    }

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
        FileUtil.setFileContents(f, StringUtils.repeat("testing", RandomUtil.nextInt(1, 1000)), false);
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
//        Process p = Runtime.getRuntime().exec("tree.com /a " + dir.getAbsolutePath());
//        InputStream is = p.getInputStream();
//        String output = IOUtils.toString(is);
//        output = output.substring(output.indexOf("\n", output.indexOf("\n") + 1));
//        System.out.println(output);
    }

    
    /**
     * Executes the native version of tree to use as a comparison.
     * 
     * @param dir Directory.
     * @throws IOException on IO error.
     */
    public void printNativeFileTree(File dir) throws IOException
    {
//        Process p = Runtime.getRuntime().exec("tree.com /a /f " + dir.getAbsolutePath());
//        InputStream is = p.getInputStream();
//        String output = IOUtils.toString(is);
//        output = output.substring(output.indexOf("\n", output.indexOf("\n") + 1));
//        System.out.println(output);
    }
}