package toolbox.tree;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;

import toolbox.util.ArrayUtil;
import toolbox.util.StreamUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.DirectoryFilter; 

/**
 * Tree provides the equivalent functionality of the Window 2000 tree.exe
 * command.
 */
public class Tree
{
    private PrintWriter writer_;
    private FilenameFilter dirFilter_;
    
    private static final String SPACER   = "    ";
    private static final String BAR      = "|   ";
    private static final String JUNCTION = "+";
    private static final String ARM      = "---";
    
    /**
     * Entrypoint
     *
     * @param   args  TBD
     */
	public static void main(String args[]) throws Exception
	{
		Tree t = new Tree();
        t.showTree(new File("src/toolbox/tree/test"), "");
        Process p = Runtime.getRuntime().exec("tree.com /a src/toolbox/tree/test");
        InputStream is = p.getInputStream();
        System.out.println(StreamUtil.asString(is));
        
	}


    /**
     * Default constructor
     */
    public Tree()
    {
        dirFilter_ = new DirectoryFilter();
    }
    

    /**
     * Prints the directory tree to System.out starting from the given
     * root directory.
     * 
     * @param  rootDir  Root directory of the tree
     */
    public void showTree(File rootDir)
    {
        showTree(rootDir, new PrintWriter(System.out));
    }

    /**
     * Prints the directory tree to the given writer starting from the 
     * given root directory.
     * 
     * @param  rootDir  Root directory of the tree
     * @param  writer   Output sent to this writer
     */
    public void showTree(File rootDir, Writer writer)
    {
        writer_ = new PrintWriter(writer);
        showTree(rootDir, "");
        writer_.flush();
    }
    
    /**
     * Recurses the directory structure of the given rootDir and generates
     * a hierarchical representation.
     * 
     * @param  rootDir   Root diretory
     * @param  level     Current level of indentation
     */
	protected boolean showTree(File rootDir, String level)
	{
        boolean atRoot = (level.length() == 0);
        
        if (atRoot)
            writer_.println(rootDir.getAbsolutePath());
            
        // Get list of directories in root
		File[] files = rootDir.listFiles(dirFilter_);

        // Bow out if nothing todo
		if (ArrayUtil.isNullOrEmpty(files))
        {
            if (atRoot)
                writer_.println("No subfolders exist");
            return false;
        }

        int len = files.length; 

        // we know theres at least one child so go ahead and print a BAR
        if (atRoot)
            writer_.println(BAR);
            
        // Process each directory    
		for (int i=0; i<len; i++)
		{
			File current = files[i];

			writer_.print(level);
			writer_.print(JUNCTION);
			writer_.print(ARM);
  			writer_.print(current.getName());
            writer_.println();
            
            // Recurse            
            if (i == len-1 && len > 1)  
            {
                // At end and more then one dir
                showTree(current, level + SPACER);
            }
            else if (len > 1) 
            {
                // More than one dir
                showTree(current, level + BAR);                
                writer_.println(level + BAR);                   
            }
            else  
            {
                // Not at end                
                showTree(current, level + SPACER);
            }
		}
        
        return true;
	}
}