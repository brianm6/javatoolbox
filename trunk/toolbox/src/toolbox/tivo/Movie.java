package toolbox.tivo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Movie {

    private String filename_;
    private String duration_;
    private Integer bitrate_;
    
    private VideoStream videoStream_;
    private AudioStream audioStream_;
 
    
    /**
     * Returns the audioStream.
     * 
     * @return AudioStream
     */
    public AudioStream getAudioStream() {
        return audioStream_;
    }
    
    /**
     * Sets the value of audioStream.
     * 
     * @param audioStream The audioStream to set.
     */
    public void setAudioStream(AudioStream audioStream) {
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
    }
    
    /**
     * Returns the videoStream.
     * 
     * @return VideoStream
     */
    public VideoStream getVideoStream() {
        return videoStream_;
    }
    
    /**
     * Sets the value of videoStream.
     * 
     * @param videoStream The videoStream to set.
     */
    public void setVideoStream(VideoStream videoStream) {
        videoStream_ = videoStream;
    }
    
    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}