package toolbox.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author spatel
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PersistentReader extends BufferedReader
{

    /**
     * Constructor for PersistentReader.
     * @param in
     * @param sz
     */
    public PersistentReader(Reader in, int sz)
    {
        super(in, sz);
    }

    /**
     * Constructor for PersistentReader.
     * @param in
     */
    public PersistentReader(Reader in)
    {
        super(in);
    }

    public static void main(String[] args)
    {
    }
    
    /**
     * @see java.io.BufferedReader#read(char[], int, int)
     */
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        return super.read(cbuf, off, len);
    }
    
}
