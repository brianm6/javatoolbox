package toolbox.jtail.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.jtail.TailReader;

/**
 * 
 */
public class TailReaderTest extends TestCase
{

	/**
	 * Entrypoint
	 */
	public static void main(String[] args)
	{
		TestRunner tr = new TestRunner();
		tr.run(TailReaderTest.class); 
	}

	/**
	 * Constructor for TailReaderTest.
	 */
	public TailReaderTest(String name)
	{
		super(name);
	}

	/**
	 * Test TailReader
	 */
	public void testTailReader() throws Exception
	{
		//TailReader tr = new TailReader(null, null);		
	}

}
