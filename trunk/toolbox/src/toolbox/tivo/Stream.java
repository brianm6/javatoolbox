package toolbox.tivo;


public class Stream {

    String number_;
    String id_;
    String format_;
    
    /**
     * Returns the format.
     * 
     * @return String
     */
    public String getFormat() {
        return format_;
    }
    
    /**
     * Sets the value of format.
     * 
     * @param format The format to set.
     */
    public void setFormat(String format) {
        format_ = format;
    }
    
    /**
     * Returns the id.
     * 
     * @return String
     */
    public String getId() {
        return id_;
    }
    
    /**
     * Sets the value of id.
     * 
     * @param id The id to set.
     */
    public void setId(String id) {
        id_ = id;
    }
    
    /**
     * Returns the number.
     * 
     * @return String
     */
    public String getNumber() {
        return number_;
    }
    
    /**
     * Sets the value of number.
     * 
     * @param number The number to set.
     */
    public void setNumber(String number) {
        number_ = number;
    }
}
