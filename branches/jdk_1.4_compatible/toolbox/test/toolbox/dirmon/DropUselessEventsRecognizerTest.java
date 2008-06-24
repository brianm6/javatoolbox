package toolbox.dirmon;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.dirmon.DirSnapshot;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.IFileActivityRecognizer;
import toolbox.util.dirmon.recognizer.FileChangedRecognizer;

/**
 * Unit test for {@link toolbox.dirmon.DropUselessEventsRecognizer}.
 */
public class DropUselessEventsRecognizerTest extends TestCase {

    private static final Logger logger = 
        Logger.getLogger(DropUselessEventsRecognizerTest.class);
    
    // -------------------------------------------------------------------------
    // Main 
    // -------------------------------------------------------------------------
    
    public static void main(String[] args) {
        TestRunner.run(DropUselessEventsRecognizerTest.class);
    }

    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------
    
    public void testDropNone() throws Exception {
        logger.info("Running testDropNone ...");
        
    }
    
    
    public void testDropDirectoryTimestampChanged() throws Exception {
        logger.info("Running testDropDirectoryTimestampChanged ...");

        File tempDir = null;
        
        try {
            
            // Setup
            tempDir = FileUtil.createTempDir();
            File dirChanged = FileUtil.createTempDir(tempDir);
            DirectoryMonitor monitor = new DirectoryMonitor(tempDir);
            
            IFileActivityRecognizer defaultRecognizer = new FileChangedRecognizer(monitor);
            IFileActivityRecognizer dropRecognizer = new DropUselessEventsRecognizer(defaultRecognizer);
            
            DirSnapshot beforeDirSnapshot = new DirSnapshot(tempDir);
            FileUtils.touch(dirChanged);
            DirSnapshot afterDirSnapshot = new DirSnapshot(tempDir);
            
            
            // Test
            List unfilteredEvents = defaultRecognizer.getRecognizedEvents(beforeDirSnapshot, afterDirSnapshot);
            List filteredEvents = dropRecognizer.getRecognizedEvents(beforeDirSnapshot, afterDirSnapshot);
            
            
            // Verify
            assertEquals(1, unfilteredEvents.size());
            assertEquals(0, filteredEvents.size());
        }
        finally {
            FileUtils.forceDelete(tempDir);
        }
    }
    
    
    public void testDropTempFile_Changed_Created_Deleted() throws Exception {
        logger.info("Running testDropTempFileChanged ...");
        
        File tempDir = null;
        
        try {
            // Setup
            tempDir = FileUtil.createTempDir();
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
        finally {
            FileUtils.forceDelete(tempDir);
        }
    }
}