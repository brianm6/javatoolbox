package toolbox.util.reflect;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Symbol
 */
public class Symbol implements Externalizable
{
    protected int hashCode;
    protected String string;

    // CONSTRUCTORS

    /**
     * Creates a new Symbol object.
     * 
     * @param string DOCUMENT ME!
     */
    public Symbol(String string)
    {
        setString(string);
    }

    // STANDARD METHODS

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public int hashCode()
    {
        return hashCode;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param another DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public boolean equals(Object another)
    {
        if (this == another)
            return true;
        else if (another == null || another.getClass() != getClass())
            return false;

        return string == another.toString();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public String toString()
    {
        return string;
    }

    // ACCESSING METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param string DOCUMENT ME!
     */
    protected final void setString(String string)
    {
        this.string = string.intern();
        this.hashCode = string.hashCode();
    }

    // EXTERNALIZABLE METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param in DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     * @throws ClassNotFoundException DOCUMENT ME!
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
     * DOCUMENT ME!
     * 
     * @param out DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void writeExternal(ObjectOutput out) throws IOException
    {
        byte[] buf = string.getBytes();
        out.writeInt(buf.length);
        out.write(buf);
    }
}