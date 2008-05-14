package toolbox.dirmon;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.dirmon.DirSnapshot;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.IFileActivityRecognizer;
import toolbox.util.dirmon.recognizer.FileChangedRecognizer;

/**
 * Unit test for {@link toolbox.dirmon.DropUselessEventsRecognizer}.
 */
public class DropUselessEventsRecognizerTest extends TestCase {

    private static final Logger logger = Logger.getLogger(DropUselessEventsRecognizerTest.class);
    
    private File tempDir;
    
    // -------------------------------------------------------------------------
    // Main 
    // -------------------------------------------------------------------------
    
    public static void main(String[] args) {
        TestRunner.run(DropUselessEventsRecognizerTest.class);
    }

    // -------------------------------------------------------------------------
    // Setup/TearDown
    // -------------------------------------------------------------------------

    protected void setUp() throws Exception {
        tempDir = FileUtil.createTempDir();
    }

    protected void tearDown() throws Exception {
        FileUtils.forceDelete(tempDir);
    }
    
    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------
    
    public void testDropDirectoryTimestampChanged() throws Exception {
        logger.info("Running testDropDirectoryTimestampChanged ...");

        // Setup
        File dirChanged = FileUtil.createTempDir(tempDir);
        logger.debug("Timestamp before: " + dirChanged.lastModified());
        DirectoryMonitor monitor = new DirectoryMonitor(tempDir);
        
        IFileActivityRecognizer defaultRecognizer = new FileChangedRecognizer(monitor);
        IFileActivityRecognizer dropRecognizer = new DropUselessEventsRecognizer(defaultRecognizer);
        
        DirSnapshot beforeDirSnapshot = new DirSnapshot(tempDir);
        ThreadUtil.sleep(1500);
        FileUtils.touch(dirChanged);
        logger.debug("Timestamp after: " + dirChanged.lastModified());
        DirSnapshot afterDirSnapshot = new DirSnapshot(tempDir);
        
        // Test
        List unfilteredEvents = defaultRecognizer.getRecognizedEvents(beforeDirSnapshot, afterDirSnapshot);
        List filteredEvents = dropRecognizer.getRecognizedEvents(beforeDirSnapshot, afterDirSnapshot);
        
        // Verify
        assertEquals(1, unfilteredEvents.size());
        assertEquals(0, filteredEvents.size());
    }
    
    
    public void testDropTempFile_Changed_Created_Deleted() throws Exception {
        logger.info("Running testDropTempFileChanged ...");
        
        // Setup
        DirectoryMonitor monitor = new DirectoryMonitor(tempDir);
        
        IFileActivityRecognizer defaultRecognizer = new FileChangedRecognizer(monitor);
        IFileActivityRecognizer dropRecognizer = new DropUselessEventsRecognizer(defaultRecognizer);

        File tempFileChanged = new File(tempDir, "~dropme_filechanged");
        FileUtils.writeStringToFile(tempFileChanged, "contents before", null);
        DirSnapshot beforeDirSnapshot = new DirSnapshot(tempDir);
        
        FileUtils.writeStringToFile(tempFileChanged, "contents after", null);
        DirSnapshot afterDirSnapshot = new DirSnapshot(tempDir);
        
        // Test
        List unfilteredEvents = defaultRecognizer.getRecognizedEvents(beforeDirSnapshot, afterDirSnapshot);
        List filteredEvents = dropRecognizer.getRecognizedEvents(beforeDirSnapshot, afterDirSnapshot);
        
        // Verify
        assertEquals(1, unfilteredEvents.size());
        assertEquals(0, filteredEvents.size());
    }
}