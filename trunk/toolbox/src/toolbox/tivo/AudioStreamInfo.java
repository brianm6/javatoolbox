package toolbox.tivo;

import java.text.NumberFormat;


public class AudioStreamInfo extends StreamInfo {

    Integer hertz_;

    boolean stereo_;

    Integer bitrate_;


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
     * Returns the hertz.
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


    /**
     * Returns the stereo.
     * 
     * @return boolean
     */
    public boolean isStereo() {
        return stereo_;
    }


    /**
     * Sets the value of stereo.
     * 
     * @param stereo The stereo to set.
     */
    public void setStereo(boolean stereo) {
        stereo_ = stereo;
    }
    
    // -------------------------------------------------------------------------
    // Overrides java.lang.Object
    // -------------------------------------------------------------------------
    
    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        
        StringBuffer sb = new StringBuffer();
        sb.append("Audio Stream\n");
        sb.append("------------\n");
        sb.append("Format   = " + getFormat() + "\n");
        sb.append("Bitrate  = " + nf.format(getBitrate()) + " kb/s\n");
        sb.append("Hertz    = " + nf.format(getHertz()) + " Hz\n");
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
            getBitrate().equals(info.getBitrate()) &&
            getFormat().equals(info.getFormat()) &&
            getHertz().equals(info.getHertz()) &&
            isStereo() == info.isStereo());
    }
    
    public int hashCode() {
        return super.hashCode();
    }
}
