package toolbox.util.ui;

import java.awt.Graphics;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * JSmartFileChooser can remember the last chosen directory.
 */
public class JSmartFileChooser extends JFileChooser 
    implements IPreferenced, AntiAliased
{
    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    private static final String NODE_JFILECHOOSER = "JFileChooser";
    private static final String ATTR_LAST_DIRECTORY = "lastDirectory";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

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
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = XOMUtil.getFirstChildElement(prefs, NODE_JFILECHOOSER,
            new Element(NODE_JFILECHOOSER));
        
        setCurrentDirectory(new File(
            XOMUtil.getStringAttribute(
                root, ATTR_LAST_DIRECTORY, System.getProperty("user.home"))));
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        try
        {
            Element root = new Element(NODE_JFILECHOOSER);

            root.addAttribute(new Attribute(ATTR_LAST_DIRECTORY,
                getCurrentDirectory().getCanonicalPath()));

            XOMUtil.insertOrReplace(prefs, root);
        }
        catch (Exception e)
        {
            throw new PreferencedException(e);
        }
    }    
    
    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAliased()
     */
    public boolean isAntiAliased()
    {
        return antiAliased_;
    }


    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAliased(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAliased_ = b;
    }

    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}