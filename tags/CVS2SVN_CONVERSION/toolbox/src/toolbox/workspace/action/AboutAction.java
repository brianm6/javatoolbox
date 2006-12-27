package toolbox.workspace.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import toolbox.util.ResourceUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.workspace.PluginWorkspace;

/**
 * Shows the About dialog box with version number and build date.
 */
public class AboutAction extends BaseAction
{
    private static final Logger logger_ = Logger.getLogger(AboutAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AboutAction.
     * 
     * @param workspace Plugin workspace.
     */
    public AboutAction(PluginWorkspace workspace)
    {
        super(workspace, "About");
    }

    //--------------------------------------------------------------------------
    // SmartAction Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        String message = "Java Toolbox";
        Properties p = new Properties();
        
        try
        {
            p.load(ResourceUtil.getResource("version.properties"));
        }
        catch(IOException ioe)
        {
            logger_.error(ioe);
        }
        
        if (!p.isEmpty())
        {
            message = 
                message 
                + " v. " 
                + p.getProperty("toolbox.version") 
                + " Build " 
                + p.getProperty("toolbox.build.number")
                + " Date "  
                + p.getProperty("toolbox.build.date");
        }
        
        JSmartOptionPane.showMessageDialog(
            getWorkspace(), 
            message, 
            "About", 
            JOptionPane.INFORMATION_MESSAGE, 
            ImageCache.getIcon(ImageCache.IMAGE_DUKE));
    }
}