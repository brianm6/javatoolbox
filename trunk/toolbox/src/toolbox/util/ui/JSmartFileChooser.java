package toolbox.util.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * JSmartFileChooser is responsible for _____.
 */
public class JSmartFileChooser extends JFileChooser implements IPreferenced
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    private static final String NODE_JFILECHOOSER = "JFileChooser";


    /**
     * Creates a JSmartFileChooser.
     */
    public JSmartFileChooser()
    {
    }


    /**
     * Creates a JSmartFileChooser.
     * 
     * @param currentDirectory
     */
    public JSmartFileChooser(File currentDirectory)
    {
        super(currentDirectory);
    }


    /**
     * Creates a JSmartFileChooser.
     * 
     * @param currentDirectoryPath
     */
    public JSmartFileChooser(String currentDirectoryPath)
    {
        super(currentDirectoryPath);
    }


    /**
     * Creates a JSmartFileChooser.
     * 
     * @param fsv
     */
    public JSmartFileChooser(FileSystemView fsv)
    {
        super(fsv);
    }


    /**
     * Creates a JSmartFileChooser.
     * 
     * @param currentDirectory
     * @param fsv
     */
    public JSmartFileChooser(File currentDirectory, FileSystemView fsv)
    {
        super(currentDirectory, fsv);
    }


    /**
     * Creates a JSmartFileChooser.
     * 
     * @param currentDirectoryPath
     * @param fsv
     */
    public JSmartFileChooser(String currentDirectoryPath, FileSystemView fsv)
    {
        super(currentDirectoryPath, fsv);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(prefs, NODE_JFILECHOOSER,
            new Element(NODE_JFILECHOOSER));
        
        setCurrentDirectory(new File(
            root.getAttributeValue("currentDirectory")));
        
        
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JFILECHOOSER);
        
        root.addAttribute(
            new Attribute(
                "currentDirectory", 
                getCurrentDirectory().getCanonicalPath()));
        

        XOMUtil.insertOrReplace(prefs, root);
    }    
}
