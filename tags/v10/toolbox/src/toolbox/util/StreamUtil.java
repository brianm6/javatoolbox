package toolbox.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * I/O Stream Utility Class
 */
public class StreamUtil
{
	/**
	 * Prevent construction
	 */
	private StreamUtil()
	{
	}

	/**
	 * Converts the contents of an character input stream to a string.
	 *
	 * @param      inputStream        The input stream to read from
	 * @return     String representation of the input stream contents.
	 * @exception  IOException
	 */
	public static String asString(InputStream inputStream) throws IOException
	{
		return new String(toBytes(inputStream));
	}

	/**
	 * Converts the remaining contents of an InputStream to a byte array
	 * 
	 * @param  is   InputStream to convert
	 * @return byte[]
	 */	
	public static byte[] toBytes(InputStream is) throws IOException
	{
		List buffer = new ArrayList();
		int c;
		while ( (c = is.read()) != -1)
		{
			Byte b = new Byte((byte)c);
			buffer.add(b);
		}
		
		byte[] byteArray = new byte[buffer.size()];
		for(int i=0; i<buffer.size(); i++)
		{
			Byte b = (Byte)buffer.get(i);
			byteArray[i] = b.byteValue();
		}
		return byteArray;
	}
}