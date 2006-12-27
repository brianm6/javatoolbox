package toolbox.util.collections;

import java.util.Iterator;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class CircularCharQueue implements CharSequence {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private CircularFifoBuffer buffer_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public CircularCharQueue(int size) {
        buffer_ = new CircularFifoBuffer(size);
    }
    
    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
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

    // -------------------------------------------------------------------------
    // CharSequence Interface
    // -------------------------------------------------------------------------
    
    /*
     * @see java.lang.CharSequence#charAt(int)
     */
    public char charAt(int index) {
        return toString().charAt(index);
    }
    
    /*
     * @see java.lang.CharSequence#length()
     */
    public int length() {
        return size();
    }
    
    /*
     * @see java.lang.CharSequence#subSequence(int, int)
     */
    public CharSequence subSequence(int start, int end) {
        CircularCharQueue q = new CircularCharQueue(end - start + 1);
        for (int i = start; i < end; i++)
            q.add(charAt(i));
        return q;
    }
}