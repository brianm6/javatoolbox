package toolbox.tivo;

import java.text.NumberFormat;

/**
 * Audio stream information.
 */
public class AudioStreamInfo extends StreamInfo {

    private Integer hertz_;

    private String channels_;

    private Integer bitrate_;


    /**
     * Returns the bitrate or null if the bitrate was not obtainable.
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
     * Returns the hertz or null if the hertz was not obtainable.
     * 
     * @return Integer
     */
    public Integer getHertz() {
        return hertz_;
    }


    /**
     * Sets the value of hertz.
     * 
     * @param hertz The hertz to set.
     */
    public void setHertz(Integer hertz) {
        hertz_ = hertz;
    }


    public String getChannels() {
        return channels_;
    }


    public void setChannels(String channels) {
        channels_ = channels;
    }
    
    // -------------------------------------------------------------------------
    // Overrides java.lang.Object
    // -------------------------------------------------------------------------
    
    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        
        StringBuffer sb = new StringBuffer();
        sb.append("Audio Stream\n");
        sb.append("------------\n");
        
        sb.append(
            "Format   = " 
            + (getFormat() != null 
                ? getFormat() + "\n" 
                : "N/A\n"));
        
        sb.append(
            "Bitrate  = " 
            + (getBitrate() != null 
                ? nf.format(getBitrate()) + " kb/s\n" 
                : "N/A\n"));
        
        sb.append(
            "Hertz    = " 
            + (getHertz() != null 
                ? nf.format(getHertz()) + " Hz\n" 
                : "N/A\n"));
        
        sb.append(
            "Channels = " 
            + (getChannels() != null 
                ? getChannels() + "\n" 
                : "N/A\n"));
        
        return sb.toString();
    }
    
    
    public boolean equals(Object obj) {
        
        if (obj == null)
            return false;
        
        if (this == obj)
            return true;
        
        if (getClass() != obj.getClass())
            return false;
        
        AudioStreamInfo info = (AudioStreamInfo) obj;
        
        return ( 
            getBitrate()  != null ? getBitrate().equals(info.getBitrate())   : info.getBitrate() == null &&
            getFormat()   != null ? getFormat().equals(info.getFormat())     : info.getFormat() == null &&
            getHertz()    != null ? getHertz().equals(info.getHertz())       : info.getHertz() == null &&
            getChannels() != null ? getChannels().equals(info.getChannels()) : info.getChannels() == null); 
    }
    
    
    public int hashCode() {
        return super.hashCode();
    }
}
