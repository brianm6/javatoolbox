package toolbox.tivo;

import java.util.ArrayList;
import java.util.List;

/**
 * Tivo supported video and audio format settings. Movies must conform to these
 * standards to be playable on a non-HD tivo. 
 */
public class TivoStandards {

    public static final List VIDEO_FORMATS = new ArrayList();
    public static final VideoStreamInfo VIDEO_720_480 = new VideoStreamInfo();
    public static final VideoStreamInfo VIDEO_704_480 = new VideoStreamInfo();
    public static final VideoStreamInfo VIDEO_544_480 = new VideoStreamInfo();
    public static final VideoStreamInfo VIDEO_480_480 = new VideoStreamInfo();
    public static final VideoStreamInfo VIDEO_352_480 = new VideoStreamInfo();
    public static final VideoStreamInfo VIDEO_DEFAULT = VIDEO_720_480;
        
    public static final AudioStreamInfo AUDIO_128 = new AudioStreamInfo();

    
    static {
        VIDEO_720_480.setFormat("mpeg2video");
        VIDEO_720_480.setFramesPerSecond("29.97");
        VIDEO_720_480.setHeight(new Integer(480));
        VIDEO_720_480.setWidth(new Integer(720));

        VIDEO_704_480.setFormat("mpeg2video");
        VIDEO_704_480.setFramesPerSecond("29.97");
        VIDEO_704_480.setHeight(new Integer(480));
        VIDEO_704_480.setWidth(new Integer(704));
        
        VIDEO_544_480.setFormat("mpeg2video");
        VIDEO_544_480.setFramesPerSecond("29.97");
        VIDEO_544_480.setHeight(new Integer(480));
        VIDEO_544_480.setWidth(new Integer(544));
        
        VIDEO_480_480.setFormat("mpeg2video");
        VIDEO_480_480.setFramesPerSecond("29.97");
        VIDEO_480_480.setHeight(new Integer(480));
        VIDEO_480_480.setWidth(new Integer(480));
    
        VIDEO_352_480.setFormat("mpeg2video");
        VIDEO_352_480.setFramesPerSecond("29.97");
        VIDEO_352_480.setHeight(new Integer(480));
        VIDEO_352_480.setWidth(new Integer(352));

        //VIDEO_FORMATS.add(VIDEO_352_480);
        //VIDEO_FORMATS.add(VIDEO_480_480);
        //VIDEO_FORMATS.add(VIDEO_544_480);
        //VIDEO_FORMATS.add(VIDEO_704_480);
        VIDEO_FORMATS.add(VIDEO_720_480);
        
        AUDIO_128.setBitrate(new Integer(128));
        AUDIO_128.setFormat("mp2");
        AUDIO_128.setHertz(new Integer(48000));
        AUDIO_128.setChannels("stereo");
    }
}