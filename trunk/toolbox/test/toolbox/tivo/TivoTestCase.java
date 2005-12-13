package toolbox.tivo;

import junit.framework.TestCase;


public abstract class TivoTestCase extends TestCase {

    public TivoTestCase() {
    }
    
    protected String getTestFilename() {
        return "c:\\workspaces\\workspace-toolbox\\toolbox\\test\\toolbox\\tivo\\h264.avi";
    }
}
