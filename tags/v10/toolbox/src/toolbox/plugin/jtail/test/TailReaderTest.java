/*
 * (c) Copyright 2001 MyCorporation.
 * All Rights Reserved.
 */
package toolbox.jtail.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.jtail.TailReader;

/**
 * @version 	1.0
 * @author
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
	public TailReaderTest()
	{
		super("");
	}

	/**
	 * Test TailReader
	 */
	public void testTailReader() throws Exception
	{
		TailReader tr = new TailReader(null, null);		
	}

}
