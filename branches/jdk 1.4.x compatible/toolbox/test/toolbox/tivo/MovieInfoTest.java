package toolbox.tivo;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class MovieInfoTest extends TestCase{

    private static final Logger logger_ = 
        Logger.getLogger(MovieInfoTest.class);

    public static void main(String[] args){
        TestRunner.run(MovieInfoTest.class);
    }

    public void testHMS() {
        logger_.info("Running testHMS...");
        
        MovieInfo info = new MovieInfo();
        
        info.setDuration("00:01:37.1");
        
        assertEquals(0, info.getHours());
        assertEquals(1, info.getMinutes());
        assertEquals(37, info.getSeconds());
    }
}
