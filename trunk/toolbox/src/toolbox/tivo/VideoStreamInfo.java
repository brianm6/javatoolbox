package toolbox.tivo;

import java.text.NumberFormat;

public class VideoStreamInfo extends StreamInfo {

    Integer width_;
    Integer height_;
    String  framesPerSecond_;
    
    /**
     * Returns the framesPerSecond.
     * 
     * @return String
     */
    public String getFramesPerSecond() {
        return framesPerSecond_;
    }
    
    /**
     * Sets the value of framesPerSecond.
     * 
     * @param framesPerSecond The framesPerSecond to set.
     */
    public void setFramesPerSecond(String framesPerSecond) {
        framesPerSecond_ = framesPerSecond;
    }
    
    /**
     * Returns the height.
     * 
     * @return Integer
     */
    public Integer getHeight() {
        return height_;
    }
    
    /**
     * Sets the value of height.
     * 
     * @param height The height to set.
     */
    public void setHeight(Integer height) {
        height_ = height;
    }
    
    /**
     * Returns the width.
     * 
     * @return Integer
     */
    public Integer getWidth() {
        return width_;
    }
    
    /**
     * Sets the value of width.
     * 
     * @param width The width to set.
     */
    public void setWidth(Integer width) {
        width_ = width;
    }
    
    public String toString() {
        //return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
        
        NumberFormat nf = NumberFormat.getInstance();
        
        StringBuffer sb = new StringBuffer();
        sb.append("Video Stream\n");
        sb.append("------------\n");
        sb.append("Format   = " + getFormat() + "\n");
        sb.append("FPS      = " + getFramesPerSecond() + " fps\n");
        sb.append("Width    = " + nf.format(getWidth()) + " pixels\n");
        sb.append("Height   = " + nf.format(getHeight()) + " pixels\n");
        return sb.toString();
    }
    
    public boolean equals(Object obj) {
        
        if (obj == null)
            return false;
        
        if (this == obj)
            return true;
        
        if (getClass() != obj.getClass())
            return false;
        
        VideoStreamInfo info = (VideoStreamInfo) obj;
        
        return ( 
            getFramesPerSecond().equals(info.getFramesPerSecond()) &&
            getFormat().equals(info.getFormat()) &&
            getHeight().equals(info.getHeight()) &&
            getWidth().equals(info.getWidth()));
    }

}
