package toolbox.tivo;

import java.io.File;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ClassUtil;
import toolbox.util.FileUtil;

/**
 * Unit test for {@link toolbox.tivo.FFMpegTranscoder}.
 */
public class FFMpegTranscoderTest extends AbstractTestCase {

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
        
        if ((ClassUtil.findInPath("ffmpeg") == null) &&
            (ClassUtil.findInPath("ffmpeg.exe") == null)) {
        	logger_.debug("Skipping test..ffmpeg not on system path");
        	return;
        }
        
        // Setup
        // =====================================================================
        MovieInfoParser parser = new MovieInfoParser();
        MovieInfo input = parser.parse(getH264Filename());
        String destFilename = 
            FileUtil.getTempDir() + File.separator + "tivoH264.mpg";

        // Test
        // =====================================================================
        ITranscoder transcoder = 
            new FFMpegTranscoder(FileUtil.getTempDir().getAbsolutePath());
        
        transcoder.transcode(input, destFilename);
        
        // Verify
        // =====================================================================
        assertTrue(new File(destFilename).exists());
        MovieInfo result = parser.parse(destFilename);
        logger_.debug(result);

        assertNotNull(result.getDuration());
        assertEquals(destFilename, result.getFilename());
        assertTrue(input.getBitrate().intValue() <= result.getBitrate().intValue());
        assertTrue(input.getFileSize().longValue() <= result.getFileSize().longValue());
        assertEquals(TivoStandards.AUDIO_128, result.getAudioStream());
        assertEquals(TivoStandards.VIDEO_720_480, result.getVideoStream());
    }
}