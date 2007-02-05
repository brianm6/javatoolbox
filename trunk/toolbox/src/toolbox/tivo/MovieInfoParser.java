package toolbox.tivo;

import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ProcessUtil;
import toolbox.util.StringUtil;

/**
 * Parses movie info from the output of running FFMpeg.
 */
public class MovieInfoParser {

    static public final Logger logger_ =
        Logger.getLogger(MovieInfoParser.class);

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------

    public MovieInfo parse(String filename) throws Exception {

	    List command = new ArrayList();
        command.add(FFMpegTranscoder.getExecutablePath());
		command.add("-i");
		command.add(filename);
        //String command = FFMpegTranscoder.getExecutablePath() + " -i \"" + filename + "\"";

        logger_.info("Executing: " + command);

        Process p = Runtime.getRuntime().exec((String[]) command.toArray(new String[0]));
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();
        int exitValue = ProcessUtil.getProcessOutput(p, stdout, stderr);

        logger_.info("\n\n" + stderr + "\n");

        String stdErr = stderr.toString();

        try {
            MovieInfo movie = new MovieInfo();
            movie.setFilename(filename);
            String[] lines = StringUtil.tokenize(stdErr, "\n");
            String movieLine = (String) new MovieLineScanner().getToken(stdErr);
            String videoLine = (String) new VideoLineScanner().getToken(stdErr);
            String audioLine = (String) new AudioLineScanner().getToken(stdErr);

            parseMovieLine(movieLine, movie);
            parseVideoLine(videoLine, movie);
            parseAudioLine(audioLine, movie);

            return movie;
        }
        catch (Exception e) {
            logger_.error(
                "Query info error:"
                + e.getMessage()
                //+ "\n\nstdout:\n"
                //+ StringUtil.indent(stdout.toString())
                + "\n\nstderr:\n"
                + StringUtil.indent(stdErr), e);

            throw e;
        }

    }

    void parseMovieLine(String line, MovieInfo movie) {

        // Duration: 00:32:27.2, start: 0.000000, bitrate: 521 kb/s

        // TODO: Convert to TokenScanner interface once something breaks it!

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

    void parseVideoLine(String line, MovieInfo movie) {

        // Stream #0.0  Id:   0: Video: mpeg4, 416x304, 25.00 fps
        // Stream #0.0: Video: h264, 416x304, 25.00 fps
        // Stream #0.0, 25.00 fps(r): Video: h264, yuv420p, 416x304

        VideoStreamInfo video = new VideoStreamInfo();
        video.setNumber("0.0");
        video.setId("0");
        video.setFormat((String) new VideoFormatScanner().getToken(line));
        video.setWidth((Integer) new WidthScanner().getToken(line));
        video.setHeight((Integer) new HeightScanner().getToken(line));
        video.setFramesPerSecond((String) new FramesPerSecondScanner().getToken(line));
        movie.setVideoStream(video);
    }

    void parseAudioLine(String line, MovieInfo movie) {

        // Stream #0.1  Id:   1: Audio: mp3, 22050 Hz, stereo, 47 kb/s
        // Stream #0.1: Audio: mp3, 22050 Hz, mono, 31 kb/s
        // Stream #0.1: Audio: aac, 48000 Hz, stereo

        AudioStreamInfo audio = new AudioStreamInfo();
        audio.setNumber("0.1");
        audio.setId("1");
        audio.setFormat((String) new AudioFormatScanner().getToken(line));
        audio.setHertz((Integer) new AudioHertzScanner().getToken(line));
        audio.setChannels((String) new AudioChannelsScanner().getToken(line));
        audio.setBitrate((Integer) new AudioBitrateScanner().getToken(line));
        movie.setAudioStream(audio);
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------

    interface TokenScanner {
        Object getToken(String line);
    }

    // Mandatory
    class AudioLineScanner implements TokenScanner {

        public Object getToken(String text) {

            List lines = ArrayUtil.toList(StringUtils.split(text, '\n'));

            for (ListIterator i = lines.listIterator(); i.hasNext(); ) {
                String line = (String) i.next();

                List tokens = ArrayUtil.toList(StringUtils.split(line));

                if (tokens.contains("Stream") && tokens.contains("Audio:")) {
                    return line;
                }
            }

            throw new IllegalArgumentException(
                "Could not determine audio line in: \n " + lines);
        }
    }

    // Mandatory
    class VideoLineScanner implements TokenScanner {

        public Object getToken(String text) {

            List lines = ArrayUtil.toList(StringUtils.split(text, '\n'));

            for (ListIterator i = lines.listIterator(); i.hasNext(); ) {
                String line = (String) i.next();

                List tokens = ArrayUtil.toList(StringUtils.split(line));

                if (tokens.contains("Stream") && tokens.contains("Video:")) {
                    return line;
                }
            }

            throw new IllegalArgumentException(
                "Could not determine video line in: \n " + lines);
        }
    }

    // Mandatory
    class MovieLineScanner implements TokenScanner {

        public Object getToken(String text) {

            List lines = ArrayUtil.toList(StringUtils.split(text, '\n'));

            for (ListIterator i = lines.listIterator(); i.hasNext(); ) {
                String line = (String) i.next();

                List tokens = ArrayUtil.toList(StringUtils.split(line));

                if (tokens.contains("Duration:")) {
                    return line;
                }
            }

            throw new IllegalArgumentException(
                "Could not determine movie line in: \n " + lines);
        }
    }


    // Optional
    class FramesPerSecondScanner implements TokenScanner {

        public Object getToken(String line) {

            List tokens = ArrayUtil.toList(StringUtils.split(line));

            for (ListIterator i = tokens.listIterator(); i.hasNext(); ) {
                String token = (String) i.next();
                if (token.startsWith("fps") && i.hasPrevious()) {
                    i.previous();
                    return (String) i.previous();
                }

            }

            logger_.warn("FPS not found in line: " + line);
            return null;
        }
    }

    // Optional
    class VideoFormatScanner implements TokenScanner {

        public Object getToken(String line) {

            List tokens = ArrayUtil.toList(StringUtils.split(line));

            for (ListIterator i = tokens.listIterator(); i.hasNext(); ) {
                String token = (String) i.next();
                if (token.startsWith("Video:") && i.hasNext())
                    return StringUtils.chomp((String) i.next(), ",");
            }

            logger_.warn("Video format not found in line: " + line);
            return null;
        }
    }

    // Mandatory
    class WidthScanner implements TokenScanner {

        public Object getToken(String line) {

            List tokens = ArrayUtil.toList(StringUtils.split(line));

            for (ListIterator i = tokens.listIterator(); i.hasNext(); ) {
                String token = (String) i.next();

                if (token.indexOf('x') > 1) {
                    String[] dimensions = StringUtils.split(token, "x,");

                    if (dimensions.length == 2) {
                        try {
                            int width = Integer.parseInt(dimensions[0]);
                            return new Integer(width);
                        }
                        catch (NumberFormatException nfe) {
                            // Ignore
                        }
                    }
                }
            }

            throw new IllegalArgumentException(
                "Width not found in line: " + line);
        }
    }

    // Mandatory
    class HeightScanner implements TokenScanner {

        public Object getToken(String line) {

            List tokens = ArrayUtil.toList(StringUtils.split(line));

            for (ListIterator i = tokens.listIterator(); i.hasNext(); ) {
                String token = (String) i.next();

                if (token.indexOf('x') > 1) {
                    String[] dimensions = StringUtils.split(token, "x,");

                    if (dimensions.length == 2) {
                        try {
                            int height = Integer.parseInt(dimensions[1]);
                            return new Integer(height);
                        }
                        catch (NumberFormatException nfe) {
                            // Ignore
                        }
                    }
                }
            }

            throw new IllegalArgumentException(
                "Height not found in line: " + line);
        }
    }

    // Optional
    class AudioFormatScanner implements TokenScanner {

        public Object getToken(String line) {

            List tokens = ArrayUtil.toList(StringUtils.split(line));

            for (ListIterator i = tokens.listIterator(); i.hasNext(); ) {
                String token = (String) i.next();
                if (token.startsWith("Audio:") && i.hasNext())
                    return StringUtils.chomp((String) i.next(), ",");
            }

            logger_.warn("Audio format not found in line: " + line);
            return null;
        }
    }

    // Optional
    class AudioBitrateScanner implements TokenScanner {

        public Object getToken(String line) {

            List tokens = ArrayUtil.toList(StringUtils.split(line));

            for (ListIterator i = tokens.listIterator(); i.hasNext(); ) {
                String token = (String) i.next();
                if (token.startsWith("kb/s") && i.hasPrevious()) {
                    i.previous();
                    String s = (String) i.previous();

                    try {
                        int bitrate = Integer.parseInt(s);
                        return new Integer(bitrate);
                    }
                    catch (NumberFormatException nfe) {
                        // Ignore
                    }
                }

            }

            logger_.warn("Audio bitrate not found in line: " + line);
            return null;
        }
    }

    // Optional
    class AudioHertzScanner implements TokenScanner {

        public Object getToken(String line) {

            List tokens = ArrayUtil.toList(StringUtils.split(line));

            for (ListIterator i = tokens.listIterator(); i.hasNext(); ) {
                String token = (String) i.next();
                if (token.startsWith("Hz") && i.hasPrevious()) {
                    i.previous();
                    String s = (String) i.previous();

                    try {
                        int hertz = Integer.parseInt(s);
                        return new Integer(hertz);
                    }
                    catch (NumberFormatException nfe) {
                        // Ignore
                    }
                }

            }

            logger_.warn("Audio hertz not found in line: " + line);
            return null;
        }
    }

    // Optional
    class AudioChannelsScanner implements TokenScanner {

        public Object getToken(String line) {

            List tokens = ArrayUtil.toList(StringUtils.split(line));

            for (ListIterator i = tokens.listIterator(); i.hasNext(); ) {
                String token = (String) i.next();
                token = StringUtils.chomp(token, ",");
                if (token.equals("mono") || token.equals("stereo"))
                    return token;
            }

            logger_.warn("Audio channels not found in line: " + line);
            return null;
        }
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


//        ffmpeg version CVS, build 3342336, Copyright (c) 2000-2004 Fabrice Bellard
//          configuration:
//          built on Dec 18 2005 14:20:43, gcc: 3.4.4 (cygming special) (gdc 0.12, using dmd 0.125)
//        Input #0, avi, from 'z:\tivo\incoming\h264.avi':
//          Duration: 00:01:37.1, start: 0.000000, bitrate: 203 kb/s
//          Stream #0.0, 25.00 fps(r): Video: h264, yuv420p, 416x304
//          Stream #0.1: Audio: mp3, 22050 Hz, mono, 32 kb/s
//        Must supply at least one output file


*/

/*
private void parseVideoLineOld(String line, MovieInfo movie) {

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

Assert.assertTrue(
    "Expected '#0.0' as 2nd token",
    tokens[i++].startsWith("#0.0"));

video.setNumber("0.0");

//Assert.assertEquals(
//    "Expected 'Id:' as 3rd token",
//    "Id:",
//    tokens[i++]);

video.setId("0");

//Assert.assertEquals(
//    "Expected '0:' as 4th token",
//    "0:",
//    tokens[3]);

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

*/