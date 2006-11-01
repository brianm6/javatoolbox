package toolbox.tivo;

import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.Figlet;
import toolbox.util.FileUtil;
import toolbox.util.ProcessUtil;
import toolbox.util.StringUtil;

/**
 * Transcodes a movie to a Tivo supported format using ffmpeg.
 */
public class FFMpegTranscoder extends AbstractTranscoder {

    public static final Logger logger_ = Logger.getLogger(FFMpegTranscoder.class);

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    
    /**
     * Make sure the bit rate passed to ffmpeg never goes about 8000. This
     * happens when ffmpeg reads the bit rate from a movie incorrectly and ends
     * up making ffmpeg blow up on the encoding.
     */
    private static final int MAX_VIDEO_BITRATE = 8000;

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private String logDir_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public FFMpegTranscoder(String logDir) {
        logDir_ = logDir;
    }
    
    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    public static final String getExecutablePath() {
        return "ffmpeg";
    }
    
    // -------------------------------------------------------------------------
    // ITranscoder Interface
    // -------------------------------------------------------------------------

    /**
     * TODO: Split into smaller methods:
     *          buildCommandLine()
     *          executeCommand()
     *          verifyTranscoding()
     */
    public void transcode(MovieInfo movieInfo, String destFilename)
        throws IOException, InterruptedException {

        // Sample command line:
        // ffmpeg -i "input.mpg" -hq -target ntsc-dvd -b 6000 -aspect 4:3 -s 720x0 -padtop 240 -padbottom 240 -acodec mp2 -ab 224 -ac 2 -mbd 2 -qmin 2 -async 1 -y "output.mpg"
        // ffmpeg -i "input.mpg" -hq -target ntsc-dvd -b 6000 -aspect 4:3 -s 720x480 -acodec mp2 -ab 224 -ac 2 -mbd 2 -qmin 2 -async 1 -y "output.mpg"
        
        // ffmpeg.exe" -i "h264.avi" -hq -target ntsc-dvd -b 6000 -aspect 4:3 -s 720x480 -acodec mp2 -ab 224 -ac 2 -mbd 2 -qmin 2 -async 1 -y "C:\workspaces\workspace-toolbox\toolbox\test\toolbox\tivo\crap.mpg"
        // ffmpeg.exe" -i "h264.avi" -hq -target ntsc-dvd -b 6000 -aspect 4:3 -s 656x480 -padright 32 -padleft 32 -acodec mp2 -ab 224 -ac 2 -mbd 2 -qmin 2 -async 1 -y "C:\workspaces\workspace-toolbox\toolbox\test\toolbox\tivo\crap.mpg"
        
        // 416 x 304  1.3682
        //
        // 720/416 = 1.73
        // 480/304 = 1.57
        //
        // width < height so multiply height by 1.57
        //
        // 416 x 1.57 = 653
        //
        // 720 - 653 / 2 = 3.5 = 34
        // 
        // if using width multiplier use -padright -padleft
        // if using height multiplier use -padtop -padbottom
        //
        // 656 x 480  -32 + 32  1.36
        // 
        StringBuffer sb = new StringBuffer();
        
        Collection videoFormatDimensions = new ArrayList();
        videoFormatDimensions.addAll(TivoStandards.VIDEO_FORMATS);
        
        CollectionUtils.transform(
            videoFormatDimensions, 
            new VideoStreamInfoToDimensionTransformer());
        
//        RatioFixer fixer = 
//            new RatioFixer(
//                new Dimension(
//                    TivoStandards.VIDEO_720_480.getWidth().intValue(),
//                    TivoStandards.VIDEO_720_480.getHeight().intValue()),
//                new Dimension(
//                    movieInfo.getVideoStream().getWidth().intValue(),
//                    movieInfo.getVideoStream().getHeight().intValue()));

        RatioFixer2 fixer = 
            new RatioFixer2(
                videoFormatDimensions,
                (Dimension) new VideoStreamInfoToDimensionTransformer().transform(movieInfo.getVideoStream()));
                
        fixer.calc();
        
        sb.append(getExecutablePath() + " ");
        
        // only do the last minute

//        sb.append(
//            "-ss " 
//            + StringUtils.leftPad(movieInfo.getHours() + "", 2, '0')
//            + ":"
//            + StringUtils.leftPad((movieInfo.getMinutes() - 1) + "", 2, '0')
//            + ":"
//            + StringUtils.leftPad(movieInfo.getSeconds() + "", 2, '0')
//            + " ");
        
        sb.append(" -i ");
        sb.append("\"" + movieInfo.getFilename()  + "\" ");
        
        // latest ffmpeg does not like this
        // sb.append("-hq "); 
        
        
        sb.append("-target ntsc-dvd ");
        
        int videoBitRate = Math.min(MAX_VIDEO_BITRATE, movieInfo.getBitrate().intValue() + 128);
        
        sb.append("-b " + videoBitRate + " ");
        sb.append("-aspect 4:3 "); 
        sb.append("-s " + fixer.getWidth() + "x" + fixer.getHeight() + " ");
        
        if (fixer.getLeftPad() > 0 || fixer.getRightPad() > 0) {
            sb.append(fixer.getPadLeftRight() 
                ? "-padleft " + fixer.getLeftPad() + " -padright " + fixer.getRightPad() + " "
                : "-padtop " + fixer.getLeftPad() + " -padbottom " + fixer.getRightPad() + " ");
        }

//        if (fixer.getPad() > 0) {
//            sb.append(fixer.getPadLeftRight() 
//                ? "-padleft " + fixer.getPad() + " -padright " + fixer.getPad() + " "
//                : "-padtop " + fixer.getPad() + " -padbottom " + fixer.getPad() + " ");
//        }
        
        sb.append("-acodec mp2 ");
        sb.append("-ab " + TivoStandards.AUDIO_128.getBitrate() + " "); 
        sb.append("-ac 2 ");
        sb.append("-mbd 2 ");
        sb.append("-qmin 2 ");
        sb.append("-async 1 ");
        sb.append("-y ");

        sb.append("\"" + destFilename + "\"");

        logger_.info("Command: " + sb);
        
        ElapsedTime timer = new ElapsedTime();
        timer.setStartTime();
        
        fireTranscodeStarted();
        
        Process p = Runtime.getRuntime().exec(sb.toString());
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();
        
        OutputStream fout = null;
        OutputStream ferr = null;
        FFMpegProgressOutputStream fpos = null;
        int exitValue = -1;

        File errFile = new File(logDir_, 
            FilenameUtils.getName(movieInfo.getFilename()) + ".err.log");

        try {
            fout = new BufferedOutputStream(
                new FileOutputStream(new File(logDir_, 
                    FilenameUtils.getName(movieInfo.getFilename()) 
                    + ".out.log")));
            
            int totalSeconds = 
                (movieInfo.getHours() * 60 * 60)
                + (movieInfo.getMinutes() * 60)
                + movieInfo.getSeconds();
            
            
            ferr = new BufferedOutputStream(
                        fpos = new FFMpegProgressOutputStream(
                            totalSeconds, 
                            new FileOutputStream(errFile)));

            ferr.write(new String("FFMpeg command: \n" + sb + "\n\n").getBytes());
            
            exitValue = ProcessUtil.getProcessOutput(p, fout, ferr);
        }
        finally {
            timer.setEndTime();

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(1);
            nf.setMaximumFractionDigits(2);
            nf.setMinimumIntegerDigits(1);
            
            if (fpos != null) {
                int transcodeSeconds = (int) timer.getTotalMillis()/1000;
                int frames = fpos.getProgressFrames();
                //logger_.info("elapsed = " + timer + " or " + timer.getTotalMillis());
                //logger_.info("frames = " + frames);
                //logger_.info("seconds = " + transcodeSeconds);
                logger_.info("Frames transcoded/sec = " + (frames / transcodeSeconds));
                logger_.info("\n" + Figlet.getBanner("FPS = " + nf.format(frames/transcodeSeconds)));
            }

            int movieSeconds = movieInfo.getTotalSeconds();
            int transcodeSeconds = (int) timer.getTotalMillis()/1000;
            float speed = (float) movieSeconds / (float) transcodeSeconds;
            
            logger_.info("Transcoded at " + speed + "x speed");
            logger_.info("\n" + Figlet.getBanner("Speed = " + nf.format(speed) + "x"));
            
            IOUtils.closeQuietly(fout);
            IOUtils.closeQuietly(ferr);
            
            FileUtil.setFileContents(errFile, 
                "\nTranscoded at " + nf.format(speed) + "x speed", true);
            
            if (exitValue != 0) 
                fireTranscodeError();
            else
                fireTranscodeFinished();
        }
        
        //logger_.info("stdout length = " + stdout.length());
        //logger_.info("stderr length = " + stderr.length());

        
        if (logger_.isDebugEnabled()) {
            logger_.debug("Exit value: " + exitValue);
            logger_.debug(StringUtil.banner("stdout:\n" + stdout));
            logger_.debug(StringUtil.banner("stderr:\n" + stderr));
            logger_.debug(movieInfo.getFilename() + " transcoded in " + timer);
        }
    }
}