package toolbox.tivo;

import java.io.File;

import junit.framework.TestCase;

/**
 * AbstractTestCase is TivoConverter related test cases. Provides easy access
 * to a small movie file to be used as an input to transcoding unit tests.
 */
public abstract class AbstractTestCase extends TestCase {

    public AbstractTestCase() {
    }

    
    private String getTestResourceDir() {
        return 
            System.getProperty("user.dir")  
            + File.separator 
            + "test" 
            + File.separator 
            + "toolbox" 
            + File.separator 
            + "tivo" 
            + File.separator;

    }
    
    
    protected String getTestFilename() {
        return getTestResourceDir() + "h264.avi";
    }
    
    
    protected String getH264Filename() {
        return getTestResourceDir() + "h264.avi";
    }
}