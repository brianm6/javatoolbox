package toolbox.tail;

import java.io.FileNotFoundException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;

/**
 * Tails one or more files
 */
public class Main extends TailAdapter
{
    private static final Category logger_ = 
        Category.getInstance(Main.class);

	/**
	 * Entrypoint 
	 */	
	public static void main(String args[])
	{
        BasicConfigurator.configure();
		Main tail = new Main(args);
	}

	/**
	 * Arg constructor
	 * 
	 * @param  args  Array of files to tail
	 */
	public Main(String args[])
	{
	
		if(args.length == 0)
		{
			printUsage();
			return;
		}
			
		String files[] = args;

		for (int i = 0; i < files.length; i++)
		{
            try
            {
                Tail tail = new Tail();
                tail.addTailListener(this);
                tail.tail(files[i]);
            }
            catch (FileNotFoundException fnfe)
            {
                logger_.error("constructor", fnfe);
            }
		}
	}

	/**
	 * Program usage 
	 */
	private void printUsage()
	{
		System.out.println("Program : Tails a list of files");
		System.out.println("Usage   : java toolbox.tail.Main [file1 file2 ... file8]");
		System.out.println("Example : java toolbox.tail.Main appserver.log");
	}
    
    public void nextLine(String line)
    {
        System.out.println(line);
    }
}