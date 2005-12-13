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
    
    public void testParse() throws Exception {
        logger_.info("Running testParse...");
        
        MovieInfoParser parser = new MovieInfoParser();
        MovieInfo movie = parser.parse(getTestFilename());
       //logger_.debug("\n" + StringUtil.banner(movie.toString()));
        logger_.debug("\n\n" + movie.toString());
    }
}
