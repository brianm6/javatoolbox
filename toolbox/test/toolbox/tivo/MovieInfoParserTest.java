package toolbox.tivo;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ClassUtil;

public class MovieInfoParserTest extends AbstractTestCase {

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

        if ( ClassUtil.findInPath("ffmpeg") == null || ClassUtil.findInPath("ffmpeg.exe") == null) {
            logger_.info("Skipping test...ffmpeg not found on path");
            return;
        }

        // Setup
        // =====================================================================
        MovieInfo expected = new MovieInfo();
        expected.setDuration("00:01:37.1");
        expected.setBitrate(new Integer(203));
        expected.setFilename(getH264Filename());

        AudioStreamInfo audio = new AudioStreamInfo();
        audio.setBitrate(new Integer(32));
        audio.setFormat("mp3");
        audio.setHertz(new Integer(22050));
        audio.setChannels("mono");
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
        assertEquals(expected.getFileSize(), movie.getFileSize());
        assertEquals(expected.getAudioStream(), movie.getAudioStream());
        assertEquals(expected.getVideoStream(), movie.getVideoStream());
    }
    
    public void testParseVideoLine1() throws Exception {
        logger_.info("Running testParseVideoLine1...");
        
        String line = "  Stream #0.0, 25.00 fps(r): Video: h264, yuv420p, 416x304";
        MovieInfo info = new MovieInfo();
        MovieInfoParser p = new MovieInfoParser();
        p.parseVideoLine(line, info);
        
        assertEquals("h264", info.getVideoStream().getFormat());
        assertEquals(new Integer(416), info.getVideoStream().getWidth());
        assertEquals(new Integer(304), info.getVideoStream().getHeight());
        assertEquals("25.00", info.getVideoStream().getFramesPerSecond());
    }
    
    public void testParseVideoLine2() throws Exception {
        logger_.info("Running testParseVideoLine2...");
        
        String line = "  Stream #0.0  Id:   0: Video: mpeg4, 416x304, 25.00 fps";
        MovieInfo info = new MovieInfo();
        MovieInfoParser p = new MovieInfoParser();
        p.parseVideoLine(line, info);
        
        assertEquals("mpeg4", info.getVideoStream().getFormat());
        assertEquals(new Integer(416), info.getVideoStream().getWidth());
        assertEquals(new Integer(304), info.getVideoStream().getHeight());
        assertEquals("25.00", info.getVideoStream().getFramesPerSecond());
    }
    
    public void testParseVideoLine3() throws Exception {
        logger_.info("Running testParseVideoLine3...");
        
        String line = "  Stream #0.0: Video: h264, 416x304, 25.00 fps";
        MovieInfo info = new MovieInfo();
        MovieInfoParser p = new MovieInfoParser();
        p.parseVideoLine(line, info);
        
        assertEquals("h264", info.getVideoStream().getFormat());
        assertEquals(new Integer(416), info.getVideoStream().getWidth());
        assertEquals(new Integer(304), info.getVideoStream().getHeight());
        assertEquals("25.00", info.getVideoStream().getFramesPerSecond());
    }

    
    public void testParseAudioLine1() throws Exception {
        logger_.info("Running testParseAudioLine1...");
        
        String line = "  Stream #0.1  Id:   1: Audio: mp3, 22050 Hz, stereo, 47 kb/s";
        MovieInfo info = new MovieInfo();
        MovieInfoParser p = new MovieInfoParser();
        p.parseAudioLine(line, info);
        
        assertEquals("mp3", info.getAudioStream().getFormat());
        assertEquals(new Integer(22050), info.getAudioStream().getHertz());
        assertEquals(new Integer(47), info.getAudioStream().getBitrate());
        assertEquals("stereo", info.getAudioStream().getChannels());
    }
    
    public void testParseAudioLine2() throws Exception {
        logger_.info("Running testParseAudioLine2...");
        
        String line = "  Stream #0.1: Audio: mp3, 22050 Hz, mono, 31 kb/s";
        MovieInfo info = new MovieInfo();
        MovieInfoParser p = new MovieInfoParser();
        p.parseAudioLine(line, info);
        
        assertEquals("mp3", info.getAudioStream().getFormat());
        assertEquals(new Integer(22050), info.getAudioStream().getHertz());
        assertEquals(new Integer(31), info.getAudioStream().getBitrate());
        assertEquals("mono", info.getAudioStream().getChannels());
    }
    
    public void testParseAudioLine3() throws Exception {
        logger_.info("Running testParseAudioLine3...");
        
        String line = "  Stream #0.1: Audio: aac, 48000 Hz, stereo";
        MovieInfo info = new MovieInfo();
        MovieInfoParser p = new MovieInfoParser();
        p.parseAudioLine(line, info);
        
        assertEquals("aac", info.getAudioStream().getFormat());
        assertEquals(new Integer(48000), info.getAudioStream().getHertz());
        assertEquals(null, info.getAudioStream().getBitrate());
        assertEquals("stereo", info.getAudioStream().getChannels());
    }
}