package toolbox.tivo;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

public class MovieInfoParserTest extends TivoTestCase {

    private static final Logger logger_ = 
        Logger.getLogger(MovieInfoParserTest.class);

    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static final void main(String args[]) {
        TestRunner.run(MovieInfoParserTest.class);
    }
    
    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------
    
    public void testParse_H264() throws Exception {
        logger_.info("Running testParse_H264...");
        
        MovieInfoParser parser = new MovieInfoParser();
        MovieInfo movie = parser.parse(getH264Filename());
        logger_.debug("\n\n" + movie.toString());
    }
}
