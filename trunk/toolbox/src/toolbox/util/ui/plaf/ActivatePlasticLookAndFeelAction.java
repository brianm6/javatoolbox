package toolbox.util.ui.plaf;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticTheme;

import org.apache.log4j.Logger;

/**
 * Action that sets the plastic theme.
 */    
public class ActivatePlasticLookAndFeelAction extends ActivateLookAndFeelAction
{
    private static final Logger logger_ =
        Logger.getLogger(ActivatePlasticLookAndFeelAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ActivatePlasticLookAndFeelAction.
     */
    public ActivatePlasticLookAndFeelAction()
    {
    }
    
    /**
     * Creates an ActivatePlasticLookAndFeelAction.
     * 
     * @param theme Theme to activate.
     */
    public ActivatePlasticLookAndFeelAction(PlasticTheme theme)
    {
        //super(theme.getName(), false, null, null);
        //theme_ = theme;
    }

    //--------------------------------------------------------------------------
    // Overrides ActivateLookAndFeelAction
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#setLookAndFeelInfo(toolbox.util.ui.plaf.LAFInfo)
     */
    public void setLookAndFeelInfo(LAFInfo info)
    {
        super.setLookAndFeelInfo(info);
        setName(info.getName() + " - " + info.getProperty("theme.name"));
    }
    
    
    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#activate()
     */
    public void activate() throws Exception
    {
        LAFInfo info = getLookAndFeelInfo();
     
        String name  = info.getProperty("theme.name");
        String clazz = info.getProperty("theme.class");
        
        PlasticTheme theme = (PlasticTheme) Class.forName(clazz).newInstance();
        PlasticLookAndFeel.setMyCurrentTheme(theme);
        
        //UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        //LookAndFeelUtil.propagateChangeInLAF();
        
        super.activate();
    }
}