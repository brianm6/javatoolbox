package toolbox.util.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import toolbox.util.ResourceUtil;

/**
 * Unit test for ResourceUtilTest
 */
public class ResourceUtilTest extends TestCase
{
	/**
	 * Entrypoint
	 */
    public static void main(String[] args)
    {
        BasicConfigurator.configure();        
    	TestRunner.run(ResourceUtilTest.class);	
    }
	
    /**
     * Constructor for ResourceUtilTest
     */
    public ResourceUtilTest(String arg0)
    {
        super(arg0);
    }
    
    
    /**
     * Tests the exportToClass() method
     */
    public void testExportToClass() throws Exception
    {
		String treeOpen  = "images" + File.separator + "tree_open.gif";
		String treeClose = "images" + File.separator + "tree_close.gif";
		String cdrive    = "images" + File.separator + "cdrive.gif";
		
		ResourceUtil.exportToClass(treeOpen, "toolbox.util.ui", "TreeOpenGIF");    	
		ResourceUtil.exportToClass(treeClose, "toolbox.util.ui", "TreeCloseGIF");
		ResourceUtil.exportToClass(cdrive, "toolbox.util.ui", "HardDriveGIF");
    }
}

