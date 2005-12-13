package toolbox.tivo;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

public class MovieInfoParserTest extends TestCase {

    private static final Logger logger_ = 
        Logger.getLogger(MovieInfoParserTest.class);

    
    public static final void main(String args[]) {
        TestRunner.run(MovieInfoParserTest.class);
    }
    
    
    public void testParse() throws Exception {
        logger_.info("Running testParse...");
        
        MovieInfoParser parser = new MovieInfoParser();
        
        Movie movie = parser.parse("z:\\tivo\\incoming\\movie.avi");
        logger_.debug(movie);
        
        
        //Movie movie = parser.parse("c:\\movie.avi");
    }
}
