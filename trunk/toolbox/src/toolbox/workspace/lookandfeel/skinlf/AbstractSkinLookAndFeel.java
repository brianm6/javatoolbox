package toolbox.workspace.lookandfeel.skinlf;

import java.io.File;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;

import org.apache.log4j.Logger;

import toolbox.util.Banner;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.ResourceUtil;

/**
 * Abstract base class for SkinLF themes that need to mapped to a single
 * look and feel class. Allows 1-1 mapping between SkinLF theme and look
 * and feel class.
 */
public abstract class AbstractSkinLookAndFeel extends SkinLookAndFeel
{
    private static final Logger logger_ =
        Logger.getLogger(AbstractSkinLookAndFeel.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an abstract SkinLookAndFeel 
     */
    public AbstractSkinLookAndFeel()
    {
    }
    
    //--------------------------------------------------------------------------
    // Abstract
    //--------------------------------------------------------------------------

    /**
     * Returns the friendly name of the SkinLF theme
     * 
     * @return String
     */
    public abstract String getThemeName();

    /**
     * Returns the friendly name of the SkinLF theme zip file
     * 
     * @return String
     */
    public abstract String getThemeFile();


    //--------------------------------------------------------------------------
    // Overrides SkinLookAndFeel
    //--------------------------------------------------------------------------

    /**
     * @see com.l2fprod.gui.plaf.skin.SkinLookAndFeel#getName()
     */
    public String getName()
    {
        return super.getName() + " " +  getThemeName(); 
    }
    
    /**
     * @see com.l2fprod.gui.plaf.skin.SkinLookAndFeel#getDescription()
     */
    public String getDescription()
    {
        return super.getDescription() + " " + getThemeName();
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.swing.LookAndFeel
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.LookAndFeel#initialize()
     */
    public void initialize()
    {
        super.initialize();

        System.out.println("\n" + Banner.getBanner("Initialize..."));

        try
        {
            String tempFile = FileUtil.generateTempFilename();
            byte[] bytes = ResourceUtil.getResourceAsBytes(getThemeFile());

            FileUtil.setFileContents(tempFile, bytes, false);                    

            logger_.debug("bytes=" + bytes.length);
            logger_.debug("file=" + tempFile);

            File file = new File(tempFile);
            Skin skin = loadThemePack(file.toURL());
            setSkin(skin);
            file.deleteOnExit();
        }
        catch (Exception ex)
        {
            ExceptionUtil.handleUI(ex, logger_);
        }
    }
}