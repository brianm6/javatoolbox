package toolbox.tivo;

import java.io.File;

import junit.framework.TestCase;


public abstract class TivoTestCase extends TestCase {

    public TivoTestCase() {
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
