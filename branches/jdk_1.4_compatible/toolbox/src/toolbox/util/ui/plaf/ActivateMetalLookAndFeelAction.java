package toolbox.util.ui.plaf;

import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 * Action that activates the Metal look and feel and associated theme.
 */    
public class ActivateMetalLookAndFeelAction extends ActivateLookAndFeelAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ActivatePlasticLookAndFeelAction.
     */
    public ActivateMetalLookAndFeelAction()
    {
    }
    
    //--------------------------------------------------------------------------
    // Overrides ActivateLookAndFeelAction
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#setLookAndFeelInfo(
     *      toolbox.util.ui.plaf.LAFInfo)
     */
    public void setLookAndFeelInfo(LAFInfo info)
    {
        super.setLookAndFeelInfo(info);
        setName(info.getName() + " " + info.getProperty("theme.name"));
    }
    
    
    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#activate()
     */
    public void activate() throws Exception
    {
        LAFInfo info = getLookAndFeelInfo();
        String clazz = info.getProperty("theme.class");
        MetalTheme theme = (MetalTheme) Class.forName(clazz).newInstance();
        MetalLookAndFeel.setCurrentTheme(theme);
        super.activate();
    }
}