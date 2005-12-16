package toolbox.tivo;

import java.util.Iterator;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class CircularCharQueue {
    
    private CircularFifoBuffer buffer_;
    
    public CircularCharQueue(int size) {
        buffer_ = new CircularFifoBuffer(size);
    }
    
    public int size() {
        return buffer_.size();
    }
    
    public void add(char c) {
        buffer_.add(new Character(c));
    }
    
    public void clear() {
        buffer_.clear();
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(size());
        for (Iterator i = buffer_.iterator(); i.hasNext(); ) {
            Character c = (Character) i.next();
            sb.append(c.charValue());
        }
        return sb.toString();
    }
}