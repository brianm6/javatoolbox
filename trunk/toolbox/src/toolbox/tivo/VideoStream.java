package toolbox.tivo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class VideoStream extends Stream {

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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
