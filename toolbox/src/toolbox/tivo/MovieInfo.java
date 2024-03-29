package toolbox.tivo;

import java.io.File;
import java.text.NumberFormat;

import toolbox.util.StringUtil;

/**
 * Data object that captures the characteristics of a movie file.
 */
public class MovieInfo {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private String filename_;
    private String duration_;
    private Integer bitrate_;
    
    /**
     * Size of the file that contains the movie in bytes.
     */
    private Long fileSize_;
    
    private VideoStreamInfo videoStream_;
    private AudioStreamInfo audioStream_;
 
    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------

    /**
     * Returns the audioStream.
     * 
     * @return AudioStream
     */
    public AudioStreamInfo getAudioStream() {
        return audioStream_;
    }
    
    /**
     * Sets the value of audioStream.
     * 
     * @param audioStream The audioStream to set.
     */
    public void setAudioStream(AudioStreamInfo audioStream) {
        audioStream_ = audioStream;
    }
    
    /**
     * Returns the bitrate.
     * 
     * @return Integer
     */
    public Integer getBitrate() {
        return bitrate_;
    }
    
    /**
     * Sets the value of bitrate.
     * 
     * @param bitrate The bitrate to set.
     */
    public void setBitrate(Integer bitrate) {
        bitrate_ = bitrate;
    }
    
    /**
     * Returns the duration.
     * 
     * @return String
     */
    public String getDuration() {
        return duration_;
    }
    
    /**
     * Sets the value of duration.
     * 
     * @param duration The duration to set.
     */
    public void setDuration(String duration) {
        duration_ = duration;
    }
    
    /**
     * Returns the filename.
     * 
     * @return String
     */
    public String getFilename() {
        return filename_;
    }
    
    
    /**
     * Sets the value of filename.
     * 
     * @param filename The filename to set.
     */
    public void setFilename(String filename) {
        filename_ = filename;
        File f = new File(filename_);
        fileSize_ = new Long(f.length());
    }
    
    
    /**
     * Returns the videoStream.
     * 
     * @return VideoStream
     */
    public VideoStreamInfo getVideoStream() {
        return videoStream_;
    }
    
    
    /**
     * Sets the value of videoStream.
     * 
     * @param videoStream The videoStream to set.
     */
    public void setVideoStream(VideoStreamInfo videoStream) {
        videoStream_ = videoStream;
    }
    
    
    public Long getFileSize() {
        return fileSize_;
    }
    
    
    public int getHours() {
        return Integer.parseInt(duration_.substring(0, 2));
    }
    
    
    public int getMinutes() {
        return Integer.parseInt(duration_.substring(3, 5));
    }
    
    
    public int getSeconds() {
        return Integer.parseInt(duration_.substring(6, 8));
    }

    
    public int getTotalSeconds() {
        return (getHours() * 60 * 60) +
               (getMinutes() * 60) +
               getSeconds();
    }
    
    // -------------------------------------------------------------------------
    // Overrides java.lang.Object
    // -------------------------------------------------------------------------

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        
        NumberFormat nf = NumberFormat.getInstance();
        
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Info\n");
        sb.append("----------\n");
        sb.append("File     = " + getFilename() + "\n");
        sb.append("Size     = " + nf.format(getFileSize()) + " bytes\n");
        sb.append("Bitrate  = " + nf.format(getBitrate()) + " kb/s\n");
        sb.append("Length   = " + getDuration() + "\n");
        sb.append("\n");
        sb.append(StringUtil.indent(getVideoStream().toString(), 2));
        sb.append("\n");
        sb.append(StringUtil.indent(getAudioStream().toString(), 2));
        return sb.toString();
    }
}