package toolbox.tivo;

import java.io.File;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;

/**
 * Unit test for {@link toolbox.tivo.FFMpegTranscoder}.
 */
public class FFMpegTranscoderTest extends TivoTestCase {

    private static final Logger logger_ = 
        Logger.getLogger(FFMpegTranscoderTest.class);

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static void main(String[] args) {
        TestRunner.run(FFMpegTranscoderTest.class);
    }

    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------
    
    public void testTranscode_H264() throws Exception {
        logger_.info("Running testTranscode...");
        
        MovieInfoParser parser = new MovieInfoParser();
        MovieInfo info = parser.parse(getH264Filename());
        
        String destFilename = 
            FileUtil.getTempDir() + File.separator + "tivoH264.mpg";
        
        ITranscoder transcoder = new FFMpegTranscoder();
        transcoder.transcode(info, destFilename);
        
        MovieInfo result = parser.parse(destFilename);
        
        logger_.info(result);
    }
}