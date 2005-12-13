package toolbox.tivo;

import java.io.IOException;

import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.ProcessUtil;
import toolbox.util.StringUtil;

public class FFMpegTranscoder implements ITranscoder {

    public static final Logger logger_ = 
        Logger.getLogger(FFMpegTranscoder.class);

    
    public FFMpegTranscoder() {
    }

    // -------------------------------------------------------------------------
    // ITranscoder Interface
    // -------------------------------------------------------------------------
    
    public void transcode(MovieInfo movieInfo, String destFilename)
        throws IOException, InterruptedException {

        // Sample command line:
        // ffmpeg -i "input.mpg" -hq -target ntsc-dvd -b 6000 -aspect 4:3 -s 720x0 -padtop 240 -padbottom 240 -acodec mp2 -ab 224 -ac 2 -mbd 2 -qmin 2 -async 1 -y "output.mpg"
        // ffmpeg -i "input.mpg" -hq -target ntsc-dvd -b 6000 -aspect 4:3 -s 720x480 -acodec mp2 -ab 224 -ac 2 -mbd 2 -qmin 2 -async 1 -y "output.mpg"
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("c:\\bin\\ffmpeg.exe ");
        sb.append(" -i ");
        sb.append("\"" + movieInfo.getFilename()  + "\" ");
        sb.append("-hq -target ntsc-dvd ");
        sb.append("-b " + movieInfo.getBitrate() + " ");
        sb.append("-aspect 4:3 -s 720x480 -acodec mp2 -ab 224 -ac 2 -mbd 2 -qmin 2 -async 1 -y ");;
        sb.append("\"" + destFilename + "\"");

        logger_.debug("Command: " + sb);
        
        ElapsedTime timer = new ElapsedTime();
        timer.setStartTime();
        
        Process p = Runtime.getRuntime().exec(sb.toString());
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();
        int exitValue = ProcessUtil.getProcessOutput(p, stdout, stderr);
        
        timer.setEndTime();
        
        logger_.debug("Exit value: " + exitValue);
        logger_.debug(StringUtil.banner("stdout:\n" + stdout));
        logger_.debug(StringUtil.banner("stderr:\n" + stderr));
        logger_.info("Transcoded in " + timer);
    }
}
