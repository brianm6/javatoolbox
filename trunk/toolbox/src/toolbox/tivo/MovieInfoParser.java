package toolbox.tivo;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.tunnel.Relay;
import toolbox.util.StringUtil;
import toolbox.util.io.StringOutputStream;


public class MovieInfoParser {
    
    private static final Logger logger_ = 
        Logger.getLogger(MovieInfoParser.class);
    

    public Movie parse(String filename) throws Exception {
    
        String command = "ffmpeg -i \"" + filename + "\"";
        logger_.debug("Command: " + command);
        
        Process p = Runtime.getRuntime().exec(command);
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();
        int exitValue = getProcessResults(p, stdout, stderr);
        
        logger_.debug(StringUtil.banner("Output: \n" + stdout));
        logger_.debug(StringUtil.banner("Error: \n" + stderr));
        
        String[] lines = StringUtil.tokenize(stderr.toString(), "\n");
        Movie movie = new Movie();
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

    private void parseAudioLine(String line, Movie movie) {
        
        // Stream #0.1  Id:   1: Audio: mp3, 22050 Hz, stereo, 47 kb/s
        
        AudioStream audio = new AudioStream();
        String[] tokens = StringUtils.split(line);
        
        Assert.assertEquals(
            "Expected 11 tokens for audio line: " + line, 11, tokens.length);
        
        Assert.assertEquals(
            "Expected 'Stream' as first token",
            "Stream",
            tokens[0]);

        Assert.assertEquals(
            "Expected '#0.1' as 2nd token",
            "#0.1",
            tokens[1]);
        
        audio.setNumber("0.1");
        
        Assert.assertEquals(
            "Expected 'Id:' as 3rd token",
            "Id:",
            tokens[2]);
        
        audio.setId("1");
        
        Assert.assertEquals(
            "Expected '1:' as 4th token",
            "1:",
            tokens[3]);
        
        
        Assert.assertEquals(
            "Expected 'Audio:' as 5th token",
            "Audio:",
            tokens[4]);
        
        audio.setFormat(StringUtils.chomp(tokens[5], ","));
        audio.setHertz(new Integer(tokens[6]));
        
        Assert.assertEquals("Expected 'Hz,' as 8th token", "Hz,", tokens[7]);
        
        audio.setStereo(tokens[8].equals("stereo,"));
        audio.setBitrate(new Integer(tokens[9]));

        Assert.assertEquals("11th token", "kb/s", tokens[10]);
        
        movie.setAudioStream(audio);
    }

    private void parseVideoLine(String line, Movie movie) {
        
        // Stream #0.0  Id:   0: Video: mpeg4, 416x304, 25.00 fps
        
        VideoStream video = new VideoStream();
        String[] tokens = StringUtils.split(line);
        
        Assert.assertEquals(
            "Expected 9 tokens for video line: " + line, 9, tokens.length);
        
        Assert.assertEquals(
            "Expected 'Stream' as first token",
            "Stream",
            tokens[0]);

        Assert.assertEquals(
            "Expected '#0.0' as 2nd token",
            "#0.0",
            tokens[1]);

        video.setNumber("0.0");
        
        Assert.assertEquals(
            "Expected 'Id:' as 3rd token",
            "Id:",
            tokens[2]);
        
        video.setId("0");
        
        Assert.assertEquals(
            "Expected '0:' as 4th token",
            "0:",
            tokens[3]);
        
        Assert.assertEquals(
            "Expected 'Video:' as 5th token",
            "Video:",
            tokens[4]);
        
        video.setFormat(StringUtils.chomp(tokens[5], ","));
        
        String dims = StringUtils.chomp(tokens[6], ",");
        String[] dimensions = StringUtils.split(dims, "x");
        
        Assert.assertEquals(
            "Dimensions string invalid: " + dims, 2, dimensions.length);
        
        video.setWidth(new Integer(dimensions[0]));
        video.setHeight(new Integer(dimensions[1]));
        
        video.setFramesPerSecond(tokens[7]);
        
        Assert.assertEquals("Expected 'fps' as 9th token", "fps", tokens[8]);
        
        movie.setVideoStream(video);
    }

    /**
     * @param lines
     * @param movie
     * @param i
     */
    private void parseLine1(String line, Movie movie) {
        
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
    
    public static int getProcessResults(
        Process process,
        StringBuffer stdout,
        StringBuffer stderr) throws InterruptedException {
        
        StringOutputStream stdoutStream = new StringOutputStream();
        Relay stdoutRelay = new Relay(process.getInputStream(), stdoutStream);
        Thread stdoutThread = new Thread(stdoutRelay, "stdoutRelay");
        stdoutThread.start();
        
        StringOutputStream stderrStream = new StringOutputStream();
        Relay stderrRelay = new Relay(process.getErrorStream(), stderrStream);
        Thread stderrThread = new Thread(stderrRelay, "stderrRelay");
        stderrThread.start();
        
        logger_.debug("Waiting for exit...");
        int exitValue = process.waitFor();

        logger_.debug("Joining stdout thread...");
        stdoutThread.join();
        
        logger_.debug("Joining stderr thread...");
        stderrThread.join();
        
        stdout.append(stdoutStream.toString());
        stderr.append(stderrStream.toString());
        return exitValue;
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