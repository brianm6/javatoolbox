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

        // Setup
        // =====================================================================
        MovieInfo expected = new MovieInfo();
        expected.setDuration("00:01:37.1");
        expected.setBitrate(new Integer(203));
        expected.setFilename(getH264Filename());

        AudioStreamInfo audio = new AudioStreamInfo();
        audio.setBitrate(new Integer(31));
        audio.setFormat("mp3");
        audio.setHertz(new Integer(22050));
        audio.setStereo(false);
        expected.setAudioStream(audio);

        VideoStreamInfo video = new VideoStreamInfo();
        video.setFormat("h264");
        video.setFramesPerSecond("25.00");
        video.setHeight(new Integer(304));
        video.setWidth(new Integer(416));
        expected.setVideoStream(video);

        // Test
        // =====================================================================
        MovieInfoParser parser = new MovieInfoParser();
        MovieInfo movie = parser.parse(getH264Filename());
        logger_.debug("\n\n" + movie.toString());

        // Verify
        // =====================================================================
        assertEquals(expected.getDuration(), movie.getDuration());
        assertEquals(expected.getFilename(), movie.getFilename());
        assertEquals(expected.getBitrate(), movie.getBitrate());
        assertEquals(expected.getLength(), movie.getLength());
        assertEquals(expected.getAudioStream(), movie.getAudioStream());
        assertEquals(expected.getVideoStream(), movie.getVideoStream());
    }
}