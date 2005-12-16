package toolbox.tivo;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.ProcessUtil;
import toolbox.util.StringUtil;


public class MovieInfoParser {
    
    static public final Logger logger_ = 
        Logger.getLogger(MovieInfoParser.class);
    
    // -------------------------------------------------------------------------
    // Public 
    // -------------------------------------------------------------------------
    
    public MovieInfo parse(String filename) throws Exception {
    
        String command = "c:\\bin\\ffmpeg -i \"" + filename + "\"";
        logger_.debug("Command: " + command);
        
        Process p = Runtime.getRuntime().exec(command);
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();
        int exitValue = ProcessUtil.getProcessOutput(p, stdout, stderr);
        
        logger_.debug(StringUtil.banner("Output: \n" + stdout));
        logger_.debug(StringUtil.banner("Error: \n" + stderr));
        
        try {
            String[] lines = StringUtil.tokenize(stderr.toString(), "\n");
            MovieInfo movie = new MovieInfo();
            movie.setFilename(filename);
            boolean found = false;
            
            for (int i = 0; i < lines.length; i++) {
                
                if (lines[i].startsWith("Input #0")) {
                    parseLine1(lines[i+1], movie);    
                    parseVideoLine(lines[i+2], movie);
                    parseAudioLine(lines[i+3], movie);
                    found = true;
                    break;
                }
            }
            if (found)
                logger_.debug("Success!");
            
            return movie;

        }
        catch (Exception e) {
            logger_.error(
                "Query info error:" 
                + e.getMessage()
                + "\n\nstdout:\n" 
                + StringUtil.indent(stdout.toString()) 
                + "\n\nstderr:\n" 
                + StringUtil.indent(stderr.toString()), e);
            
            throw e;
        }
        
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------
    
    private void parseAudioLine(String line, MovieInfo movie) {
        
        // Stream #0.1  Id:   1: Audio: mp3, 22050 Hz, stereo, 47 kb/s
        // Stream #0.1: Audio: mp3, 22050 Hz, mono, 31 kb/s
        // Stream #0.1: Audio: aac, 48000 Hz, stereo
        
        AudioStreamInfo audio = new AudioStreamInfo();
        String[] tokens = StringUtils.split(line);
        
        int i = 0;
        
        //Assert.assertEquals(
        //    "Expected 11 tokens for audio line: " + line, 11, tokens.length);
        
        Assert.assertEquals(
            "Expected 'Stream' as first token",
            "Stream",
            tokens[i++]);

        Assert.assertEquals(
            "Expected '#0.1' as 2nd token",
            "#0.1",
            StringUtils.chomp(tokens[i++], ":"));
        
        audio.setNumber("0.1");
        
//        Assert.assertEquals(
//            "Expected 'Id:' as 3rd token",
//            "Id:",
//            tokens[2]);
        
        audio.setId("1");
        
//        Assert.assertEquals(
//            "Expected '1:' as 4th token",
//            "1:",
//            tokens[3]);
        
        
        Assert.assertEquals(
            "Expected 'Audio:' as 5th token",
            "Audio:",
            tokens[i++]);
        
        audio.setFormat(StringUtils.chomp(tokens[i++], ","));
        audio.setHertz(new Integer(tokens[i++]));
        
        Assert.assertEquals("Expected 'Hz,' as 8th token", "Hz,", tokens[i++]);
        
        audio.setStereo(tokens[i++].equals("stereo,"));
        
        try {
            audio.setBitrate(new Integer(tokens[i++]));
            Assert.assertEquals("11th token", "kb/s", tokens[i++]);
        }
        catch (Exception e) {
            ; // Ignore 
        }
        
        movie.setAudioStream(audio);
    }

    private void parseVideoLine(String line, MovieInfo movie) {
        
        // Stream #0.0  Id:   0: Video: mpeg4, 416x304, 25.00 fps
        // Stream #0.0: Video: h264, 416x304, 25.00 fps
        
        VideoStreamInfo video = new VideoStreamInfo();
        String[] tokens = StringUtils.split(line);
        
        int i = 0;
        
        //Assert.assertEquals(
        //    "Expected 9 tokens for video line: " + line, 9, tokens.length);
        
        Assert.assertEquals(
            "Expected 'Stream' as first token",
            "Stream",
            tokens[i++]);

        Assert.assertEquals(
            "Expected '#0.0' as 2nd token",
            "#0.0",
            StringUtils.chomp(tokens[i++], ":"));

        video.setNumber("0.0");
        
//        Assert.assertEquals(
//            "Expected 'Id:' as 3rd token",
//            "Id:",
//            tokens[i++]);
        
        video.setId("0");
        
//        Assert.assertEquals(
//            "Expected '0:' as 4th token",
//            "0:",
//            tokens[3]);
        
        Assert.assertEquals(
            "Expected 'Video:' as 5th token",
            "Video:",
            tokens[i++]);
        
        video.setFormat(StringUtils.chomp(tokens[i++], ","));
        
        String dims = StringUtils.chomp(tokens[i++], ",");
        String[] dimensions = StringUtils.split(dims, "x");
        
        Assert.assertEquals(
            "Dimensions string invalid: " + dims, 2, dimensions.length);
        
        video.setWidth(new Integer(dimensions[0]));
        video.setHeight(new Integer(dimensions[1]));
        
        video.setFramesPerSecond(tokens[i++]);
        
        Assert.assertEquals("Expected 'fps' as 9th token", "fps", tokens[i++]);
        
        movie.setVideoStream(video);
    }

    /**
     * @param lines
     * @param movie
     * @param i
     */
    private void parseLine1(String line, MovieInfo movie) {
        
        // Duration: 00:32:27.2, start: 0.000000, bitrate: 521 kb/s
        
        String[] tokens = StringUtils.split(line);
        
        Assert.assertEquals(
            "Expected 7 tokens for duration line", 7, tokens.length);
        
        Assert.assertEquals(
            "Expected 'Duration:' as first token",
            "Duration:",
            tokens[0]);
        
        movie.setDuration(StringUtils.chomp(tokens[1], ","));
        
        Assert.assertEquals(
            "Expected 'start:' as 3rd token",
            "start:",
            tokens[2]);
        
        // Skip over start number, token = 4th

        Assert.assertEquals(
            "Expected 'bitrate' as 5th token",
            "bitrate:",
            tokens[4]);

        movie.setBitrate(new Integer(tokens[5]));
    }
}

/*
 
Z:\tivo\incoming>..\gui4ffmpeg\ffmpeg.exe -i "Indian Idol 2.avi"
ffmpeg version 0.4.9-pre1, build 4747, Copyright (c) 2000-2004 Fabrice Bellard
  configuration:  --enable-memalign-hack --enable-mingw32 --enable-mp3lame --enable-gpl --enable-a52
  built on Mar 21 2005 16:41:12, gcc: 3.4.2 (mingw-special)
Input #0, avi, from 'Indian Idol 2.avi':
  Duration: 00:32:27.2, start: 0.000000, bitrate: 521 kb/s
  Stream #0.0  Id:   0: Video: mpeg4, 416x304, 25.00 fps
  Stream #0.1  Id:   1: Audio: mp3, 22050 Hz, stereo, 47 kb/s
Must supply at least one output file

*/