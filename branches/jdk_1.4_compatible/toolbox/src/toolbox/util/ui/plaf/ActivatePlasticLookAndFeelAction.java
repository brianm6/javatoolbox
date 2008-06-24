package toolbox.util.ui.plaf;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

/**
 * Action that activates the plastic look and feel.
 */    
public class ActivatePlasticLookAndFeelAction extends ActivateLookAndFeelAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ActivatePlasticLookAndFeelAction.
     */
    public ActivatePlasticLookAndFeelAction()
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
        //String name  = info.getProperty("theme.name");
        String clazz = info.getProperty("theme.class");
        PlasticTheme theme = (PlasticTheme) Class.forName(clazz).newInstance();
        PlasticLookAndFeel.setPlasticTheme(theme);
        super.activate();
    }
}