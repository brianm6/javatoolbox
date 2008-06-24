package toolbox.util.reflect;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Symbol.
 */
public class Symbol implements Externalizable
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private int hashCode_;
    private String string_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new Symbol object.
     * 
     * @param string DOCUMENT ME!
     */
    public Symbol(String string)
    {
        setString(string);
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return hashCode_;
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object another)
    {
        if (this == another)
            return true;
        else if (another == null || another.getClass() != getClass())
            return false;

        return string_ == another.toString();
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return string_;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     * 
     * @param string DOCUMENT ME!
     */
    protected final void setString(String string)
    {
        string_ = string.intern();
        hashCode_ = string.hashCode();
    }

    //--------------------------------------------------------------------------
    // Externalizable Interface
    //--------------------------------------------------------------------------

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException,
        ClassNotFoundException
    {
        int size = in.readInt();
        byte[] buf = new byte[size];
        in.readFully(buf);
        setString(new String(buf));
    }


    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException
    {
        byte[] buf = string_.getBytes();
        out.writeInt(buf.length);
        out.write(buf);
    }
}