package toolbox.tivo;


public class TivoStandards {

    public static final VideoStreamInfo VIDEO_720 = new VideoStreamInfo();
    public static final AudioStreamInfo AUDIO_224 = new AudioStreamInfo();
    
    static {
        VIDEO_720.setFormat("mpeg2video");
        VIDEO_720.setFramesPerSecond("29.97");
        VIDEO_720.setHeight(new Integer(480));
        VIDEO_720.setWidth(new Integer(720));

        AUDIO_224.setBitrate(new Integer(224));
        AUDIO_224.setFormat("mp2");
        AUDIO_224.setHertz(new Integer(48000));
        AUDIO_224.setStereo(true);
    }
}
