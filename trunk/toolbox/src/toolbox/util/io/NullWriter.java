package toolbox.util.io;

import java.io.Writer;

/**
 * Writer to /dev/null
 */
public class NullWriter extends Writer
{

    public void close()
    {
    }

    public void flush()
    {
    }

    public void write(char[] cbuf, int off, int len)
    {
    }
}